# business_logic

## Component Design

```text
MedidaEntrada = (uuid: Text, gas: N, valor: Z, contador: N)
Medida = (medidaID: Text, uuid: Text, gas: N, valor: Z, contador: N, fecha: Text)
```
UUID exacto `EPSG-GTI-PROY-3A`; gas y contador 0..255; valor -32768..32767.
`medidaID` es texto decimal para preservar el rango BIGINT. Fecha ISO 8601 UTC.

```text
entrada: MedidaEntrada --> guardarMedida() --> medida: Medida
limit: N --> listarMedidas() --> medidas: [Medida]
entrada: MedidaEntrada, codigo_o3: N | Ausente --> validarMedida() --> entrada: MedidaEntrada
```
`guardarMedida`: valida exactamente cuatro campos, inserta una fila y recupera ID y fecha.
`listarMedidas`: exige 1 <= limit <= 100 (por defecto 50); orden fecha e ID descendentes.
Errores de dominio: entrada inválida. Errores de persistencia: operación fallida.
Persistencia: tabla `medidas` de `database_design.md`, columnas `medidaID`, `uuid`, `gas`, `valor`, `contador`, `fecha`.
Implementación: `src/business_logic/Logica.php`. Dependencia externa de persistencia: PDO.


## Design Clarifications

- Las operaciones de persistencia se realizan con parámetros enlazados.
- El código de gas O3 está pendiente; configuración ausente acepta 0..255.
- Las firmas describen la interfaz pública de dominio; la conexión es una dependencia de construcción.

## General Rules

- Programming Language: PHP 7.4 o superior.
- Function/Method Headers: incluir diseño lógico, precondiciones, poscondiciones y errores en un bloque delimitado por `-------------------`.
- Code Readability: nombres claros y código autoexplicativo; comentarios adicionales solo cuando aporten contexto.
- Automated Testing: generar pruebas unitarias o de integración para todas las funciones críticas, con límites y errores.
