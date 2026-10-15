# database

## Component Design

```text
TABLE: medidas
DESCRIPTION: Producto mínimo viable: identificador, tipo, valor ficticio y fecha de recepción.
COLUMNS:
+ medidaID | BIGINT UNSIGNED | NOT NULL | AUTO_INCREMENT
+ tipo | TINYINT UNSIGNED | NOT NULL | sin valor por defecto
+ valor | SMALLINT | NOT NULL | sin valor por defecto
+ fecha | TIMESTAMP(3) | NOT NULL | CURRENT_TIMESTAMP(3)
PRIMARY KEY: medidaID
FOREIGN KEYS:
+ Ninguna
CONSTRAINTS:
+ tipo BETWEEN 0 AND 255 por tipo UNSIGNED
+ CHECK (valor BETWEEN -32768 AND 32767)
+ INDEX idx_medidas_fecha_id (fecha, medidaID)
```

Implementación canónica `src/database/crear.sql`; copia compatible en bd/crear.sql.
Motor InnoDB y zona de recepción UTC. No hay tablas de usuarios ni dispositivos.

## Design Clarifications

- UUID y contador no se almacenan en la tabla activa, según la aclaración de la autora.
- IF NOT EXISTS no modifica una tabla anterior; usar bd/migrar_mvp.sql si aún existe gas/uuid/contador.
- La migración preserva IDs, tipos (gas pasa a tipo), valores y fechas. La tabla anterior
  se conserva temporalmente como respaldo de seguridad, fuera del modelo de la aplicación.
- Exportar el respaldo antes de retirarlo del esquema de entrega; no se borra automáticamente.

## General Rules

- Programming Language: SQL MySQL/MariaDB.
- Function/Method Headers: incluir descripción, diseño lógico y parámetros en un bloque delimitado por `-------------------`.
- Code Readability: nombres claros y código autoexplicativo.
- Automated Testing: probar funciones críticas, límites, errores y persistencia de forma independiente.
