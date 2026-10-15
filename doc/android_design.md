# android

## Component Design

```text
Trama = (uuid: [N]_16, major: N, minor: N, tx_power: Z)
datos: [N] --> esIBeacon() --> B
datos: [N] --> TramaIBeacon() --> Trama
getValor() --> Z
getTipoMedicion() --> N
getContador() --> N
esNuestroDispositivo() --> B
trama: Trama --> enviarMedida()
cerrar()
```
Código existente: `Prueba2025/app/src/main/java/org/jordi/prueba2025/`.
Trama exacta de 23 bytes: 02 15, UUID 16, major 2, minor 2, potencia 1.
Tipo = major >> 8; contador = major & 255; valor = minor con signo de 16 bits.
Pantalla: botones buscar todos, buscar el proyecto y detener; muestra estado, RSSI y medida.
MainActivity gestiona permisos y BLE; delega envío a LogicaFake y PeticionarioREST.
Solo envía tipo 11. Deduplicación 60 s tras éxito, reintento mínimo 10 s, una petición en curso.


## Design Clarifications

- El proyecto Gradle conserva su ubicación para evitar romper Android Studio. Su adaptación completa a src/ está pendiente.
- El proxy Android todavía no comparte nombre y firma exactos con guardarMedida del servidor.
- No existe almacenamiento offline; cerrar no garantiza cancelar una inserción ya iniciada.
- Las cabeceras de todos los métodos Android aún necesitan adecuación formal.

## General Rules

- Programming Language: Java 8 y Android SDK 34.
- Function/Method Headers: incluir diseño lógico, precondiciones, poscondiciones y errores en un bloque delimitado por `-------------------`.
- Code Readability: nombres claros y código autoexplicativo; comentarios adicionales solo cuando aporten contexto.
- Automated Testing: generar pruebas unitarias o de integración para todas las funciones críticas, con límites y errores.
