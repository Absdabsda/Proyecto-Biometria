<?php
declare(strict_types=1);
require_once __DIR__ . '/../src/business_logic/Logica.php';

/** -------------------
 * condicion:B, descripcion:Text --> verificar() --> ()
 * Descripción: detiene la prueba ante un incumplimiento.
 * Parámetros: condición calculada y explicación del resultado esperado.
 * ------------------- */
function verificar(bool $condicion, string $descripcion): void {
    if (!$condicion) { throw new RuntimeException($descripcion); }
}

/** -------------------
 * accion:Operacion --> rechazar() --> ()
 * Descripción: exige un error de dominio, sin aceptar fallos de conexión.
 * Parámetros: operación sobre datos deliberadamente inválidos.
 * ------------------- */
function rechazar(callable $accion): void {
    try { $accion(); } catch (InvalidArgumentException $error) { return; }
    throw new RuntimeException('Se aceptaron datos inválidos.');
}

$dsn = getenv('PBIO_TEST_DSN');
if (!$dsn || getenv('PBIO_TEST_DATABASE') !== 'pbio_test') {
    throw new RuntimeException('Usar únicamente una base desechable pbio_test y PBIO_TEST_DSN.');
}
$bd = new PDO($dsn, getenv('PBIO_TEST_USER') ?: 'root', getenv('PBIO_TEST_PASSWORD') ?: '', [
    PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
    PDO::ATTR_EMULATE_PREPARES => false,
]);
verificar($bd->query('SELECT DATABASE()')->fetchColumn() === 'pbio_test', 'Base de prueba incorrecta.');
$bd->exec("SET SESSION time_zone = '+00:00'");
$bd->exec("SET SESSION sql_mode = 'STRICT_TRANS_TABLES,NO_ENGINE_SUBSTITUTION'");
$esquema = file_get_contents(__DIR__ . '/../src/database/crear.sql');
$inicio = strpos($esquema, 'CREATE TABLE IF NOT EXISTS');
$fin = strpos($esquema, ') ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;');
$bd->exec(substr($esquema, $inicio, $fin - $inicio + strlen(') ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;')));
$bd->beginTransaction();
try {
    verificar($bd->query('SHOW COLUMNS FROM medidas')->fetchAll(PDO::FETCH_COLUMN) === ['medidaID', 'tipo', 'valor', 'fecha'], 'El esquema no es mínimo viable.');
    $logica = new Logica($bd);
    $entrada = ['tipo' => 11, 'valor' => 1234];
    $cantidadInicial = (int) $bd->query('SELECT COUNT(*) FROM medidas')->fetchColumn();
    $primera = $logica->guardarMedida($entrada);
    verificar($primera['valor'] === 1234 && is_string($primera['medidaID']), 'No conserva el valor o ID.');
    verificar(preg_match('/^\d{4}-\d{2}-\d{2}T.*\.\d{3}Z$/', $primera['fecha']) === 1, 'Fecha no UTC.');
    $segunda = $logica->guardarMedida(array_replace($entrada, ['valor' => -32768]));
    $tercera = $logica->guardarMedida(array_replace($entrada, ['valor' => 32767]));
    $lista = $logica->listarMedidas(2);
    verificar(count($lista) === 2, 'No aplica el límite.');
    verificar($lista[0]['medidaID'] === $tercera['medidaID'] && $lista[1]['medidaID'] === $segunda['medidaID'], 'Orden incorrecto.');
    foreach ([0, 101] as $limite) { rechazar(fn() => $logica->listarMedidas($limite)); }
    foreach ([['valor' => 32768], ['tipo' => -1], ['tipo' => 256], ['valor' => "' OR 1=1 --"], ['extra' => 1]] as $cambio) {
        rechazar(fn() => $logica->guardarMedida(array_replace($entrada, $cambio)));
    }
    rechazar(fn() => (new Logica($bd, 12))->guardarMedida($entrada));
    verificar((int) $bd->query('SELECT COUNT(*) FROM medidas')->fetchColumn() === $cantidadInicial + 3, 'Se insertó una medida inválida.');
    echo "Integración MySQL correcta: persistencia, rangos, orden, límites y esquema.\n";
} finally {
    $bd->rollBack();
}
verificar((int) $bd->query('SELECT COUNT(*) FROM medidas')->fetchColumn() === $cantidadInicial, 'La prueba dejó datos.');
