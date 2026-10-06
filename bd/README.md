# Base de datos del Sprint 0

## Importar en Plesk

1. En el dominio de Plesk, entra en **Bases de datos** y crea una base MySQL/MariaDB y su usuario. Guarda nombre, usuario, contraseña y servidor para configurar el backend. No los subas a GitHub.
2. Abre **phpMyAdmin** y selecciona esa base.
3. En **Importar**, selecciona `crear.sql` y ejecútalo. Creará la tabla `medidas`; no crea el servidor REST.
4. Importa `prueba.sql`. Las columnas de comprobación deben valer `1`. La inserción de ejemplo tiene valor **1234** y se revierte al terminar. El contador autoincremental puede dejar un hueco: es normal.

No es necesario subir los SQL a la carpeta pública de la web.

## Qué representa una fila

`medidaID` es la clave primaria automática. `uuid` identifica el proyecto, no una placa individual. `gas` es el byte alto de Major y `contador` el bajo. `valor` es Minor interpretado como entero de 16 bits con signo. `fecha` es la recepción en la base de datos.

1234 es un ejemplo de valor; no se fija por defecto en la tabla. El móvil deberá enviar lo que realmente reciba, y cambiar la placa a 1235 deberá producir una medida con valor 1235.

La unidad del PDF es ppb. La app existente dice ppm: hay que corregir y contrastar esa etiqueta con el firmware. El código 11 aparece en el emisor existente con el nombre CO2 y en Android como O3. El diseño deja CODIGO_O3 pendiente; por eso este SQL permite cualquier código de gas entre 0 y 255 hasta resolver esa correspondencia. Una vez confirmada, hay que añadir la restricción de gas y la misma validación en Logica. No se considera cerrado ese requisito.

## Zona horaria y comprobaciones del motor

El backend debe ejecutar `SET SESSION time_zone = '+00:00'` en cada conexión. TIMESTAMP representa el instante y MySQL lo muestra según la zona de la conexión. La API debe devolver ISO 8601 UTC; la página lo convierte a la hora local.

En phpMyAdmin ejecuta `SELECT VERSION(), @@SESSION.sql_mode;` y `SHOW CREATE TABLE medidas;`. Usa modo estricto en las conexiones del backend (`STRICT_TRANS_TABLES`) para que los valores fuera de rango no se recorten silenciosamente.

Para comprobar restricciones, prueba cada inserción por separado en una base de prueba, con modo estricto activo:

```sql
SET SESSION sql_mode = 'STRICT_TRANS_TABLES,NO_ENGINE_SUBSTITUTION';
INSERT INTO medidas (uuid, gas, valor, contador) VALUES ('EPSG-GTI-PROY-3A',11,1234,256);
INSERT INTO medidas (uuid, gas, valor, contador) VALUES ('EPSG-GTI-PROY-3A',11,32768,1);
INSERT INTO medidas (uuid, gas, valor, contador) VALUES ('UUID-INVALIDO',11,1234,1);
```

Las tres deben fallar. Si la tercera entra, el motor no está aplicando CHECK. Estas pruebas negativas aún no se han ejecutado contra tu Plesk. La lógica del servidor debe validar siempre tipos, UUID y rangos antes de insertar.

La base no deduplica anuncios BLE: no hay clave única por UUID/contador/valor porque el contador se reutiliza. El móvil debe aplicar la ventana de 60 segundos del diseño.

## Cómo conecta con REST

Android (cliente REST) envía un POST con `uuid`, `gas`, `valor` y `contador`. El servidor REST recibe JSON y llama a Logica; Logica valida e inserta usando parámetros SQL. GET devuelve las medidas ordenadas por `fecha DESC, medidaID DESC`. La página consulta GET. El móvil y el navegador no reciben las credenciales de la base ni acceden directamente a MySQL.

Los ejemplos actuales son puntos de partida: PeticionarioREST hace peticiones HTTP y el ejemplo PHP devuelve saludos. Falta implementar los endpoints de medidas, Logica, el envío desde Prueba2025 y la página. El PDF propone Node.js para backend; el repositorio contiene ejemplos PHP. La tecnología de despliegue debe concretarse antes de implementar el servidor.
