# firmware

## Component Design

```text
setup()
loop()
iniciarMedidor()
medirCO2() --> Z
medirTemperatura() --> Z
valor: Z, contador: N, tiempo_espera: Z --> publicarCO2()
valor: Z, contador: N, tiempo_espera: Z --> publicarTemperatura()
```
Código: `src/firmware/HolaMundoIBeacon/`.
El loop activo emite MEDIDA_DE_PRUEBA=1234; no llama a Medidor para adquirir el gas. Medidor conserva constantes de ejemplo 235 y -12. Es un simulador, no una adquisición real del sensor.
Publicador emite UUID EPSG-GTI-PROY-3A, tipo 11 o 12 en byte alto de major,
contador en byte bajo, valor en minor y referencia -53.

| Clase | Estado y operaciones |
| --- | --- |
| Publicador | UUID fijo y EmisoraBLE; encenderEmisora, publicarCO2, publicarTemperatura |
| EmisoraBLE | Nombre, fabricante y potencia; iniciar/detener anuncios, emitir iBeacon, añadir servicios y registrar eventos |
| Medidor | Sin estado físico; iniciarMedidor y constantes de ejemplo |
| LED | Número de pin y estado; encender, apagar, alternar, brillar |
| PuertoSerie | Puerto serie de plataforma, sin campos propios; esperarDisponible y escribir |
| ServicioEnEmisora | UUID y colección de características; añadir, activar y mostrar UUID |
| Caracteristica | UUID y característica Bluefruit; propiedades, permisos, longitud, escritura y notificación |

setup activa Publicador y Medidor. loop incrementa un contador de 8 bits, ejecuta
el patrón LED y publica 1234 durante 3000 ms. El anuncio continúa entre iteraciones.
El bloque de temperatura está desactivado con `#if 0`. Los helpers de servicios
se conservan como material de apoyo, pero el recorrido activo usa anuncios iBeacon.


### Clases y contratos completos

Los bloques siguientes representan el código conservado, incluidos los servicios
de apoyo que no usa la demostración iBeacon. Los miembros privados permanecen
dentro del bloque; dos líneas verticales separan las operaciones públicas.
`-->` indica modificación o efectos, `<--` consulta y `--x` operación estática.
Las referencias y plantillas de C++ se omiten en las firmas lógicas.

```text
Elemento = N | Z | R | B | Text
ModoPermiso = (modo: N, nivel: N)
EventoConexion = (identificador: N)
EventoDesconexion = (identificador: N, motivo: N)
EventoEscritura = (identificador: N, datos: [N])
ConexionBLE = (identificador: N)
ServicioBLE = (uuid: [N]_16)
CaracteristicaBLE = (uuid: [N]_16)
MedicionesID = { CO2, TEMPERATURA, RUIDO }
```

ModoPermiso abstrae los dos valores del permiso de Bluefruit; ServicioBLE,
CaracteristicaBLE y ConexionBLE son entidades del adaptador, no punteros del diseño.
Los eventos identifican el contrato observable; registrar callbacks es el mecanismo
de implementación. El parámetro de espera es Z por la firma conservada; el uso
válido exige milisegundos no negativos. Las cantidades y contadores caben en los
rangos especificados por las cabeceras; el contador de adquisición vuelve de 255 a 0.

