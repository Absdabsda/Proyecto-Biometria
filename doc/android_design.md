# android

## Component Design

```text
MedidaEntrada = (tipo: N, valor: Z)
Medida = (medidaID: Text, tipo: N, valor: Z, fecha: Text)
Trama = (uuid: [N]_16, major: N, minor: N, tx_power: Z)

datos: [N] --> esIBeacon() --> B
datos: [N] --> TramaIBeacon() --> Trama
getMajor() --> N
getMinor() --> N
getValor() --> Z
getTipoMedicion() --> N
getContador() --> N
getTxPower() --> Z
getUUID() --> [N]_16
getDescripcionMedicion() --> Text
esNuestroDispositivo() --> B
trama: Trama --> desdeTrama() --> MedidaEntrada
entrada: MedidaEntrada --> guardarMedida() --> Medida
cerrar()
```

La firma de `guardarMedida` coincide con business_logic_design.md. En Java se
devuelve CompletableFuture<Medida>; la promesa representa la asincronía, no cambia
el resultado lógico. Medida agrupa los campos de entrada mediante `entrada`.

```text
              -------- LogicaFake --------------------
              | intentos: [Intento]
              | pendiente: Intento | Ausente
              | cerrada: B
              |
              |
entrada:MedidaEntrada --> guardarMedida() -->
medida:Medida  <--
              |
              |
              cerrar() -->
              ---------------------------------------
```

MainActivity posee el escáner, los textos y el proxy. TramaIBeacon interpreta
23 bytes de fabricante: 02 15, UUID 16, major 2, minor 2, potencia 1. Los métodos
de Utilidades combinan bytes big endian. MedidaEntrada representa tipo y valor; conserva adicionalmente metadatos locales de adquisición para filtrar y deduplicar BLE, que no se persisten;
Medida valida el ID textual positivo y la fecha UTC. LogicaFake coordina la cola
serial y deduplicación; PeticionarioREST implementa su interfaz Transporte,
envía el anuncio compatible con el servidor anterior y verifica estado 201, tipo/valor confirmados y respuesta <=4096 bytes. Acepta tipo del MVP o gas de la respuesta anterior; ID y fecha se reciben del servidor.
MainActivity no realiza peticiones de red ni conoce la base de datos.

Pantalla: botones buscar todos, buscar el proyecto y detener; muestra estado,
RSSI, tipo, valor y contador. Solicita permisos según versión, comprueba Bluetooth
y ubicación, ignora callbacks antiguos y detiene el escaneo en onStop.

Solo transmite tipo 11. Un anuncio repetido comparte la misma operación en curso.
Éxitos se reutilizan 60 s; fallos permiten un nuevo intento a los 10 s. Solo hay
una operación distinta en curso. Cerrar impide nuevos envíos y cancela el resultado
pendiente. Una inserción remota ya iniciada puede terminar; no hay cola offline ni
garantía de entrega exactamente una vez.

## Design Clarifications

- Código en `Prueba2025/app/src/main/java/org/jordi/prueba2025/`.
- Se conserva la ubicación del proyecto Android para facilitar su apertura en Android Studio.
- 19 pruebas Android automáticas: 14 del parser y 5 del proxy y los datos de dominio.
- En este Windows, Gradle necesita una ruta sin tildes para cargar correctamente sus pruebas.
- La API aloja la fecha de recepción; el móvil no inventa ID ni fecha de persistencia.

## General Rules

- Programming Language: Java 8 y Android SDK 34.
- Function/Method Headers: incluir diseño lógico, precondiciones, poscondiciones y errores en un bloque delimitado por `-------------------`.
- Code Readability: nombres claros y código autoexplicativo; comentarios adicionales solo cuando aporten contexto.
- Automated Testing: generar pruebas unitarias o de integración para todas las funciones críticas, con límites y errores.
