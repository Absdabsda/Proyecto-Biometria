# communication

## Component Design

```text
Peticion = (metodo: Text, ruta: Text, tipo: Text, cuerpo: Text, query: [Parametro])
Parametro = (nombre: Text, valor: Text)
Respuesta = (estado: N, cuerpo: Medida | [Medida] | Error)
Error = (codigo: Text, mensaje: Text)
peticion: Peticion --> atenderPeticion() --> respuesta: Respuesta
```
Implementación: `src/communication/ReglasREST.php`; punto de entrada `servidor/public/api/index.php`.
POST `/medida`: JSON MedidaEntrada, delega guardarMedida, devuelve 201 y Medida.
GET `/medidas?limit=50`: delega listarMedidas, devuelve 200 y lista.
Alternativa sin rewrite: `index.php?ruta=medida` y `index.php?ruta=medidas`.
Errores 400 datos/JSON, 404 ruta, 405 método, 413 cuerpo >4096 bytes, 415 tipo y 500 interno.
No expone errores de conexión ni credenciales. Cabecera Allow en 405.


## Design Clarifications

- La fábrica de lógica es una dependencia de implementación, omitida de la firma abstracta.
- Configuración privada fuera de la carpeta pública en Plesk.
- Prototipo sin autenticación de escritura ni idempotencia.

## General Rules

- Programming Language: PHP 7.4 o superior.
- Function/Method Headers: incluir diseño lógico, precondiciones, poscondiciones y errores en un bloque delimitado por `-------------------`.
- Code Readability: nombres claros y código autoexplicativo; comentarios adicionales solo cuando aporten contexto.
- Automated Testing: generar pruebas unitarias o de integración para todas las funciones críticas, con límites y errores.
