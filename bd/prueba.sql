-- Prueba independiente de datos existentes: inserta una fila ficticia y la revierte.
SET SESSION time_zone = '+00:00';
START TRANSACTION;
INSERT INTO medidas (tipo, valor) VALUES (11, 1234);
SET @prueba_id = LAST_INSERT_ID();
SELECT medidaID = @prueba_id AS id_correcto, tipo = 11 AS tipo_correcto,
       valor = 1234 AS valor_correcto, fecha IS NOT NULL AS fecha_correcta
FROM medidas WHERE medidaID = @prueba_id;
ROLLBACK;
-- El autoincremento puede dejar un hueco; no queda una medida de prueba.
