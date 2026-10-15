# firmware

## Component Design

```text
setup()
loop()
iniciarMedidor()
medirCO2() --> Z
medirTemperatura() --> Z
valor: Z, contador: N, tiempo_espera: N --> publicarCO2()
valor: Z, contador: N, tiempo_espera: N --> publicarTemperatura()
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
| PuertoSerie | Velocidad; esperarDisponible y escribir |
| ServicioEnEmisora | UUID y colección de características; añadir, activar y mostrar UUID |
| Caracteristica | UUID y característica Bluefruit; propiedades, permisos, longitud, escritura y notificación |

setup activa Publicador y Medidor. loop incrementa un contador de 8 bits, ejecuta
el patrón LED y publica 1234 durante 3000 ms. El anuncio continúa entre iteraciones.
El bloque de temperatura está desactivado con `#if 0`. Los helpers de servicios
se conservan como material de apoyo, pero el recorrido activo usa anuncios iBeacon.


## Design Clarifications

- Se preserva la atribución del código docente.
- El identificador CO2 procede del ejemplo; no demuestra calibración ni lectura de O3.
- Compilación superada con Adafruit nRF52 1.7.0 para SparkFun Pro nRF52840 Mini: 98584 bytes de programa y 15380 bytes de RAM. Esto no confirma el modelo físico ni que se haya cargado en placa.
- Se incorporan cabeceras de descripción, diseño y parámetros en las funciones activas.
- Dentro de src/firmware/ se conserva HolaMundoIBeacon para que el nombre del sketch coincida con su directorio de Arduino.

## General Rules

- Programming Language: C++ Arduino con Bluefruit.
- Function/Method Headers: incluir diseño lógico, precondiciones, poscondiciones y errores en un bloque delimitado por `-------------------`.
- Code Readability: nombres claros y código autoexplicativo; comentarios adicionales solo cuando aporten contexto.
- Automated Testing: generar pruebas unitarias o de integración para todas las funciones críticas, con límites y errores.
