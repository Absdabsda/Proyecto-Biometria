# Aplicaciones de Biometría y Medio Ambiente — Sprint 0

Autora: Julia Valén de Oliveira.

La placa anuncia la medida ficticia **1234** mediante iBeacon. Android reconoce
el proyecto, interpreta tipo y valor y envía la medida al servidor. El backend PHP
la guarda en una base mínima de cuatro campos: **medidaID, tipo, valor y fecha**.
El UUID y el contador se utilizan localmente para BLE; no se guardan en la tabla activa.

## Aplicación publicada

- Web: https://jvaldeo.upv.edu.es/ux/Aplicacion.html
- API: https://jvaldeo.upv.edu.es/api/index.php?ruta=medidas&limit=50
- Contrato y compatibilidad: `doc/api.md`.

La web consume un proxy separado; la GUI no realiza peticiones de red directamente.
La API admite tanto {tipo, valor} como el anuncio anterior del móvil. Solo se pasan
tipo y valor a negocio. Los errores de validación y persistencia se tratan por separado.

## Estructura

| Ruta | Contenido |
| --- | --- |
| author.md | Autoría |
| doc/ | Diseños de componentes y API |
| context/ | Notación lógica y formato de tablas del revisor |
| src/business_logic/ | Validación y acceso SQL independiente del transporte |
| src/communication/ | Adaptación de la API REST |
| src/database/ | Esquema mínimo canónico |
| src/frontend_business_logic/ | Proxy de lectura web |
| src/gui/ | Pantalla web |
| test/ | Integración SQL, migración y estados de la pantalla |
| Prueba2025/ | Proyecto Android completo, con GUI, proxy y adaptador separados |
| HolaMundoIBeacon/ | Sketch Arduino y clases de apoyo |
| servidor/ | Entrada pública, despliegue y pruebas REST y web |
| bd/ | Instalación, prueba SQL y migración desde el esquema anterior |
| outputs/2026-10-16/ | Informe de revisión |

El material docente adicional se conserva con sus atribuciones. Android y Arduino
mantienen sus carpetas de proyecto originales, documentadas en sus diseños.

## Pruebas y despliegue

```sh
node --test servidor/tests/medidas.test.mjs test/gui.test.mjs
php servidor/tests/rest.php
python scripts/preparar_plesk.py
```

La integración SQL y la migración se ejecutan con bases desechables en GitHub Actions,
contra MySQL 8.4 y MariaDB 10.11. La configuración de pruebas está en el workflow.
Android Studio: abrir Prueba2025; para compilar y probar, usar Gradle 8.7 y SDK 34.
En este Windows hay que trabajar en una carpeta sin tildes para cargar sus pruebas.

El generador crea `output/plesk-servidor.zip`. Editar las fuentes de src/ y regenerar;
las copias públicas son generadas. Nunca publicar config.php, contraseñas ni APK en Git.
Seguir `servidor/README.md` y `bd/README.md` para una instalación nueva.

## Verificación del 16/10/2026

- 19 pruebas Android superadas con Gradle y APK debug generado.
- 10 pruebas web superadas, incluidos errores, vacío, recuperación y compatibilidad.
- Sintaxis PHP, adaptación REST, persistencia y migración comprobadas en CI.
- Firmware compilado para SparkFun Pro nRF52840 Mini, Adafruit nRF52 1.7.0; no se ha cargado en placa desde esta revisión.
- Plesk actualizado: tabla activa de cuatro columnas, 31 filas antiguas preservadas
  y comparadas por ID, tipo, valor y fecha; API y pantalla funcionando.
- Respaldo SQL y de archivos guardados fuera de httpdocs. La tabla anterior se conserva
  temporalmente como respaldo y no forma parte del modelo de la aplicación.

La demostración física actual placa-móvil-servidor debe comprobarse aparte con una
fila nueva de hoy; los registros antiguos no prueban una recepción actual.
Consultar `ENTREGA_SPRINT_0.md` para la prueba final.
