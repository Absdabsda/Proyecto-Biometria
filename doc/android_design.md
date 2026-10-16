# android

## Component Design

```text
MedidaEntrada = (tipo: N, valor: Z)
Medida = (medida_id: Text, tipo: N, valor: Z, fecha: Text)
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
                                                                                    -------- LogicaFake ----------------------------
                                                                                    | red: EjecutorSerial
                                                                                    | reloj: Reloj
                                                                                    | transporte: PersistenciaRemota
                                                                                    | intentos: [Intento]
                                                                                    | pendiente: Intento | Ausente
                                                                                    | cerrada: B
                                                                                    | entrada:MedidaEntrada, intento:Intento --> ejecutar() -->
                                                                                    | error:Error --> fallo() --x
                                                                                    | resultado:Medida|Error|Pendiente <--
                                                                                    |
                                                                                    |
                                                             destino:Text --> LogicaFake() -->
                                                                                    |
                                                                                    |
           transporte:PersistenciaRemota, reloj:Reloj, red:EjecutorSerial --> LogicaFake() -->
                                                                                    |
                                                                                    |
                                                   entrada:MedidaEntrada --> guardarMedida() -->
                                                                  medida:Medida <--
                                                                                    |
                                                                                    |
                                                                                cerrar() -->
                                                                                    -----------------------------------------------
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

### Clases de dominio y adaptadores

Los tipos MedidaEntrada y Medida anteriores describen el intercambio con el
backend. El objeto local MedidaEntrada conserva además uuid:Text, gas:N y
contador:N; gas es un alias de tipo. Medida agrupa tipo y valor mediante entrada.
Estos campos públicos finales son datos inmutables, no estado privado.

```text
Intento = (resultado: Medida | Error | Pendiente, instante: N, exito: B)
Error = (mensaje: Text)
PersistenciaRemota = (destino: Text)
Reloj = (instante_monotono: N)
EjecutorSerial = (ocupado: B)
```

```text
                                                                                    -------- MedidaEntrada ----------------------------
                                                                                    |
                                                                                    |
                                   uuid:Text, gas:N, valor:Z, contador:N --> MedidaEntrada() -->
                                                                                    |
                                                                                    |
                                                         tipo:N, valor:Z --> MedidaEntrada() -->
                                                                                    |
                                                                                    |
                                                              trama:Trama --> desdeTrama() --x
                                                          entrada:MedidaEntrada <--
                                                                                    |
                                                                                    |
                                       tipo:N, valor:Z, uuid:Text, gas:N, contador:N [publico]
                                                                                    -----------------------------------------------
```
```text
                                                                                    -------- Medida ----------------------------
                                                                                    |
                                                                                    |
                      identificador:Text, entrada:MedidaEntrada, fecha:Text --> Medida() -->
                                                                                    |
                                                                                    |
                                   medida_id:Text, entrada:MedidaEntrada, fecha:Text [publico]
                                                                                    -----------------------------------------------
```
```text
                                                                                    -------- TramaIBeacon ----------------------------
                                                                                    | uuid_proyecto: [N]_16 (constante compartida)
                                                                                    | uuid: [N]_16
                                                                                    | major: N
                                                                                    | minor: N
                                                                                    | potencia: Z
                                                                                    |
                                                                                    |
                                                                 datos:[N] --> esIBeacon() --x
                                                                       valido:B <--
                                                                                    |
                                                                                    |
                                                               datos:[N] --> TramaIBeacon() -->
                                                                                    |
                                                                                    |
                                                                         esNuestroDispositivo() <--
                                                                      nuestro:B <--
                                                                                    |
                                                                                    |
                                                                               getMajor() <--
                                                                        major:N <--
                                                                                    |
                                                                                    |
                                                                               getMinor() <--
                                                                        minor:N <--
                                                                                    |
                                                                                    |
                                                                               getValor() <--
                                                                        valor:Z <--
                                                                                    |
                                                                                    |
                                                                            getTipoMedicion() <--
                                                                         tipo:N <--
                                                                                    |
                                                                                    |
                                                                              getContador() <--
                                                                     contador:N <--
                                                                                    |
                                                                                    |
                                                                              getTxPower() <--
                                                                     potencia:Z <--
                                                                                    |
                                                                                    |
                                                                                getUUID() <--
                                                                    uuid:[N]_16 <--
                                                                                    |
                                                                                    |
                                                                        getDescripcionMedicion() <--
                                                               descripcion:Text <--
                                                                                    |
                                                                                    |
                                                            fabricante_apple: N = 76 [publico]
                                                                                    -----------------------------------------------
```
```text
                                                                                    -------- Utilidades ----------------------------
                                                                                    | Utilidades() --> (constructor privado)
                                                                                    |
                                                                                    |
                                                                datos:[N] --> bytesToInt() --x
                                                                        valor:Z <--
                                                                                    -----------------------------------------------
```
```text
                                                                                    -------- PeticionarioREST ----------------------------
                                                                                    | destino: Text
                                                                                    |
                                                                                    |
                                                          destino:Text --> PeticionarioREST() -->
                                                                                    |
                                                                                    |
                                                   entrada:MedidaEntrada --> guardarMedida() -->
                                                                  medida:Medida <--
                                                                                    -----------------------------------------------
