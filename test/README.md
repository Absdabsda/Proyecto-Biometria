# Pruebas del Sprint 0

Las pruebas del servidor con entradas simuladas están en `servidor/tests/rest.php`;
las del proxy web en `servidor/tests/medidas.test.mjs` y las del parser Android en
`src/android/Prueba2025/app/src/test/java/org/jordi/prueba2025/TramaIBeaconTest.java`.

`backend_integration.php` ejercita la lógica de negocio contra MySQL real usando
el esquema de `src/database/crear.sql`. Se ejecuta en GitHub Actions con una base
desechable: exige conectarse a `pbio_test`, crea `pbio_backend_test` para su esquema
y la elimina en finally. Las inserciones también se revierten. No altera tablas
ni secuencias AUTO_INCREMENT de la base original; no depende de medidas existentes.

La prueba física placa-móvil-servidor se documenta por separado: ninguna prueba
simulada sustituye la demostración con la placa y el teléfono.

`firmware_contract.cpp` incluye el Publicador y Medidor reales con una emisora
sustituida. Comprueba 15 combinaciones de valor y contador, UUID, tipo, signo,
espera y permanencia del anuncio; también el ejemplo de temperatura. GitHub
Actions lo compila y ejecuta con g++, sin radio ni esperas físicas.

`python scripts/verificar_revision.py` comprueba estructura, secciones de diseño,
modelo mínimo, separación de capas, copias generadas y cabeceras de declaraciones
nominales. Es una auditoría estática limitada; no interpreta toda la semántica.
