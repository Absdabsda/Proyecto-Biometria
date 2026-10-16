<?php
/**
 * Nombre: config.example.php
 * Descripción: Configuración o adaptador de despliegue y comprobación del Sprint 0.
 * Copyright: 2026 Julia Valén de Oliveira, solo aportaciones propias; se conservan derechos y atribuciones anteriores.
 * Fecha: 2026-10-16 (revisión; fechas originales conservadas si constan).
 * Autor: Julia Valén de Oliveira (aportaciones); base docente según atribuciones conservadas.
 * Aportación: Integración con las fuentes canónicas y documentación de la entrega.
 */

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
