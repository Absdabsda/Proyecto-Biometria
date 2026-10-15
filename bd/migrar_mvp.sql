-- Una sola ejecución en la base correcta. Exportar primero la base desde phpMyAdmin.
-- Requiere permisos CREATE, INSERT, SELECT, LOCK TABLES y ALTER/RENAME.
-- Aplicar junto al backend MVP, con la captura de medidas detenida.
SET SESSION time_zone = '+00:00';
CREATE TABLE medidas_preparacion_mvp (
    medidaID BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    tipo TINYINT UNSIGNED NOT NULL,
    valor SMALLINT NOT NULL,
    fecha TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    PRIMARY KEY (medidaID),
    KEY idx_medidas_fecha_id (fecha, medidaID),
    CONSTRAINT chk_mvp_valor CHECK (valor BETWEEN -32768 AND 32767)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
LOCK TABLES medidas WRITE, medidas_preparacion_mvp WRITE;
INSERT INTO medidas_preparacion_mvp (medidaID, tipo, valor, fecha)
SELECT medidaID, gas, valor, fecha FROM medidas;
UNLOCK TABLES;
-- MariaDB no admite este RENAME con LOCK TABLES activo. La captura debe seguir detenida.
RENAME TABLE medidas TO medidas_respaldo_anterior, medidas_preparacion_mvp TO medidas;
SELECT medidaID, tipo, valor, fecha FROM medidas ORDER BY fecha DESC, medidaID DESC LIMIT 5;
-- La tabla activa ya no guarda UUID ni contador. El respaldo no pertenece al modelo de la aplicación.
-- Exportar el respaldo fuera del esquema de entrega antes de retirarlo manualmente.
