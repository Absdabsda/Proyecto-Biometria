<?php
// Copiar como config.php fuera de httpdocs. No subir la contraseña a GitHub.
return [
    'host' => 'localhost',
    'port' => 3306,
    'database' => 'jvaldeo_pbio',
    'user' => 'pbio_app',
    'password' => '',
    // Pendiente de confirmación del firmware. null admite códigos 0..255.
    'codigo_o3' => null,
];
