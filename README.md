# Aplicaciones de Biometría y Medio Ambiente

Autora: Julia Valén de Oliveira.
Repositorio de entrega: https://github.com/Absdabsda/Proyecto-Biometria

## Prototipo actual

El firmware emite un valor ficticio **1234** en una trama iBeacon. La aplicación Android
reconoce el UUID del proyecto, interpreta el valor con signo y lo envía a un servidor PHP.
El servidor valida y guarda las medidas en MySQL/MariaDB y permite consultarlas.
La medida ficticia es deliberada en el Sprint 0. La pantalla web de consulta está implementada y utiliza un proxy separado.

## Estructura

| Ruta | Contenido |
| --- | --- |
| `author.md` | Autoría |
| `doc/` | Diseños y límites de cada componente |
| `context/` | Notación y formato del revisor facilitados en el ZIP |
| `src/business_logic/` | Validación, persistencia y consulta PHP |
| `src/communication/` | Adaptación de peticiones REST |
| `src/database/` | Esquema SQL canónico |
| `src/frontend_business_logic/` | Proxy web de consulta |
| `src/gui/` | Pantalla web de medidas |
| `test/` | Pruebas de persistencia y pantalla |
| `Prueba2025/` | Proyecto Android completo |
| `HolaMundoIBeacon/` | Firmware Arduino/Bluefruit |
| `servidor/` | Entrada pública, configuración de ejemplo y pruebas |
| `outputs/2026-10-16/` | Informe de revisión |

Los demás ejemplos docentes se conservan con sus atribuciones.

## Verificación reproducible

```sh
node --test servidor/tests/medidas.test.mjs test/gui.test.mjs
php servidor/tests/rest.php
python scripts/preparar_plesk.py
```

Abrir `Prueba2025` con Android Studio para compilar y ejecutar
las pruebas Android. Requiere SDK 34, JDK compatible con Gradle 8.7 y sus dependencias.
Las pruebas del parser no requieren Bluetooth; la prueba completa sí requiere placa y móvil.

El paquete generado es `output/plesk-servidor.zip`; seguir el README del servidor.
Los archivos públicos del proxy son copias generadas: editar `src/frontend_business_logic/medidas.mjs`
y regenerar el paquete. No subir contraseñas, configuración privada, APK ni carpetas temporales.

## Resultado del 16 de octubre de 2026

- 10 pruebas web superadas: 7 del proxy y 3 de la pantalla.
- 19 pruebas Android superadas con Gradle; APK debug generado desde una ruta sin tildes.
- Sintaxis PHP, pruebas REST y persistencia con MySQL real superadas en GitHub Actions.
- GET del servidor publicado responde con medidas 1234 ya almacenadas; no se insertaron datos.
- Firmware compilado para SparkFun Pro nRF52840 Mini con Adafruit nRF52 1.7.0; falta confirmar placa física y carga.

El sprint confirmado usa la medida ficticia 1234. Para la demostración, comprobar
en el móvil el valor recibido, el aviso de guardado y una fila nueva con el mismo
tipo y valor en el servidor. Los datos antiguos por sí solos no prueban un envío actual.
Consultar `doc/api.md`, los diseños y el informe de `outputs/2026-10-16/`.

## Aclaración del modelo mínimo viable

La autora confirmó que se exige tipo de medida. La tabla activa queda en medidaID, tipo, valor y fecha, sin UUID ni contador. La migración del alojamiento aún requiere acceso a phpMyAdmin y subida del backend coordinada; las consultas actuales siguen usando el esquema anterior. Ver bd/README.md y bd/migrar_mvp.sql.