```text
                                                                                    -------- Publicador ----------------------------
                                                                                    | beacon_uuid: [N]_16
                                                                                    |
                                                                                    |
                                                                              Publicador() -->
                                                                                    |
                                                                                    |
                                                                            encenderEmisora() -->
                                                                                    |
                                                                                    |
                                     valor:Z, contador:N, tiempo_espera:Z --> publicarCO2() -->
                                                                                    |
                                                                                    |
                                 valor:Z, contador:N, tiempo_espera:Z --> publicarTemperatura() -->
                                                                                    |
                                                                                    |
                                                              la_emisora: EmisoraBLE [publico]
                                                                                    |
                                                                                    |
                                                                       rssi: Z = -53 [publico]
                                                                                    -----------------------------------------------
```
```text
                                                                                                         -------- EmisoraBLE ----------------------------
                                                                                                         | nombre_emisora: Text
                                                                                                         | fabricante_id: N
                                                                                                         | potencia: Z
                                                                                                         |
                                                                                                         |
                                                         nombre:Text, fabricante:N, potencia:Z --> EmisoraBLE() -->
                                                                                                         |
                                                                                                         |
                                                                                                 encenderEmisora() -->
                                                                                                         |
                                                                                                         |
                                      conexion:EventoConexion, desconexion:EventoDesconexion --> encenderEmisora() -->
                                                                                                         |
                                                                                                         |
                                                                                                 detenerAnuncio() -->
                                                                                                         |
                                                                                                         |
                                                                                                 estaAnunciando() <--
                                                                                        anunciando:B <--
                                                                                                         |
                                                                                                         |
                                                    uuid:[N]_16, major:N, minor:Z, rssi:Z --> emitirAnuncioIBeacon() -->
                                                                                                         |
                                                                                                         |
                                                                  carga:Text, tamanyo:N --> emitirAnuncioIBeaconLibre() -->
                                                                                                         |
                                                                                                         |
                                                                  servicio:ServicioEnEmisora --> anyadirServicio() -->
                                                                                          aceptado:B <--
                                                                                                         |
                                                                                                         |
                                                       servicio:ServicioEnEmisora --> anyadirServicioConSusCaracteristicas() -->
                                                                                          aceptado:B <--
                                                                                                         |
                                                                                                         |
servicio:ServicioEnEmisora, caracteristica:Caracteristica, resto:[Caracteristica] --> anyadirServicioConSusCaracteristicas() -->
                                                                                          aceptado:B <--
                                                                                                         |
                                                                                                         |
                 servicio:ServicioEnEmisora, caracteristicas:[Caracteristica] --> anyadirServicioConSusCaracteristicasYActivar() -->
                                                                                          aceptado:B <--
                                                                                                         |
                                                                                                         |
                                                             evento:EventoConexion --> instalarCallbackConexionEstablecida() -->
                                                                                                         |
                                                                                                         |
                                                           evento:EventoDesconexion --> instalarCallbackConexionTerminada() -->
                                                                                                         |
                                                                                                         |
                                                                               identificador:N --> getConexion() <--
                                                                        conexion:ConexionBLE|Ausente <--
                                                                                                         -----------------------------------------------
```
```text
                                                                                    -------- Medidor ----------------------------
                                                                                    | sin estado de adquisicion fisica
                                                                                    |
                                                                                    |
                                                                                Medidor() -->
                                                                                    |
                                                                                    |
                                                                            iniciarMedidor() -->
                                                                                    |
                                                                                    |
                                                                               medirCO2() <--
                                                                        valor:Z <--
                                                                                    |
                                                                                    |
                                                                           medirTemperatura() <--
                                                                        valor:Z <--
                                                                                    -----------------------------------------------
```
```text
                                                                                    -------- LED ----------------------------
                                                                                    | numero_led: Z
                                                                                    | encendido: B
                                                                                    |
                                                                                    |
                                                                     numero:Z --> LED() -->
                                                                                    |
                                                                                    |
                                                                               encender() -->
                                                                                    |
                                                                                    |
                                                                                apagar() -->
                                                                                    |
                                                                                    |
                                                                               alternar() -->
                                                                                    |
                                                                                    |
                                                                   tiempo:Z --> brillar() -->
                                                                                    -----------------------------------------------
```
```text
                                                                                    -------- PuertoSerie ----------------------------
                                                                                    | sin campos propios; utiliza el puerto de la plataforma
                                                                                    |
                                                                                    |
                                                                baudios:Z --> PuertoSerie() -->
                                                                                    |
                                                                                    |
                                                                           esperarDisponible() -->
                                                                                    |
                                                                                    |
                                                          mensaje:Elemento --> escribir() -->
                                                                                    -----------------------------------------------
```
```text
                                                                                    -------- ServicioEnEmisora ----------------------------
                                                                                    | uuid_servicio: [N]_16
                                                                                    | servicio: ServicioBLE
                                                                                    | caracteristicas: [Caracteristica]
                                                                                    |
                                                                                    |
                                                           nombre:Text --> ServicioEnEmisora() -->
                                                                                    |
                                                                                    |
                                                                              escribeUUID() -->
                                                                                    |
                                                                                    |
                                       caracteristica:Caracteristica --> anyadirCaracteristica() -->
                                                                                    |
                                                                                    |
                                                                            activarServicio() -->
                                                                                    |
                                                                                    |
                                                                          servicioSubyacente() <--
                                                           servicio:ServicioBLE <--
                                                                                    -----------------------------------------------
```
servicioSubyacente representa la conversión implícita operator BLEService de C++.

