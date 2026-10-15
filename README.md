# Aplicaciones de Biometría y Medio Ambiente

Autora: Julia Valén de Oliveira.
Repositorio de entrega: https://github.com/Absdabsda/Proyecto-Biometria

## Prototipo actual

El firmware emite un valor ficticio **1234** en una trama iBeacon. La aplicación Android
reconoce el UUID del proyecto, interpreta el valor con signo y lo envía a un servidor PHP.
El servidor valida y guarda las medidas en MySQL/MariaDB y permite consultarlas.
La lectura de un sensor físico y la interfaz web de consulta no están terminadas.

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
| `Prueba2025/` | Proyecto Android completo |
| `HolaMundoIBeacon/` | Firmware Arduino/Bluefruit |
| `servidor/` | Entrada pública, configuración de ejemplo y pruebas |
| `outputs/2026-10-16/` | Informe de revisión |

Los demás ejemplos docentes se conservan con sus atribuciones.

## Verificación reproducible

```sh
node --test servidor/tests/medidas.test.mjs
php servidor/tests/rest.php
python scripts/preparar_plesk.py
```

Abrir `Prueba2025` con Android Studio para compilar y ejecutar
las pruebas Android. Requiere SDK 34, JDK compatible con Gradle 8.7 y sus dependencias.
Las pruebas del parser no requieren Bluetooth; la prueba completa sí requiere placa y móvil.

El paquete generado es `output/plesk-servidor.zip`; seguir el README del servidor.
Los archivos públicos del proxy son copias generadas: editar `src/frontend_business_logic/medidas.mjs`
y regenerar el paquete. No subir contraseñas, configuración privada, APK ni carpetas temporales.

## Resultado local del 16 de octubre de 2026

- 6 pruebas del cliente web superadas.
- 14 pruebas Java/JUnit del parser superadas mediante ejecución directa.
- PHP y base de datos no ejecutados en esta revisión: no hay PHP disponible en el entorno local.
- No se ha comprobado el recorrido físico BLE, el alojamiento ni la compilación completa Android.
- El workflow incluido comprueba PHP y JavaScript cuando se ejecuta en GitHub Actions.

La adecuación al sprint concreto requiere el enunciado: los adjuntos recibidos describen
el agente revisor, no los requisitos funcionales de la entrega.
