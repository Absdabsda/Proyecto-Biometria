# Prompt de implementación — auditoría de la rúbrica oficial

Fecha: 16/10/2026. Funcionalidad nueva de esta revisión: ampliar el verificador
para que detecte la documentación y las cabeceras exigidas por la rúbrica correcta.

## Diseño previo

Lee doc/acceptance_test.md, doc/rubrica_correcta_revision.md, doc/architecture.md
y los siete diseños. Mantén las comprobaciones existentes de capas y MVP.

Contrato lógico de la ampliación:
```text
archivo:Text --> verificar_cabecera_archivo()
raiz:Text --> verificar_documentos_rubrica()
```
Una condición incumplida debe detener el proceso con una explicación. Leer archivos
y metadatos Git es válido; no se permite escribir en Plesk, modificar datos o usar red.

## Implementación solicitada

- En scripts/verificar_revision.py añade una comprobación de nombre, descripción,
copyright, fecha, autor y aportación en las cabeceras de las fuentes propias y adaptadas.
- Incluye PHP, Java, JavaScript, HTML, C++, SQL y scripts Python. Excluye binarios,
cachés, herramientas de terceros y configuración privada ignorada.
- Verifica que existan criterios de aceptación, arquitectura, trazabilidad y los
ocho prompts; la trazabilidad debe indicar que son de revisión actual.
- Exige que cada uno de los siete componentes aparezca en la matriz de aceptación.
- Conserva la comprobación de paridad de interfaces, modelo de cuatro campos y
separación GUI/proxy. Refuerza paridad también para guardarMedida en cliente Android.
- Documenta cada función nueva con contrato y descripción. No añadas bibliotecas.

## Aceptación y revisión

Debe pasar en la copia de trabajo y en CI. Para comprobar que detecta fallos,
invoca las funciones sobre fixtures temporales: una cabecera incompleta debe
rechazarse y una completa aceptarse. Los fixtures se eliminan al terminar.
No fabriques historial de IA ni declares todas las tareas de PoliformaT cumplidas.

## Ampliación al recibir el enunciado de PoliformaT

Verifica también las seis especificaciones de generación de doc/especificaciones_entrega/:
BD, negocio, REST, fake teléfono, fake navegador y UX. Cada una debe incorporar
diseño formal, aclaraciones, directrices de implementación/comentarios y tests.
No confundas estas especificaciones de entrega con los ocho prompts de revisión.
