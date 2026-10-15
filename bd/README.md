# Base de datos mínima viable — Sprint 0

La tabla activa `medidas` tiene únicamente:

| Campo | Tipo | Finalidad |
| --- | --- | --- |
| medidaID | BIGINT UNSIGNED, PK, AUTO_INCREMENT | Identificador textual en la API |
| tipo | TINYINT UNSIGNED | Código de medida; 11 en el prototipo actual |
| valor | SMALLINT | Medida ficticia 1234, rango con signo de 16 bits |
| fecha | TIMESTAMP(3), fecha actual | Recepción en el servidor, UTC |

UUID y contador pertenecen a la identificación y filtrado BLE del móvil; no se
almacenan en la tabla activa. El tipo 11 no demuestra una identificación química.

## Instalación nueva

Seleccionar la base en phpMyAdmin e importar `crear.sql`. No crea usuarios ni
el alojamiento. Configurar las credenciales exclusivamente en el config.php privado.
Ejecutar `prueba.sql` para insertar y revertir una medida de prueba.

## Actualizar la base publicada

No se ha ejecutado esta migración en Plesk desde la revisión.

1. Exportar la base actual con phpMyAdmin y detener temporalmente la captura del móvil.
2. Confirmar que la tabla actual tiene medidaID, uuid, gas, valor, contador y fecha.
3. Ejecutar una sola vez `migrar_mvp.sql`: copia ID, gas como tipo, valor y fecha,
   y cambia los nombres de forma atómica. La tabla anterior queda como respaldo.
4. Subir inmediatamente el backend MVP generado por `scripts/preparar_plesk.py`.
5. Consultar GET y verificar que conserva los IDs, valores y fechas anteriores,
   y que una nueva medida se guarda con tipo 11 y valor 1234.
6. Exportar el respaldo fuera del esquema de entrega antes de retirarlo. La
   aplicación utiliza solo `medidas`, con sus cuatro columnas.

Si ya existe medidas_preparacion_mvp o medidas_respaldo_anterior, no repetir la
migración ni borrar tablas a ciegas: revisar su estado primero. DDL no es reversible
mediante ROLLBACK. El respaldo permite recuperar el esquema anterior si es necesario.

Los clientes actualizados aceptan la respuesta del servidor antiguo y del MVP;
la API MVP también admite el anuncio antiguo del móvil, descarta sus metadatos y
entrega solo el modelo mínimo. Esta compatibilidad no añade columnas a la base.