```
```text
                                                                                    -------- Transporte ----------------------------
                                                                                    |
                                                                                    |
                                                   entrada:MedidaEntrada --> guardarMedida() -->
                                                                  medida:Medida <--
                                                                                    -----------------------------------------------
```
```text
                                                                                    -------- Intento (privada de LogicaFake) ----------------------------
                                                                                    |
                                                                                    |
                                                                 instante:N --> Intento() -->
                                                                                    |
                                                                                    |
                               resultado:Medida|Error|Pendiente, instante:N, exito:B [publico]
                                                                                    -----------------------------------------------
```
LogicaFake posee además transporte:PersistenciaRemota y las dependencias de
ejecución serial y reloj. Sus auxiliares privados ejecutar y fallo resuelven la
operación o representan el error. Intento es privado de LogicaFake: constructor
instante:N y campos resultado, instante y exito. Las dependencias de pruebas
del constructor no forman parte de la firma de dominio guardarMedida.
MainActivity y sus eventos son la GUI Android: su diseño de pantalla se describe
arriba; la revisión formal de la GUI queda fuera del alcance del agente aportado.


### Pantalla Android y eventos de plataforma

La rúbrica oficial pide describir la arquitectura de todo el código activo; por
eso se documenta también MainActivity aunque el agente excluya la verificación
formal de GUI. Los tipos de Android se abstraen como datos y sesiones lógicas:

```text
EstadoGuardado = (restaurada: B)
Control = { BUSCAR_TODOS, BUSCAR_PROYECTO, DETENER }
AnuncioBLE = (direccion: Text, nombre: Text, rssi: Z, fabricante: N, datos: [N])
SesionBusqueda = (activa: B, solo_proyecto: B)
DispositivoVista = (direccion: Text, descripcion: Text)
EstadoPantalla = (resultados: Text, estado: Text, envio: Text)

                                                          -------- MainActivity ----------------------
                                                          | escaner: SesionBusqueda | Ausente
                                                          | receptor: SesionBusqueda | Ausente
                                                          | pantalla: EstadoPantalla
                                                          | logica_fake: LogicaFake
                                                          | solo_nuestro: B
                                                          | actualizacion_pendiente: B
                                                          | dispositivos: [DispositivoVista]
                                                          | cola_pantalla: EjecutorSerial
                                                          | solo_nuestro:B --> iniciarBusqueda() -->
                                                          | sesion:SesionBusqueda, anuncio:AnuncioBLE --> recibirResultado() -->
                                                          | anuncio:AnuncioBLE --> mostrarDispositivo() -->
                                                          | prepararEscanner() <-- preparado:B
                                                          | permisosNecesarios() <-- permisos:[Text]
                                                          | tengoPermisos() <-- concedidos:B
                                                          | pedirPermisos() -->
                                                          | detenerBusqueda() -->
                                                          | actualizarPantalla() -->
                                                          | estado:EstadoGuardado|Ausente --> onCreate() -->
                                                          | onStop() -->
                                                          | onDestroy() -->
                                                          |
                                                          |
                                    control:Control --> botonBuscarDispositivosBTLEPulsado() -->
                                                          |
                                                          |
                                    control:Control --> botonBuscarNuestroDispositivoBTLEPulsado() -->
                                                          |
                                                          |
                                    control:Control --> botonDetenerBusquedaDispositivosBTLEPulsado() -->
                                                          |
                                                          |
    codigo:Z, permisos:[Text], resultados:[Z] --> onRequestPermissionsResult() -->
                                                          ---------------------------------------------

                                            -------- ReceptorEscaneo (anonimo) --------
                                            |
                                            |
         tipo:Z, anuncio:AnuncioBLE --> onScanResult() -->
                                            |
                                            |
                   anuncios:[AnuncioBLE] --> onBatchScanResults() -->
                                            |
                                            |
                                 codigo:Z --> onScanFailed() -->
                                            ---------------------------------------------
```

onCreate/onStop/onDestroy son eventos protegidos de la plataforma; se mantienen
dentro del bloque. El receptor anónimo pertenece a una sesión y descarta eventos
de sesiones anteriores. actualizarPantalla es una operación diferida, implementada
mediante Runnable; acumula anuncios durante 500 ms y limita la lista a 100 equipos.
Los controles son eventos sin datos de dominio adicionales; View/Bundle, callbacks
y Handler son mecanismos de implementación, no tipos de las interfaces de negocio.

## Design Clarifications

- bytesToInt conserva el patrón de hasta cuatro bytes en Z; con uno o dos bytes representa N.

- Código en `src/android/Prueba2025/app/src/main/java/org/jordi/prueba2025/`.
- Abrir src/android/Prueba2025 en Android Studio; el traslado no cambia el paquete ni la aplicación instalada.
- 19 pruebas Android automáticas: 14 del parser y 5 del proxy y los datos de dominio.
- En este Windows, Gradle necesita una ruta sin tildes para cargar correctamente sus pruebas.
- La API aloja la fecha de recepción; el móvil no inventa ID ni fecha de persistencia.

## General Rules

- Programming Language: Java 8 y Android SDK 34.
- Function/Method Headers: incluir descripción, diseño lógico y parámetros; indicar precondiciones, resultados y errores relevantes en un bloque delimitado por `-------------------`.
- Code Readability: nombres claros y código autoexplicativo; comentarios adicionales solo cuando aporten contexto.
- Automated Testing: generar pruebas unitarias o de integración para todas las funciones críticas, con límites y errores.
