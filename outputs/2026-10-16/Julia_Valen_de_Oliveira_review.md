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
- ✅ Web: pantalla publicada en /ux/Aplicacion.html, proxy separado y estados de carga, vacío, error y recuperación. Paleta morada publicada; muestra medidas recientes recibidas de la prueba física.
- ✅ Android: GUI, proxy y comunicación separados; guardarMedida devuelve la medida confirmada. Deduplicación, reintentos y cierre probados.
- ✅ Comentarios del producto activo: cabeceras de descripción, diseño y parámetros en PHP, proxy, GUI, Android y firmware; clases y pruebas documentadas. Los ejemplos docentes están identificados como referencia y conservan sus atribuciones y comentarios originales.
- ✅ Pruebas locales: 19 Android con Gradle y APK generado; 10 web superadas.
- ✅ CI: sintaxis PHP, REST, persistencia, migración y Android comprobados. La migración se verifica tanto con MySQL 8.4 como con MariaDB 10.11.
- ✅ Firmware: anuncia la medida ficticia 1234; compilado para SparkFun Pro nRF52840 Mini con Adafruit nRF52 1.7.0. La simulación es deliberada en este sprint.
- ✅ Alojamiento: backend actualizado, API con los cuatro campos y pantalla consultada correctamente. No se insertaron medidas de demostración desde el agente.

- ✅ Ubicación: los componentes activos están en src/, incluidos src/android/Prueba2025 y src/firmware/HolaMundoIBeacon; rutas de documentación y CI actualizadas.
- ✅ Demostración física: Julia confirmó beacon y subida inmediata; la web mostró ID 81, tipo 11, valor 1234, 16/10/2026 01:53:27 Europe/Madrid. No se insertó esa fila desde el agente.

## Pendientes y límites

- ❌ Frecuencia histórica y GitFlow completo: hay ramas y commits reales; no se certifica la frecuencia de trabajo del equipo.
  Acción: explicar el historial auténtico en la revisión; no inventar commits ni evidencias.

## Límites de pruebas

La compilación por sí sola no demuestra ejecución física ni calibración; la prueba
física se registra por la confirmación de Julia y las filas nuevas observadas.
El sensor real no es parte de la demostración ficticia del Sprint 0. Las pruebas
de CI usan bases desechables. No se garantiza una nota ni la valoración del profesor.