```text
                                                                                              -------- Caracteristica ----------------------------
                                                                                              | uuid_caracteristica: [N]_16
                                                                                              | caracteristica: CaracteristicaBLE
                                                                                              | propiedades:N --> asignarPropiedades() -->
                                                                                              | lectura:ModoPermiso, escritura:ModoPermiso --> asignarPermisos() -->
                                                                                              | tamanyo:N --> asignarTamanyoDatos() -->
                                                                                              |
                                                                                              |
                                                                      nombre:Text --> Caracteristica() -->
                                                                                              |
                                                                                              |
nombre:Text, propiedades:N, lectura:ModoPermiso, escritura:ModoPermiso, tamanyo:N --> Caracteristica() -->
                                                                                              |
                                                                                              |
 propiedades:N, lectura:ModoPermiso, escritura:ModoPermiso, tamanyo:N --> asignarPropiedadesPermisosYTamanyoDatos() -->
                                                                                              |
                                                                                              |
                                                                        datos:Text --> escribirDatos() -->
                                                                               cantidad:N <--
                                                                                              |
                                                                                              |
                                                                       datos:Text --> notificarDatos() -->
                                                                              resultado:N <--
                                                                                              |
                                                                                              |
                                                evento:EventoEscritura --> instalarCallbackCaracteristicaEscrita() -->
                                                                                              |
                                                                                              |
                                                                                          activar() -->
                                                                                              -----------------------------------------------
```
### Funciones libres y secuencia de ejecución

```text
tiempo: Z --> esperar()
elementos: [Elemento], n: N --> alReves() --> [Elemento]
texto: Text, destino: [N], capacidad: Z --> stringAUint8AlReves() --> [N]
inicializarPlaquita()
setup()
lucecitas()
loop()
```

alReves modifica y devuelve la misma colección; exige 0 <= n <= longitud.
stringAUint8AlReves copia al final del destino en orden inverso; conserva el resto.
setup limita la espera serie a 2000 ms; lucecitas suma esperas y destellos antes
de cada anuncio. loop incrementa el contador y publica 1234 durante 3000 ms.
Publicador mantiene el anuncio de tipo 11 activo entre iteraciones. El ejemplo
de temperatura, desactivado en loop, detiene su anuncio al concluir el intervalo.
PuertoSerie no conserva un campo velocidad: el constructor configura Serial.
La sobrecarga libre que solo recibe texto está comentada y no es una operación activa.


## Design Clarifications

- Se preserva la atribución del código docente.
- El identificador CO2 procede del ejemplo; no demuestra calibración ni lectura de O3.
- Compilación superada con Adafruit nRF52 1.7.0 para SparkFun Pro nRF52840 Mini: 98584 bytes de programa y 15380 bytes de RAM. Esto no confirma el modelo físico ni que se haya cargado en placa.
- Se incorporan cabeceras de descripción, diseño y parámetros en las funciones activas.
- Dentro de src/firmware/ se conserva HolaMundoIBeacon para que el nombre del sketch coincida con su directorio de Arduino.

## General Rules

- Programming Language: C++ Arduino con Bluefruit.
- Function/Method Headers: incluir descripción, diseño lógico y parámetros; indicar precondiciones, resultados y errores relevantes en un bloque delimitado por `-------------------`.
- Code Readability: nombres claros y código autoexplicativo; comentarios adicionales solo cuando aporten contexto.
- Automated Testing: generar pruebas unitarias o de integración para todas las funciones críticas, con límites y errores.
