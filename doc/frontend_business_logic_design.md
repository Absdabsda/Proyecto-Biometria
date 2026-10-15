# frontend_business_logic

## Component Design

```text
MedidaEntrada = (uuid: Text, gas: N, valor: Z, contador: N)
Medida = (medidaID: Text, uuid: Text, gas: N, valor: Z, contador: N, fecha: Text)
```
UUID exacto `EPSG-GTI-PROY-3A`; gas y contador 0..255; valor -32768..32767.
`medidaID` es texto decimal para preservar el rango BIGINT. Fecha ISO 8601 UTC.

```text
limit: N --> listarMedidas() --> medidas: [Medida]
```
Interfaz idéntica al subconjunto de lectura de business_logic_design.md.
Valida límite 1..100, consulta GET, valida la lista recibida y devuelve medidas.
Errores: límite inválido, conexión fallida, respuesta incorrecta o datos inválidos.
Implementación `src/frontend_business_logic/medidas.mjs`.


## Design Clarifications

- La asincronía y la inyección del cliente de pruebas son detalles de implementación.
- `obtenerMedidas` se conserva como alias de compatibilidad.
- No existe todavía pantalla web propia de consulta; no se presenta el ejemplo de login como producto terminado.

## General Rules

- Programming Language: JavaScript ES modules.
- Function/Method Headers: incluir diseño lógico, precondiciones, poscondiciones y errores en un bloque delimitado por `-------------------`.
- Code Readability: nombres claros y código autoexplicativo; comentarios adicionales solo cuando aporten contexto.
- Automated Testing: generar pruebas unitarias o de integración para todas las funciones críticas, con límites y errores.
