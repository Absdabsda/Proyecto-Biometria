# API de medidas del Sprint 0

Base publicada consultada: `https://jvaldeo.upv.edu.es/api/index.php`.
El navegador y Android usan esta API; solo el servidor posee las credenciales SQL.

## Guardar medida

`POST ?ruta=medida`, Content-Type `application/json`:

```json
{"uuid":"EPSG-GTI-PROY-3A","gas":11,"valor":1234,"contador":1}
```

Campos exactos. gas y contador enteros 0..255; valor entero -32768..32767.
El valor 1234 es la medida ficticia del sprint; el servidor acepta otros valores
del rango para permitir comprobar el recorrido sin valores fijados en la API.
Devuelve 201 con medidaID textual, los cuatro campos y fecha ISO 8601 UTC.

## Consultar medidas

`GET ?ruta=medidas&limit=50`, límite entero 1..100, por defecto 50.
Devuelve 200 con lista ordenada por fecha e ID descendentes, o `[]` si no hay medidas.
Las rutas `/api/medida` y `/api/medidas` son equivalentes cuando rewrite está habilitado.

## Errores

| Estado | Significado |
| --- | --- |
| 400 | JSON, estructura, tipos, rangos o límite inválidos |
| 404 | Ruta desconocida |
| 405 | Método incorrecto; cabecera Allow indica el admitido |
| 413 | Cuerpo mayor de 4096 bytes |
| 415 | Contenido distinto de application/json |
| 500 | Configuración o persistencia no disponible; sin detalles privados |

Los errores contienen `codigo` y `mensaje`. La escritura es un prototipo académico
sin autenticación; la deduplicación del móvil no impone unicidad en el servidor.
No se añade una clave única por contador porque este se reutiliza al alcanzar 255.

## Evidencia de consulta

El 16/10/2026 se consultó GET limit=5: respondió con cinco filas, IDs 31 a 27,
valor 1234 y fechas del 08/10/2026. Esta consulta no insertó ni alteró datos y no
demuestra una recepción BLE nueva de hoy.
