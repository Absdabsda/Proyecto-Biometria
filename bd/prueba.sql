-- () --> probarMedidas() --> comprobaciones:[B]
-- Pre: ejecutar crear.sql primero y usar la misma conexión durante este script.
-- Post: comprueba una inserción de valor 1234 y la revierte con ROLLBACK.
-- Errores: esquema incompatible o permisos insuficientes.
-- gas=11 solo reproduce el código del emisor actual; no confirma que sea O3.
SET SESSION time_zone = '+00:00';
START TRANSACTION;
INSERT INTO medidas (uuid, gas, valor, contador)
VALUES ('EPSG-GTI-PROY-3A', 11, 1234, 1);
SET @pbio_id_prueba = LAST_INSERT_ID();

SELECT medidaID, uuid, gas, valor, contador, fecha,
       valor = 1234 AS valor_correcto,
       uuid = 'EPSG-GTI-PROY-3A' AS uuid_correcto,
       contador = 1 AS contador_correcto,
       fecha IS NOT NULL AS fecha_asignada
FROM medidas WHERE medidaID = @pbio_id_prueba;

SELECT medidaID, uuid, gas, valor, contador, fecha
FROM medidas ORDER BY fecha DESC, medidaID DESC LIMIT 50;
ROLLBACK;
SELECT COUNT(*) = 0 AS prueba_revertida FROM medidas WHERE medidaID = @pbio_id_prueba;
