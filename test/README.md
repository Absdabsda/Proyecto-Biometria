# Pruebas del Sprint 0

Las pruebas del servidor con entradas simuladas están en `servidor/tests/rest.php`;
las del proxy web en `servidor/tests/medidas.test.mjs` y las del parser Android en
`src/android/Prueba2025/app/src/test/java/org/jordi/prueba2025/TramaIBeaconTest.java`.

`backend_integration.php` ejercita la lógica de negocio contra MySQL real usando
el esquema de `src/database/crear.sql`. Se ejecuta en GitHub Actions con una base
desechable `pbio_test`. Inserta dentro de una transacción y revierte al terminar;
no depende de medidas existentes ni conserva filas de prueba.

La prueba física placa-móvil-servidor se documenta por separado: ninguna prueba
simulada sustituye la demostración con la placa y el teléfono.
