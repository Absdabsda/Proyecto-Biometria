<?php
// Copiar como config.php fuera de httpdocs. No subir la contraseña a GitHub.
return [
    'host' => 'localhost',
    'port' => 3306,
    'database' => 'jvaldeo_pbio',
    'user' => 'pbio_app',
    'password' => '',
    // Tipo del prototipo: 11. null admite tipos 0..255 sin atribuirles una identificación química.
    'tipo_permitido' => null,
];
