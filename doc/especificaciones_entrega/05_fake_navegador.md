# Especificación: Generar la lógica fake en el navegador

Autora: Julia Valén de Oliveira. Fecha: 16/10/2026.

Documento de generación preparado para la entrega a partir del diseño vigente.
No acredita que este fuera el prompt usado en la implementación inicial.

## Objetivo y rutas

Genera src/frontend_business_logic/medidas.mjs como módulo ES. La web utiliza el
subconjunto de lectura listarMedidas; guardarMedida pertenece al cliente Android.
El alias obtenerMedidas es compatibilidad de implementación con pruebas existentes.

## Diseño de comunicación encapsulada

```text
limit: N --> listarMedidas() --> medidas: [Medida]
GUI --> proxy --> GET ../api/index.php?ruta=medidas&limit=N
GUI <-- [Medida] <-- proxy valida respuesta <-- servidor
```

## Aclaraciones concretas

- limit entero 1..100, por defecto 50. La función no toca el DOM.
- fetchImpl es una dependencia de implementación inyectable, no un argumento de
la interfaz lógica. Pide JSON, evita caché y mantiene el orden recibido del negocio.
- Valida array, ID decimal positivo textual, tipo entero 0..255, valor entero de
16 bits con signo y fecha reconocible. No convierte ID a Number.
- Durante compatibilidad puede aceptar gas en lugar de tipo; devuelve únicamente
medidaID/tipo/valor/fecha, sin UUID ni contador.
- Distingue límite, conexión, estado no satisfactorio y JSON/datos inválidos.
La GUI recibe éxito o fallo de dominio; no conoce URL ni objetos de transporte.


## Diseño formal incorporado

# frontend_business_logic

## Component Design

```text
MedidaEntrada = (tipo: N, valor: Z)
Medida = (medida_id: Text, tipo: N, valor: Z, fecha: Text)
```
tipo 0..255; valor -32768..32767. ID decimal positivo como texto y fecha ISO 8601 UTC.

```text
limit: N --> listarMedidas() --> medidas: [Medida]
entrada: MedidaEntrada --> guardarMedida() --> medida: Medida
```
Las firmas coinciden con los subconjuntos del backend. La web usa listarMedidas:
valida límite 1..100, consulta, verifica ID, tipo, valor y fecha y devuelve solo
esos cuatro campos. Android usa guardarMedida mediante LogicaFake, cuya fuente
está en src/android/Prueba2025/app/src/main/java/org/jordi/prueba2025/.
Los metadatos BLE locales sirven para deduplicar; no cambian la medida persistida.
android_design.md detalla el adaptador, la ejecución serial, el reloj y los reintentos.
Errores: límite, conexión, estado incorrecto o respuesta inválida.
Implementación `src/frontend_business_logic/medidas.mjs`; GUI en src/gui/.

## Design Clarifications

- Mientras el alojamiento se actualiza, gas de la respuesta antigua se normaliza a tipo.
- No se devuelven UUID ni contador a la GUI, aunque el servidor antiguo los envíe.
- Asincronía e inyección del cliente de pruebas son detalles de implementación.
- obtenerMedidas es un alias de compatibilidad de listarMedidas.

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

Inyecta respuestas y fallos de red. Comprueba lista vacía, límites, tipo/valor/fecha
inválidos, ID grande preservado y normalización de gas a tipo sin metadatos extra.
Verifica obtenerMedidas === listarMedidas y que la firma lógica coincide con negocio.
Ejecuta servidor/tests/medidas.test.mjs. Regenera la copia pública con el generador.
