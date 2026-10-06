-- () --> crearTablaMedidas() --> tabla:Medidas
-- Pre: seleccionar en phpMyAdmin una base MySQL/MariaDB propia del proyecto.
-- Post: crea la tabla si no existe; no borra ni modifica medidas existentes.
-- Errores: permisos insuficientes o motor incompatible.
-- IMPORTANTE: IF NOT EXISTS no actualiza una tabla que tenga otro esquema.
SET SESSION time_zone = '+00:00';

CREATE TABLE IF NOT EXISTS medidas (
    medidaID BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    uuid VARCHAR(16) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    gas TINYINT UNSIGNED NOT NULL,
    valor SMALLINT NOT NULL COMMENT 'Valor recibido de minor, con signo; ppb según el diseño',
    contador TINYINT UNSIGNED NOT NULL,
    fecha TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    PRIMARY KEY (medidaID),
    KEY idx_medidas_fecha_id (fecha, medidaID),
    CONSTRAINT chk_medidas_uuid CHECK (uuid = 'EPSG-GTI-PROY-3A'),
    CONSTRAINT chk_medidas_valor CHECK (valor BETWEEN -32768 AND 32767),
    CONSTRAINT chk_medidas_contador CHECK (contador BETWEEN 0 AND 255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- CODIGO_O3 está pendiente en el diseño. No se impone gas=11 sin confirmarlo.
-- El índice también permite recorrer fecha e ID en orden descendente.
-- Algunas versiones antiguas ignoran CHECK: verificar y validar también en Logica.
