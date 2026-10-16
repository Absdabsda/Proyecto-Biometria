# Especificación: Generar la base de datos mínima

Autora: Julia Valén de Oliveira. Fecha: 16/10/2026.

Documento de generación preparado para la entrega a partir del diseño vigente.
No acredita que este fuera el prompt usado en la implementación inicial.

## Objetivo y rutas

Genera SQL MySQL 8.4/MariaDB 10.11 en src/database/crear.sql y la copia compatible
bd/crear.sql. La tabla activa medidas tiene exactamente cuatro columnas. No añadas
usuarios, dispositivos, UUID, contadores ni catálogos. Configura recepción UTC.

## Diseño gráfico complementario

```text
+--------------------------------------+
| medidas                              |
+--------------------------------------+
| medidaID : N  (clave primaria)        |
| tipo     : N                         |
| valor    : Z                         |
| fecha    : Text (fecha UTC)           |
+--------------------------------------+
```

## Aclaraciones concretas

medidaID es BIGINT UNSIGNED AUTO_INCREMENT; tipo es TINYINT UNSIGNED 0..255;
valor SMALLINT -32768..32767; fecha TIMESTAMP(3) con CURRENT_TIMESTAMP(3).
Índice compuesto fecha/medidaID para ordenar lo más reciente primero. InnoDB,
utf8mb4 y modo estricto. El ID se representará como texto al salir al cliente.
IF NOT EXISTS no migra esquemas anteriores. No ejecutes migraciones en una base
real: la migración existente requiere respaldo y captura detenida; no la repitas.


## Diseño formal incorporado

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

## Directrices de implementación y revisión

Implementa lo diseñado en las rutas indicadas. No cambies identificadores, nombres
de las operaciones ni rangos del dominio para adaptarlos a otra arquitectura.
La medida del Sprint 0 es ficticia: no atribuyas calibración ni lectura física.

- Cabecera de cada fichero propio/adaptado: Nombre, Descripción, Copyright limitado
a aportaciones propias, Fecha real, Autor y Aportación. Autora: Julia Valén de Oliveira.
Conserva las atribuciones de Jordi Bataller y demás material original cuando existan.
- Cada función lleva un bloque separado por ------------------- con firma lógica
en la notación oficial, descripción breve, parámetros y errores relevantes.
- Usa N, Z, R, B, Text, agregaciones y colecciones para diseñar; los detalles del
lenguaje, referencias, hilos y callbacks pertenecen a la implementación.
- Usa nombres claros y pocos comentarios internos, salvo decisiones que requieran explicación.
- Escribe pruebas automáticas independientes, con datos propios y limpieza; cubre
casos válidos, límites y errores. No uses credenciales reales ni la base de Plesk.
- Revisa el resultado frente a cada firma y criterio. Indica qué has comprobado y
qué no se puede verificar sin hardware. Nunca sustituyas una prueba real por una afirmación.
- Mantén las rutas de apertura de los IDE y evita copias divergentes. No reescribas
otros componentes ni regeneres todo el proyecto para corregir un fichero.


## Tests y aceptación

Verifica las cuatro columnas, la clave primaria, tipos, límites y fecha UTC.
La integración debe crear su base pbio_backend_test y eliminarla en finally,
sin cambiar tablas ni AUTO_INCREMENT de la base inicial. Prueba guardar/listar
mediante negocio y reversión de entradas inválidas. La migración se prueba en
pbio_migration_test, preservando ID, tipo, valor, fecha y respaldo.

Ejecuta test/backend_integration.php y test/migration_integration.php bajo las
variables del workflow. No simules que hay una base local si no está disponible;
usa CI con MySQL/MariaDB. Las dos copias crear.sql deben coincidir exactamente.
