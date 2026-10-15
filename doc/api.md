# API mínima viable — Sprint 0

## Modelo

Entrada de dominio: `{"tipo":11,"valor":1234}`.
Respuesta: `{"medidaID":"31","tipo":11,"valor":1234,"fecha":"2026-10-08T22:28:50.690Z"}`.
La tabla activa solo tiene estos cuatro campos. El tipo es un código 0..255,
valor un entero -32768..32767. ID y fecha se generan en el servidor.

## Operaciones

- POST `/api/index.php?ruta=medida`, application/json, hasta 4096 bytes: guarda y devuelve 201.
- GET `/api/index.php?ruta=medidas&limit=50`: devuelve 200 y hasta 1..100 filas,
  ordenadas por fecha e ID descendentes; `[]` si la base está vacía.
- Con rewrite, `/api/medida` y `/api/medidas` son equivalentes.

## Compatibilidad durante la actualización

El móvil existente envía {uuid, gas, valor, contador}. La comunicación MVP admite
ese objeto exacto, valida el UUID del proyecto y el contador, transforma gas en tipo
y pasa únicamente {tipo, valor} a negocio. La base no almacena los metadatos BLE.
Android actualizado acepta tanto la respuesta antigua como la MVP y comprueba tipo y valor.
El proxy web normaliza gas a tipo y entrega solo las cuatro propiedades MVP a la GUI.

## Errores

| Estado | Motivo |
| --- | --- |
| 400 | JSON, estructura, tipos, rangos o límite inválidos |
| 404 | Ruta desconocida |
| 405 | Método incorrecto; cabecera Allow |
| 413 | Cuerpo >4096 bytes |
| 415 | Tipo de contenido incorrecto |
| 500 | Configuración o persistencia no disponible; sin detalles privados |

Errores con codigo y mensaje. Prototipo académico sin autenticación de escritura.
No hay garantía de entrega exactamente una vez ni clave única por anuncio BLE.

## Estado del alojamiento

El 16/10/2026 se aplicó la migración al alojamiento MariaDB 10.11 y se actualizó
el backend. Se conservaron las 31 filas anteriores, comparadas por ID, tipo, valor
y fecha. GET devuelve únicamente medidaID, tipo, valor y fecha. La pantalla publicada
muestra las mismas medidas. Hay respaldo SQL y de archivos fuera de httpdocs.
La prueba de una medida física nueva se realiza aparte.
