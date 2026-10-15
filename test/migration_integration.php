<?php
declare(strict_types=1);
$dsn = getenv('PBIO_TEST_DSN');
if (!$dsn || getenv('PBIO_TEST_DATABASE') !== 'pbio_test') {
    throw new RuntimeException('La migración se prueba solo en el servicio desechable de CI.');
}
$bd = new PDO($dsn, getenv('PBIO_TEST_USER') ?: 'root', getenv('PBIO_TEST_PASSWORD') ?: '', [PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION]);
if ($bd->query('SELECT DATABASE()')->fetchColumn() !== 'pbio_test') throw new RuntimeException('Base incorrecta.');
$creada = false;
try {
    $bd->exec('CREATE DATABASE pbio_migration_test');
    $creada = true;
    $bd->exec('USE pbio_migration_test');
    $bd->exec("SET SESSION time_zone = '+00:00'");
    $bd->exec('CREATE TABLE medidas (medidaID BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY, uuid VARCHAR(16) NOT NULL, gas TINYINT UNSIGNED NOT NULL, valor SMALLINT NOT NULL, contador TINYINT UNSIGNED NOT NULL, fecha TIMESTAMP(3) NOT NULL) ENGINE=InnoDB');
    $bd->exec("INSERT INTO medidas VALUES (31,'EPSG-GTI-PROY-3A',11,1234,93,'2026-10-08 22:28:50.690'),(32,'EPSG-GTI-PROY-3A',12,-12,94,'2026-10-08 22:28:51.123')");
    $archivo = getenv('PBIO_MIGRATION_FILE') ?: __DIR__ . '/../bd/migrar_mvp.sql';
    $sql = preg_replace('/^\s*--[^\n]*/m', '', file_get_contents($archivo));
    foreach (explode(';', $sql) as $sentencia) {
        $sentencia = trim($sentencia);
        if ($sentencia === '') continue;
        if (preg_match('/^SELECT\b/i', $sentencia)) { $bd->query($sentencia)->fetchAll(); }
        else { $bd->exec($sentencia); }
    }
    $columnas = $bd->query('SHOW COLUMNS FROM medidas')->fetchAll(PDO::FETCH_COLUMN);
    if ($columnas !== ['medidaID', 'tipo', 'valor', 'fecha']) throw new RuntimeException('La tabla activa no es mínima.');
    $filas = $bd->query('SELECT medidaID,tipo,valor,fecha FROM medidas ORDER BY medidaID')->fetchAll(PDO::FETCH_ASSOC);
    if (count($filas) !== 2 || (int) $filas[0]['medidaID'] !== 31 || (int) $filas[0]['valor'] !== 1234
        || (int) $filas[1]['tipo'] !== 12 || (int) $filas[1]['valor'] !== -12
        || $filas[0]['fecha'] !== '2026-10-08 22:28:50.690') throw new RuntimeException('La migración perdió datos.');
    if ((int) $bd->query('SELECT COUNT(*) FROM medidas_respaldo_anterior')->fetchColumn() !== 2) throw new RuntimeException('Falta respaldo.');
    echo "Migración MVP correcta: cuatro columnas, IDs/valores/fechas preservados y respaldo completo.\n";
} finally {
    $bd->exec('UNLOCK TABLES');
    if ($creada) {
        $bd->exec('USE pbio_test');
        $bd->exec('DROP DATABASE pbio_migration_test');
    }
}
