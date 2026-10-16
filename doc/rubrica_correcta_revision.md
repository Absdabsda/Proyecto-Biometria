# Revisión con la rúbrica oficial del Sprint 0

La rúbrica aportada el 16/10/2026 sustituye a la imagen anterior. Pondera buenas
prácticas 15 %, ingeniería inversa/diseño 20 %, arquitectura/negocio 30 %, tests
25 % y uso de IA 10 %. Además exige superar el recorrido físico completo.

Cambios de esta revisión:
- main se renombra a master conservando el historial; master es la rama predeterminada y develop la de integración.
- Se completan cabeceras de fichero con nombre, descripción, copyright, fecha de revisión, autoría y aportación.
- Se documentan aceptación por entregable, patrones de arquitectura, correspondencia de código/diseño y uso real de IA.
- Al recibir el enunciado de PoliformaT se preparan sus seis especificaciones de generación autocontenidas; Julia confirma que ya entregó el manuscrito de ingeniería inversa.
- Se refuerza la auditoría automática para detectar estos requisitos de documentación.
- Se mantiene la base mínima medidaID/tipo/valor/fecha, el valor ficticio 1234 y las rutas de apertura de Android/Arduino.

## Comparación de referencia

Se consultó JoanCataSend/PBIO-O3-GTI3A-2026, commit 7ac3a8270b8d598e486c95d57820ba741b7794a7.
Tiene master/develop, criterios de aceptación, prompts y trazabilidad. Son referencias
útiles de organización, no una garantía de cumplimiento. Su esquema de tres tablas,
tipos BLE y valores de ejemplo no son requisitos para este proyecto y no se adoptan.
Las aportaciones y la autoría de Julia se mantienen separadas de las de Joan.

## Verificación y límites

Las comprobaciones se ejecutan en CI: auditoría, PHP/REST, persistencia/migración
MySQL y MariaDB, web, Publicador C++ y Android. La prueba física de 1234 ya fue
confirmada por Julia y observada en la web; debe poder repetirse en la defensa.
La trazabilidad añadida identifica cuándo se escribieron los prompts y distingue
la revisión actual de la generación anterior; no se inventa un historial previo.
No se puede acreditar retroactivamente una frecuencia de commits. La entrega del
manuscrito se registra por confirmación de Julia, no por revisión del documento;
la entrega de las seis especificaciones en PoliformaT aún debe confirmarla ella.
