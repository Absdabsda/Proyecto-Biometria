# Revisión del Sprint 0 — Julia Valén de Oliveira

Fecha: 16/10/2026. Alcance: medida ficticia 1234 y modelo mínimo con tipo de medida.
Criterios: rúbrica aportada y documentos del revisor. No se garantiza una nota.

## Verificaciones aprobadas

- ✅ Autoría y proyecto: author.md, README, doc/, src/ y test/; instrucciones de ejecución y despliegue.
- ✅ Diseños: negocio, comunicación, BD, proxy web, GUI, Android y firmware; lenguaje y reglas de implementación definidos.
- ✅ Base mínima: la tabla activa medidas tiene solo medidaID, tipo, valor y fecha. No guarda UUID ni contador.
- ✅ Conservación: se compararon las 31 filas antes del cambio; IDs, tipos, valores y fechas coincidieron. Tras activarla siguen las 31 filas.
- ✅ Respaldo: dump SQL en pbio_privado/respaldo_sprint0_20261016_antes_mvp.sql.zip y archivo privado respaldo_codigo_sprint0_20261016.zip fuera de httpdocs. La tabla anterior queda como respaldo temporal.
- ✅ Negocio y API: comunicación llama a negocio; negocio depende de PDO y no del transporte. GET/POST y errores están documentados. El formato anterior del móvil se normaliza antes de persistir.
- ✅ Web: pantalla publicada en /ux/Aplicacion.html, proxy separado y estados de carga, vacío, error y recuperación. Muestra las 31 medidas reales existentes.
- ✅ Android: GUI, proxy y comunicación separados; guardarMedida devuelve la medida confirmada. Deduplicación, reintentos y cierre probados.
- ✅ Comentarios: cabeceras de descripción, diseño y parámetros en las funciones activas Android y firmware; se preservan las atribuciones docentes.
- ✅ Pruebas locales: 19 Android con Gradle y APK generado; 10 web superadas.
- ✅ CI: sintaxis PHP, REST, persistencia, migración y Android comprobados. La migración se verifica tanto con MySQL 8.4 como con MariaDB 10.11.
- ✅ Firmware: anuncia la medida ficticia 1234; compilado para SparkFun Pro nRF52840 Mini con Adafruit nRF52 1.7.0. La simulación es deliberada en este sprint.
- ✅ Alojamiento: backend actualizado, API con los cuatro campos y pantalla consultada correctamente. No se insertaron medidas de demostración desde el agente.

## Pendientes y límites

- ❌ Demostración física actual: falta observar una fila nueva procedente de placa y móvil en esta sesión.
  Acción: instalar el APK nuevo, recibir 1234, comprobar el aviso de guardado y una fila nueva con tipo, valor y fecha de hoy.
- ❌ Ubicación estricta de todos los componentes en src/: Android y firmware conservan sus carpetas originales, aunque los diseños las identifican.
  Acción: si se exige esa regla literalmente, migrar las carpetas y actualizar las rutas de compilación antes de volver a probar.
- ❌ Frecuencia histórica y GitFlow completo: hay ramas y commits reales; no se certifica la frecuencia de trabajo del equipo.
  Acción: explicar el historial auténtico en la revisión; no inventar commits ni evidencias.

## Límites de pruebas

La compilación no demuestra ejecución física ni calibración. El sensor real no es
parte de la demostración ficticia confirmada. Las 31 filas antiguas se conservaron,
pero no equivalen a una medida recibida hoy. Las pruebas de CI usan bases desechables.
