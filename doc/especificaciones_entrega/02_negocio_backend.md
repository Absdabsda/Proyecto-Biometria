# Especificación: Generar la lógica de negocio del backend

Autora: Julia Valén de Oliveira. Fecha: 16/10/2026.

Documento de generación preparado para la entrega a partir del diseño vigente.
No acredita que este fuera el prompt usado en la implementación inicial.

## Objetivo y rutas

Implementa Logica en src/business_logic/Logica.php, PHP 7.4 o superior, usando PDO.
Los únicos datos de entrada del guardado son tipo y valor; el resultado tiene
medidaID textual, tipo, valor y fecha ISO 8601 UTC con milisegundos.

## Aclaraciones concretas

- __construct recibe persistencia y un filtro opcional de tipo permitido.
- validarMedida exige exactamente tipo/valor enteros; rechaza campos extra,
booleanos, números como texto y rangos inválidos. Un filtro ausente admite todo
tipo válido; si existe exige igualdad con ese tipo.
- guardarMedida valida, inserta con parámetros enlazados y recupera la fila por
el identificador asignado por la base. Nunca inventa ID ni fecha en el cliente.
- listarMedidas acepta 1..100, por defecto 50, y ordena fecha/ID descendentes.
- serializar es privada: mantiene ID como Text, tipo/valor numéricos y fecha UTC.
- El componente no importa comunicación, GUI ni frameworks de transporte.
No incluye rutas, peticiones, cabeceras ni códigos de protocolo en sus firmas.


## Diseño formal incorporado

# business_logic

## Component Design

```text
MedidaEntrada = (tipo: N, valor: Z)
Medida = (medida_id: Text, tipo: N, valor: Z, fecha: Text)
RegistroSQL = (medida_id: Text, tipo: N, valor: Z, fecha: Text)
```
tipo 0..255; valor -32768..32767. ID decimal positivo como texto y fecha ISO 8601 UTC.

```text
entrada: MedidaEntrada --> guardarMedida() --> medida: Medida
limit: N --> listarMedidas() --> medidas: [Medida]
entrada: MedidaEntrada, tipo_permitido: N | Ausente --> validarMedida() --> MedidaEntrada
```
`guardarMedida` valida exactamente tipo y valor, inserta una fila y recupera ID y fecha.
`listarMedidas` exige límite 1..100 (defecto 50), orden fecha e ID descendentes.
Errores: entrada inválida o persistencia no disponible.
Usa exclusivamente tabla `medidas` de database_design.md, con columnas medidaID,
tipo, valor y fecha. No almacena identificadores de placa ni contadores de anuncios.
Implementación: `src/business_logic/Logica.php`; dependencia de persistencia PDO.

```text
                                                                                    -------- Logica ----------------------------
                                                                                    | bd: Persistencia
                                                                                    | tipo_permitido: N | Ausente
                                                                                    | fila:RegistroSQL --> serializar() --x
                                                                                    | medida:Medida <--
                                                                                    |
                                                                                    |
                                bd:Persistencia, tipo_permitido:N|Ausente --> __construct() -->
                                                                                    |
                                                                                    |
                                                   entrada:MedidaEntrada --> guardarMedida() -->
                                                                  medida:Medida <--
                                                                                    |
                                                                                    |
                                                                 limit:N --> listarMedidas() <--
                                                               medidas:[Medida] <--
                                                                                    |
                                                                                    |
                         entrada:MedidaEntrada, tipo_permitido:N|Ausente --> validarMedida() --x
                                                          entrada:MedidaEntrada <--
                                                                                    -----------------------------------------------
```

Persistencia representa la conexión a las cuatro columnas del diseño de base de datos.
serializar es privado: conserva ID decimal textual y convierte la fecha de recepción a UTC.

## Design Clarifications

- El código 11 identifica el tipo del prototipo sin afirmar una identificación química.
- El filtrado y la deduplicación BLE ocurren en el móvil; no forman parte de la base.
- Las operaciones usan parámetros enlazados. ID y fecha los genera la base de datos.
- La conexión es una dependencia de construcción; las firmas públicas usan tipos de dominio.

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

Prueba tipo 0 y 255, valor -32768 y 32767; rechaza fuera de rango, campos
faltantes/adicionales y valores de otro tipo. Prueba filtro de tipo permitido.
Contra una base desechable, guarda 1234, recupera su ID/fecha y lista con límites
y desempate por ID. Una validación fallida no inserta filas. Finalmente elimina
la base de pruebas, incluso si una aserción falla.

Usa test/backend_integration.php y la validación ejercitada por servidor/tests/rest.php.
La revisión estática debe confirmar la ausencia de dependencias de comunicación.
