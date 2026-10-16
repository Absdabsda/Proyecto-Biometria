# Especificación: Generar la UX en el navegador

Autora: Julia Valén de Oliveira. Fecha: 16/10/2026.

Documento de generación preparado para la entrega a partir del diseño vigente.
No acredita que este fuera el prompt usado en la implementación inicial.

## Objetivo y rutas

Genera src/gui/Aplicacion.html y src/gui/medidas.mjs. Usa el proxy de lectura de
dominio; la GUI no contiene fetch, XMLHttpRequest, WebSocket, rutas ni SQL.
Mantén los IDs de elementos y el contrato de iniciarPantalla para pruebas aisladas.

## Diseño gráfico de pantalla

```text
+-----------------------------------------------------------------+
| Biometría y Medio Ambiente · Sprint 0                 [Actualizar]|
| Medidas recibidas                                                |
| Explicación del valor ficticio 1234 y del orden de recepción       |
| Estado: cargando / N medidas / sin datos / error recuperable       |
+------------+-----------------------+-------------------+----------+
| ID         | Fecha de recepción    | Tipo de medida    | Valor    |
| mas nuevo  | hora local            | 11                | 1234     |
| ... hasta 50 filas, de mas reciente a mas antigua ...             |
+-----------------------------------------------------------------+
| Julia Valén de Oliveira · fechas en la hora local del navegador   |
+-----------------------------------------------------------------+
```

## Aclaraciones concretas

- Paleta morada: fondo claro, texto oscuro, botón violeta y contraste legible.
Diseño adaptable, tabla con desplazamiento horizontal cuando sea necesario.
- Al abrir y al pulsar Actualizar, pide listarMedidas(50). La primera fila es la
última medida porque negocio ordena fecha/ID descendentes; no simules ese dato.
- Usa textContent, ID textual, fechas locales y encabezados scope=col. Estado con
role=status y aria-live. Deshabilita el botón mientras haya una consulta activa.
- Borra filas al iniciar una actualización. Si falla no dejes registros anteriores
como si fueran actuales; muestra explicación y permite volver a intentar.
- Separa cargar datos y representarlos. Documento y listar son dependencias
de implementación inyectables; no las incluyas como mecanismos en la firma lógica.
- No muestres UUID/contador ni atribuyas una identificación química al código 11.


## Diseño formal incorporado

# gui

## Component Design

```text
iniciarPantalla() --> PantallaActiva
actualizar()
```

Pantalla web con título, explicación de la medida ficticia 1234, botón Actualizar,
estado accesible y tabla de las últimas 50 medidas. Muestra ID textual, fecha local,
tipo de medida y valor ficticio. Al abrir consulta mediante `listarMedidas(limit: N) -> [Medida]`
del componente frontend_business_logic. No contiene operaciones de transporte.

Estados: cargando (botón deshabilitado), lista, lista vacía y error recuperable.
Al actualizar se retiran los registros anteriores; los datos se insertan con textContent.
La fecha persistida sigue en UTC; solo cambia su presentación.

## Design Clarifications

- Implementación en `src/gui/Aplicacion.html` y `src/gui/medidas.mjs`.
- El generador copia la pantalla a `public/ux/` y adapta la ruta del import del proxy.
- Los parámetros documento y listar son dependencias de implementación para pruebas aisladas.
- No se atribuye el código 11 a O3 sin confirmación del enunciado.

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

Usa documento y proxy aislados. Comprueba texto 1234, ID grande, fecha presentada,
vacío, fallo, recuperación, exclusión de consultas superpuestas y retirada de
filas antiguas tras un error. Ejecuta test/gui.test.mjs y tests del proxy.
Regenera servidor/public/ux con scripts/preparar_plesk.py; comprueba igualdad de
las copias e import adaptado. No inventes una captura del navegador ni una prueba física.
