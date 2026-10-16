# Criterios de aceptación del Sprint 0

## Matriz de entregables

| Entregable | Criterio reproducible | Comprobación |
| --- | --- | --- |
| firmware | Publica UUID del proyecto, tipo 11, contador y valor 1234; conserva el signo y mantiene el anuncio | test/firmware_contract.cpp: 15 combinaciones de valor/contador; compilación completa y prueba física |
| android | Reconoce los 16 bytes del UUID, interpreta major/minor, muestra 1234 y delega en LogicaFake | TramaIBeaconTest y LogicaFakeTest: 19 pruebas; dispositivo físico para permisos/radio |
| business_logic | Valida exclusivamente tipo/valor, persiste cuatro campos y lista por fecha/ID descendentes | servidor/tests/rest.php y test/backend_integration.php |
| database | Exactamente medidaID, tipo, valor y fecha; migración conserva datos y respaldo | test/backend_integration.php y test/migration_integration.php en MySQL/MariaDB |
| communication | POST válido produce 201 y medida confirmada; GET lista medidas; entradas inválidas se rechazan | servidor/tests/rest.php; integración CI con persistencia real |
| frontend_business_logic | Mantiene la firma de listarMedidas y valida límite, tipo, valor, fecha e ID textual | servidor/tests/medidas.test.mjs |
| gui | La primera fila corresponde a la última medida almacenada; presenta vacío/error/recuperación sin comunicación directa | test/gui.test.mjs y orden comprobado en negocio |
| Repositorio/documentación | Componentes/diseños coinciden, hay cabeceras y trazabilidad, master/develop existen | scripts/verificar_revision.py y comprobación de ramas en GitHub |

## Ejecución automática

Desde la raíz:
```sh
python scripts/preparar_plesk.py
python scripts/verificar_revision.py
node --test servidor/tests/medidas.test.mjs test/gui.test.mjs
php servidor/tests/rest.php
```
Las pruebas SQL requieren las bases desechables de .github/workflows/verificar.yml;
no se ejecutan contra Plesk. Android se prueba abriendo src/android/Prueba2025,
con :app:assembleDebug y :app:testDebugUnitTest. En Windows usar una ruta sin tildes
para la ejecución de pruebas. El contrato C++ se compila con g++ como indica CI.

## Test de funcionamiento obligatorio

1. Confirmar MEDIDA_DE_PRUEBA=1234 en src/firmware/HolaMundoIBeacon/HolaMundoIBeacon.ino.
2. Encender la placa con ese firmware; teléfono con Bluetooth y permisos habilitados.
3. Abrir Android y buscar nuestro dispositivo: deben verse tipo 11 y valor 1234.
4. Esperar el mensaje de guardado correcto en el servidor.
5. Abrir https://jvaldeo.upv.edu.es/ux/Aplicacion.html y pulsar Actualizar.
6. Comprobar que la primera fila tiene 1234, tipo 11, ID nuevo y fecha actual.
7. Si no aparece, o cambia el valor en una capa, el test falla aunque compilen los proyectos.

La web consulta hasta 50 filas ordenadas de más reciente a más antigua; la primera
es la última medición. No se interpreta 1234 como lectura calibrada de un sensor.
Para demostrar que no es un valor fijo en la web se puede cambiar la constante de
placa a otro entero de 16 bits, repetir el recorrido y restaurar 1234 al terminar.

## Evidencias existentes

Prueba realizada por Julia el 16/10/2026: beacon encontrado y subidas inmediatas.
La web mostró ID 81, tipo 11, valor 1234, 01:53:27 Europe/Madrid. Una lectura
posterior de API devolvió ID 270 con el mismo tipo/valor y fecha 00:13:55.011Z.
La captura pública disponible está en doc/evidencias/web-morada-publicada.jpg.
La captura no sustituye la demostración presencial ni acredita permisos/radio por sí sola.
