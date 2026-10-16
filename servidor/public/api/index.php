<?php
/**
 * Nombre: index.php
 * Descripción: Configuración o adaptador de despliegue y comprobación del Sprint 0.
 * Copyright: 2026 Julia Valén de Oliveira, solo aportaciones propias; se conservan derechos y atribuciones anteriores.
 * Fecha: 2026-10-16 (revisión; fechas originales conservadas si constan).
 * Autor: Julia Valén de Oliveira (aportaciones); base docente según atribuciones conservadas.
 * Aportación: Integración con las fuentes canónicas y documentación de la entrega.
 */

/** Entrada REST: configura el transporte y delega las operaciones al componente de comunicación. */
declare(strict_types=1);
ini_set('display_errors', '0');
header('Content-Type: application/json; charset=utf-8');
header('Cache-Control: no-store');
header('X-Content-Type-Options: nosniff');

// En Plesk, estos ficheros quedan fuera de httpdocs.
$privado = getenv('PBIO_PRIVATE_DIR') ?: dirname(__DIR__, 2) . '/pbio_privado';
$local = dirname(__DIR__, 2);
$raiz = is_file($privado . '/servidorREST/ReglasREST.php') ? $privado : $local;
if (!is_file($raiz . '/servidorREST/ReglasREST.php') || !is_file($raiz . '/logica/Logica.php')) {
    http_response_code(500);
    echo json_encode(['codigo' => 'CONFIGURACION_PENDIENTE', 'mensaje' => 'El servidor no está configurado.']);
    exit;
}
require_once $raiz . '/logica/Logica.php';
require_once $raiz . '/servidorREST/ReglasREST.php';

/** -------------------
 * Descripción: construye el componente de negocio con la configuración privada de persistencia.
 * Diseño: conectarLogica() --> logica:Logica
 * Parámetros: ninguno; la configuración procede del directorio privado del despliegue.
 * Pre: configuración privada y PDO MySQL. Post: conexión estricta en UTC.
 * Errores: configuración incompleta o conexión fallida.
 * ------------------- */
function conectarLogica(): Logica {
    global $raiz;
    $config = require $raiz . '/config.php';
    $bd = new PDO('mysql:host=' . $config['host'] . ';port=' . $config['port'] . ';dbname=' . $config['database'] . ';charset=utf8mb4',
        $config['user'], $config['password'], [PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION, PDO::ATTR_EMULATE_PREPARES => false]);
    $bd->exec("SET SESSION time_zone = '+00:00'");
    $bd->exec("SET SESSION sql_mode = 'STRICT_TRANS_TABLES,NO_ENGINE_SUBSTITUTION'");
    return new Logica($bd, $config['tipo_permitido'] ?? $config['codigo_o3'] ?? null);
}

// Algunos servidores entregan PATH_INFO vacío: la ruta explícita tiene prioridad.
$rutaRecibida = $_GET['ruta'] ?? ($_SERVER['PATH_INFO'] ?? '');
$ruta = is_string($rutaRecibida) ? '/' . trim($rutaRecibida, '/') : '/';
$cuerpo = file_get_contents('php://input', false, null, 0, 4097);
[$estado, $respuesta] = atenderPeticion($_SERVER['REQUEST_METHOD'], $ruta,
    $_SERVER['CONTENT_TYPE'] ?? '', $cuerpo === false ? '' : $cuerpo, $_GET, 'conectarLogica');
http_response_code($estado);
if ($estado === 405) { header('Allow: ' . ($ruta === '/medida' ? 'POST' : 'GET')); }
echo json_encode($respuesta, JSON_UNESCAPED_UNICODE | JSON_THROW_ON_ERROR);
