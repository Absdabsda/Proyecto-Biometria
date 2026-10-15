# Revisión del Sprint 0 — Julia Valén de Oliveira

Fecha: 16/10/2026. Alcance confirmado: medida ficticia 1234.
Criterios: rúbrica aportada por la autora y documentos del revisor.
Repositorio: Absdabsda/Proyecto-Biometria. Rama propuesta: codex/revision-sprint-2026-10-16.
No se promete una calificación: las pruebas físicas y la valoración docente se realizan aparte.

## Verificaciones aprobadas

- ✅ Proyecto: author.md, doc/, src/, test/ y README con despliegue y ejecución de pruebas.
- ✅ Diseños: comunicación, negocio, base de datos, proxy web, GUI, Android y firmware; formato y reglas explícitos.
- ✅ Base de datos: tabla medidas, PK, restricciones y columnas alineadas con SQL y lógica; se conserva BIGINT como texto en los clientes.
- ✅ Lógica: backend separado de transporte; utiliza PDO y consultas parametrizadas. Proxy web listarMedidas y proxy Android guardarMedida comparten las firmas lógicas del backend.
- ✅ API: GET y POST separados, validación de JSON y rangos, límites de cuerpo, errores definidos y documentación específica en doc/api.md.
- ✅ Interfaz web: pantalla propia; utiliza exclusivamente el proxy, sin fetch ni SQL en la GUI.
- ✅ Android: GUI, proxy y transporte en clases distintas; permisos y ciclo de vida gestionados; deduplicación y reintentos verificables.
- ✅ Comentarios: se incorporan cabeceras de descripción, diseño y parámetros en las funciones activas Android y firmware; se preservan las atribuciones docentes.
- ✅ Pruebas web: 9 superadas, incluidos vacío, error, recuperación, concurrencia e identificadores grandes.
- ✅ Pruebas Android: 19 superadas con Gradle desde ruta sin tildes; APK debug generado.
- ✅ Backend: sintaxis PHP, pruebas REST y prueba contra MySQL real superadas en GitHub Actions. La integración usa base desechable y rollback.
- ✅ Servidor publicado: GET limit=5 respondió con IDs 31..27 y valor 1234, de fechas previas. Consulta sin modificaciones.
- ✅ Arduino, alcance: el loop activo anuncia 1234 ficticio; es coherente con el Sprint 0 confirmado. No se exige sustituirlo por lectura física del sensor para esta demostración.

## Pendientes y límites

- ❌ Prueba física actual: no hay placa conectada y no se ha observado el envío actual desde el teléfono.
  Acción: ejecutar el APK, recibir 1234, comprobar aviso de guardado y comparar contador y fecha con una fila nueva.
- ❌ Organización estricta del revisor: Android y firmware conservan sus carpetas originales fuera de src/.
  Acción: valorar la migración solo si se exige estrictamente; actualizar rutas y recompilar al moverlos. El backend y la web sí están en src/.
- ❌ Historial/GitFlow: se utiliza una rama de cambios real; no se ha acreditado la frecuencia histórica de trabajo ni un flujo completo del equipo.
  Acción: explicar el historial real en la revisión; no inventar commits ni evidencias de trabajo.
- ❌ Despliegue de la propuesta: subir una rama no actualiza Plesk ni main automáticamente.
  Acción: revisar e integrar la propuesta y desplegar el nuevo paquete público/privado; mantener config.php privado.
- ✅ Compilación Arduino: superada para SparkFun Pro nRF52840 Mini; no se detectó una placa conectada.
  Acción: seleccionar la placa real antes de cargar; no se ha cargado firmware desde esta revisión.

## Prioridad de demostración

1. Mantener 1234 en el firmware y mostrar la recepción Android.
2. Mostrar el aviso de guardado y una fila nueva en el servidor; conservar su contador e ID.
3. Abrir la pantalla de medidas tras desplegar y actualizar la tabla.
4. Enseñar diseños, separación de clases y pruebas; declarar qué verificaciones son físicas y cuáles automáticas.
