-- Nombre: crear.sql
-- Descripción: Esquema mínimo de medidas: identificador, tipo, valor y fecha.
-- Copyright: 2026 Julia Valén de Oliveira, solo aportaciones propias; se conservan derechos y atribuciones anteriores.
-- Fecha: 2026-10-16 (revisión; fechas originales conservadas si constan).
-- Autor: Julia Valén de Oliveira (aportaciones); base docente según atribuciones conservadas.
-- Aportación: Modelo mínimo viable y documentación; sin UUID ni contador persistidos.
-- Producto mínimo viable del Sprint 0: solo ID, tipo, valor y fecha.
-- Si existe el esquema anterior, usar bd/migrar_mvp.sql.
SET SESSION time_zone = '+00:00';
CREATE TABLE IF NOT EXISTS medidas (
    medidaID BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    tipo TINYINT UNSIGNED NOT NULL,
    valor SMALLINT NOT NULL COMMENT 'Medida ficticia con signo del Sprint 0',
    fecha TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    PRIMARY KEY (medidaID),
    KEY idx_medidas_fecha_id (fecha, medidaID),
    CONSTRAINT chk_medidas_valor CHECK (valor BETWEEN -32768 AND 32767)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
