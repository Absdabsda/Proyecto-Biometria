# Especificación: Generar la lógica fake en el teléfono

Autora: Julia Valén de Oliveira. Fecha: 16/10/2026.

Documento de generación preparado para la entrega a partir del diseño vigente.
No acredita que este fuera el prompt usado en la implementación inicial.

## Objetivo y rutas

Genera el proxy Java del teléfono en el paquete org.jordi.prueba2025 dentro de
src/android/Prueba2025/app/src/main/java/org/jordi/prueba2025/. Archivos:
LogicaFake.java, PeticionarioREST.java, MedidaEntrada.java y Medida.java.
MainActivity consume el proxy; no implementes red en la GUI ni cambies el escáner.

## Interfaz lógica utilizada por la GUI

```text
MedidaEntrada = (tipo: N, valor: Z)
Medida = (medidaID: Text, tipo: N, valor: Z, fecha: Text)
entrada: MedidaEntrada --> guardarMedida() --> medida: Medida

GUI/BLE --> LogicaFake --> Transporte/PeticionarioREST --> servidor
GUI/BLE <-- medida confirmada <-- LogicaFake <-- respuesta validada
```

## Aclaraciones concretas

- La firma lógica de guardarMedida coincide con negocio. CompletableFuture expresa
asincronía, sin añadir mecanismos de transporte al contrato lógico.
- MedidaEntrada conserva adicionalmente uuid/gas/contador locales para filtrar y
deduplicar BLE; gas es alias de tipo. No son columnas de persistencia.
- Medida conserva ID decimal positivo como Text, entrada y fecha válida UTC.
- PeticionarioREST usa HTTPS a https://jvaldeo.upv.edu.es/api/index.php?ruta=medida,
POST JSON compatible, estado 201, tiempos de espera y máximo 4096 bytes de respuesta.
Acepta tipo MVP o gas legado en la respuesta y comprueba tipo/valor confirmados.
- LogicaFake inyecta Transporte, reloj monotónico y ejecutor serial para pruebas.
Un anuncio idéntico en curso comparte resultado. Reutiliza éxitos durante 60 s;
fallos se pueden reintentar tras 10 s. No admite dos medidas distintas en curso.
- cerrar impide envíos nuevos y cancela el resultado pendiente; una inserción
remota ya iniciada puede terminar. No prometas entrega exactamente una vez ni cola offline.
- Mantén el filtrado del UUID y tipo 11 en Android, fuera de la lógica del servidor.


## Diseño formal incorporado

# frontend_business_logic

## Component Design

```text
MedidaEntrada = (tipo: N, valor: Z)
Medida = (medida_id: Text, tipo: N, valor: Z, fecha: Text)
```
tipo 0..255; valor -32768..32767. ID decimal positivo como texto y fecha ISO 8601 UTC.

```text
limit: N --> listarMedidas() --> medidas: [Medida]
entrada: MedidaEntrada --> guardarMedida() --> medida: Medida
```
Las firmas coinciden con los subconjuntos del backend. La web usa listarMedidas:
valida límite 1..100, consulta, verifica ID, tipo, valor y fecha y devuelve solo
esos cuatro campos. Android usa guardarMedida mediante LogicaFake, cuya fuente
está en src/android/Prueba2025/app/src/main/java/org/jordi/prueba2025/.
Los metadatos BLE locales sirven para deduplicar; no cambian la medida persistida.
android_design.md detalla el adaptador, la ejecución serial, el reloj y los reintentos.
Errores: límite, conexión, estado incorrecto o respuesta inválida.
Implementación `src/frontend_business_logic/medidas.mjs`; GUI en src/gui/.

## Design Clarifications

- Mientras el alojamiento se actualiza, gas de la respuesta antigua se normaliza a tipo.
- No se devuelven UUID ni contador a la GUI, aunque el servidor antiguo los envíe.
- Asincronía e inyección del cliente de pruebas son detalles de implementación.
- obtenerMedidas es un alias de compatibilidad de listarMedidas.

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

## Directrices de implementación y revisión

Implementa lo diseñado en las rutas indicadas. No cambies identificadores, nombres
de las operaciones ni rangos del dominio para adaptarlos a otra arquitectura.
La medida del Sprint 0 es ficticia: no atribuyas calibración ni lectura física.

- Cabecera de cada fichero propio/adaptado: Nombre, Descripción, Copyright limitado
a aportaciones propias, Fecha real, Autor y Aportación. Autora: Julia Valén de Oliveira.
Conserva las atribuciones de Jordi Bataller y demás material original cuando existan.
- Cada función lleva un bloque separado por ------------------- con firma lógica
en la notación oficial, descripción breve, parámetros y errores relevantes.
- Usa N, Z, R, B, Text, agregaciones y colecciones para diseñar; los detalles del
lenguaje, referencias, hilos y callbacks pertenecen a la implementación.
- Usa nombres claros y pocos comentarios internos, salvo decisiones que requieran explicación.
- Escribe pruebas automáticas independientes, con datos propios y limpieza; cubre
casos válidos, límites y errores. No uses credenciales reales ni la base de Plesk.
- Revisa el resultado frente a cada firma y criterio. Indica qué has comprobado y
qué no se puede verificar sin hardware. Nunca sustituyas una prueba real por una afirmación.
- Mantén las rutas de apertura de los IDE y evita copias divergentes. No reescribas
otros componentes ni regeneres todo el proyecto para corregir un fichero.


## Tests y aceptación

Usa transporte y reloj controlados, sin red ni pausas temporales arbitrarias.
Comprueba valor/ID grande confirmados, deduplicación, expiración 60 s, fallo y
reintento 10 s, operación en curso, cierre y límites de datos. Conserva las 14
pruebas del parser y las 5 del proxy/dominio. Compila APK y ejecuta Gradle test.
El test físico de recepción y permisos se registra por separado.
