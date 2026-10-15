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

## General Rules

- Programming Language: HTML5, CSS y JavaScript ES modules.
- Function/Method Headers: descripción, diseño lógico y parámetros delimitados por `-------------------`.
- Code Readability: nombres claros y presentación separada del proxy.
- Automated Testing: probar datos, vacío, error, recuperación y concurrencia de actualizaciones.
