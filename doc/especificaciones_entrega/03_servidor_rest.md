# Especificación: Generar el servidor REST del backend

Autora: Julia Valén de Oliveira. Fecha: 16/10/2026.

Documento de generación preparado para la entrega a partir del diseño vigente.
No acredita que este fuera el prompt usado en la implementación inicial.

## Objetivo y rutas

Implementa el adaptador en src/communication/ReglasREST.php y la entrada de
servidor/public/api/index.php. Invoca negocio; no dupliques validación de dominio
ni consultas SQL dentro del adaptador de rutas. La entrada configura PDO mediante
la configuración privada situada fuera del directorio público.

## Diseño de rutas

```text
POST /medida  -- JSON --> atenderPeticion --> guardarMedida --> Medida
GET  /medidas -- limit --> atenderPeticion --> listarMedidas --> [Medida]

Peticion --> comunicacion --> negocio --> medidas
Respuesta <-- comunicacion <-- negocio <-- medidas
```

## Aclaraciones concretas

- También acepta index.php?ruta=medida e index.php?ruta=medidas para Plesk sin rewrite.
- POST mínimo: {tipo, valor}; válido devuelve 201 con la medida confirmada.
- Compatibilidad móvil: {uuid, gas, valor, contador}; exige UUID EPSG-GTI-PROY-3A,
contador entero 0..255 y conjunto de claves exacto. Transforma gas en tipo y
descarta UUID/contador antes de invocar negocio. Nunca los persistas.
- GET con limit 1..100 (por defecto 50) devuelve 200 y lista, incluida lista vacía.
- Usa 400 para JSON/datos, 404 para ruta, 405 para método (con Allow), 413 para
cuerpo mayor de 4096 bytes, 415 si no es application/json y 500 para fallo interno.
- No expongas contraseñas, consultas ni detalles internos. Admite parámetros
del tipo application/json; charset=utf-8 y responde JSON con no-store.


## Diseño formal incorporado

# communication

## Component Design

```text
Peticion = (metodo: Text, ruta: Text, tipo: Text, cuerpo: Text, query: [Parametro])
Parametro = (nombre: Text, valor: Text)
Respuesta = (estado: N, cuerpo: Medida | [Medida] | Error)
Error = (codigo: Text, mensaje: Text)
peticion: Peticion --> atenderPeticion() --> respuesta: Respuesta
```
Implementación: `src/communication/ReglasREST.php`; punto de entrada `servidor/public/api/index.php`.
POST `/medida`: JSON {tipo, valor}, delega guardarMedida y devuelve 201 con medidaID, tipo, valor y fecha. También acepta el anuncio antiguo {uuid, gas, valor, contador}: valida su identificación y contador, convierte gas en tipo y descarta esos metadatos antes de llamar al negocio.
GET `/medidas?limit=50`: delega listarMedidas, devuelve 200 y lista.
Alternativa sin rewrite: `index.php?ruta=medida` y `index.php?ruta=medidas`.
Errores 400 datos/JSON, 404 ruta, 405 método, 413 cuerpo >4096 bytes, 415 tipo y 500 interno.
No expone errores de conexión ni credenciales. Cabecera Allow en 405.


## Design Clarifications

- La fábrica de lógica es una dependencia de implementación, omitida de la firma abstracta.
- Configuración privada fuera de la carpeta pública en Plesk.
- Prototipo sin autenticación de escritura ni idempotencia.

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

servidor/tests/rest.php debe cubrir POST mínimo y compatible, rangos y claves,
JSON mal formado, tipos equivocados, cuerpo grande, método/ruta y límite inválidos.
Inyecta una fábrica de negocio para probar el despacho sin base ni red. Comprueba
que llega a negocio únicamente {tipo,valor}. Usa después integración SQL real de CI.
No insertes filas de prueba en Plesk para simular una recepción de la placa.
