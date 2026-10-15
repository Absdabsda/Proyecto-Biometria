# database

## Component Design

```text
TABLE: medidas
DESCRIPTION: Anuncios de medidas recibidos; fecha de recepción asignada por la base.
COLUMNS:
+ medidaID | BIGINT UNSIGNED | NOT NULL | AUTO_INCREMENT
+ uuid | VARCHAR(16) ASCII COLLATE ascii_bin | NOT NULL | sin valor por defecto
+ gas | TINYINT UNSIGNED | NOT NULL | sin valor por defecto
+ valor | SMALLINT | NOT NULL | sin valor por defecto
+ contador | TINYINT UNSIGNED | NOT NULL | sin valor por defecto
+ fecha | TIMESTAMP(3) | NOT NULL | CURRENT_TIMESTAMP(3)
PRIMARY KEY: medidaID
FOREIGN KEYS:
+ Ninguna
CONSTRAINTS:
+ CHECK (uuid = 'EPSG-GTI-PROY-3A')
+ CHECK (valor BETWEEN -32768 AND 32767)
+ CHECK (contador BETWEEN 0 AND 255)
+ gas BETWEEN 0 AND 255 por tipo UNSIGNED
+ INDEX idx_medidas_fecha_id (fecha, medidaID)
```
Implementación canónica `src/database/crear.sql`. Motor InnoDB, zona UTC.


## Design Clarifications

- IF NOT EXISTS no migra una tabla antigua.
- La unidad ppb y la identificación química requieren confirmación con el enunciado y el sensor.
- La validación de dominio se mantiene aunque el motor no aplique CHECK.

## General Rules

- Programming Language: SQL MySQL/MariaDB.
- Function/Method Headers: incluir diseño lógico, precondiciones, poscondiciones y errores en un bloque delimitado por `-------------------`.
- Code Readability: nombres claros y código autoexplicativo; comentarios adicionales solo cuando aporten contexto.
- Automated Testing: generar pruebas unitarias o de integración para todas las funciones críticas, con límites y errores.
