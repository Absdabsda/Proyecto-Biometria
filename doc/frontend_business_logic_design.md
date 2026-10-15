# frontend_business_logic

## Component Design

```text
MedidaEntrada = (tipo: N, valor: Z)
Medida = (medidaID: Text, tipo: N, valor: Z, fecha: Text)
```
tipo 0..255; valor -32768..32767. ID decimal positivo como texto y fecha ISO 8601 UTC.

```text
limit: N --> listarMedidas() --> medidas: [Medida]
```
Coincide con el subconjunto de lectura del backend. Valida límite 1..100, consulta,
verifica ID, tipo, valor y fecha y devuelve solo esos cuatro campos.
Errores: límite, conexión, estado incorrecto o respuesta inválida.
Implementación `src/frontend_business_logic/medidas.mjs`; GUI en src/gui/.

## Design Clarifications

- Mientras el alojamiento se actualiza, gas de la respuesta antigua se normaliza a tipo.
- No se devuelven UUID ni contador a la GUI, aunque el servidor antiguo los envíe.
- Asincronía e inyección del cliente de pruebas son detalles de implementación.
- obtenerMedidas es un alias de compatibilidad de listarMedidas.

## General Rules

- Programming Language: JavaScript ES modules.
- Function/Method Headers: incluir descripción, diseño lógico y parámetros en un bloque delimitado por `-------------------`.
- Code Readability: nombres claros y código autoexplicativo.
- Automated Testing: probar funciones críticas, límites, errores y persistencia de forma independiente.
