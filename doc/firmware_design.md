# firmware

## Component Design

```text
iniciarMedidor()
medirCO2() --> Z
medirTemperatura() --> Z
valor: Z, contador: N, tiempo_espera: N --> publicarCO2()
valor: Z, contador: N, tiempo_espera: N --> publicarTemperatura()
```
Código: `HolaMundoIBeacon/`.
El loop activo emite MEDIDA_DE_PRUEBA=1234; no llama a Medidor para adquirir el gas. Medidor conserva constantes de ejemplo 235 y -12. Es un simulador, no una adquisición real del sensor.
Publicador emite UUID EPSG-GTI-PROY-3A, tipo 11 o 12 en byte alto de major,
contador en byte bajo, valor en minor y referencia -53.


## Design Clarifications

- Se preserva la atribución del código docente.
- El identificador CO2 procede del ejemplo; no demuestra calibración ni lectura de O3.
- Compilación, placa y sensor deben verificarse físicamente.
- La ubicación src/ y los encabezados formales del firmware quedan pendientes.

## General Rules

- Programming Language: C++ Arduino con Bluefruit.
- Function/Method Headers: incluir diseño lógico, precondiciones, poscondiciones y errores en un bloque delimitado por `-------------------`.
- Code Readability: nombres claros y código autoexplicativo; comentarios adicionales solo cuando aporten contexto.
- Automated Testing: generar pruebas unitarias o de integración para todas las funciones críticas, con límites y errores.
