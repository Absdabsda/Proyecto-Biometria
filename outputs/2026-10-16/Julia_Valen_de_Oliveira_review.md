# Revisión oficial del Sprint 0 — Julia Valén de Oliveira

Fecha: 16/10/2026. Esta revisión sustituye las conclusiones basadas en la primera
imagen de rúbrica. Fuentes: Rubrica-2026-Sprint-0.pdf, Notacion oficial.pdf (v3),
AGENTS.md y especificaciones del revisor. Código revisado: b56a3f8, PR #5.

## Matriz de la rúbrica correcta

| Peso | Bloque | Evidencias y resultado |
| --- | --- | --- |
| 15 % | Buenas prácticas | ✅ master es la rama predeterminada y existe develop; README con despliegue/tests, autoría, 77 cabeceras completas y 126 declaraciones con diseño/descripcion/parámetros. La frecuencia histórica no se certifica. |
| 20 % | Ingeniería inversa y diseño | ✅ Los siete componentes activos tienen diseño y carpeta correspondientes; tipos abstractos, colecciones y clases conforme a v3. Se documentan también MainActivity y los eventos de la pantalla. Modelo mínimo, negocio, proxy y REST disponibles. |
| 30 % | Arquitectura y negocio | ✅ Comunicación -> negocio -> BD; negocio sin dependencia HTTP. GUI web/Android consumen proxies de dominio con firmas coincidentes. Capas, proxy, adaptador e inyección de dependencias explicados en doc/architecture.md. |
| 25 % | Tests | ✅ Criterios por entregable en doc/acceptance_test.md. Web, Android, negocio/REST, persistencia/migración, auditoría y contrato del publicador automatizados. Bases desechables y casos aislados. |
| 10 % | Herramientas IA | ✅ Ocho prompts de revisión escritos desde los diseños; el prompt 08 guía la nueva funcionalidad de auditoría. Commits separan documentación previa y aplicación. Trazabilidad y revisión por pruebas en doc/ai_traceability.md. No se presentan estos prompts como prueba de la generación anterior. |

No se asignan notas parciales: las valora el profesor con la defensa y las tareas.

## Comprobaciones del agente

- ✅ author.md contiene Julia Valén de Oliveira; informe con su nombre bajo outputs/2026-10-16/.
- ✅ doc/, src/ y test/ presentes; cada *_design.md corresponde a su componente activo.
- ✅ Los diseños contienen Component Design, Design Clarifications y General Rules; lenguaje, cabeceras, legibilidad y pruebas definidos.
- ✅ Tríada communication/business_logic/database y frontend_business_logic explícitos.
- ✅ Diseño de tablas con TABLE, DESCRIPTION, COLUMNS, PRIMARY KEY, FOREIGN KEYS y CONSTRAINTS.
- ✅ Tabla activa medidas con medidaID, tipo, valor y fecha. Las dos copias SQL coinciden. No se adoptó el esquema de tres tablas del repositorio de referencia.
- ✅ Negocio valida datos de dominio y accede mediante PDO/SQL parametrizado; no depende del transporte.
- ✅ Firmas compartidas: listarMedidas(limit:N)->[Medida] en web/servidor y guardarMedida(entrada:MedidaEntrada)->Medida en Android/servidor. Asincronía e inyección documentadas como implementación.
- ✅ La GUI no contiene transporte; los adaptadores gestionan HTTP/JSON y errores.
- ✅ Atribuciones docentes conservadas. Copyright añadido limitado a aportaciones propias y fecha marcada como revisión real.
- ✅ Fuentes canónicas y copias públicas diferenciadas; generador y auditoría comprueban su correspondencia.
- ✅ examples/ contiene referencias docentes identificadas, ajenas al ejecutable activo.

## Pruebas y evidencias

- 10 pruebas web: proxy, valores/IDs, vacío, errores, recuperación y concurrencia.
- 19 Android: parser y proxy/dominio; compilación APK en CI.
- 3 pruebas nuevas de la auditoría: acepta metadatos completos y rechaza campos, nombres o prompts ausentes.
- PHP/REST, persistencia y migración comprobados contra MySQL 8.4 y MariaDB 10.11; bases propias eliminadas al finalizar, sin alterar datos ni secuencias de la base inicial.
- Publicador real con emisora sustituida: 15 combinaciones de valor/contador, UUID, signo, tipo e intervalo; ejemplo de temperatura y Medidor.
- Firmware completo recompilado tras las cabeceras: SparkFun Pro nRF52840 Mini, Adafruit nRF52 1.7.0, 98584 bytes de programa y 15380 de RAM.
- Auditoría de rúbrica: 7 componentes, 126 declaraciones, 77 cabeceras de fichero, 7 criterios y 8 prompts. Es una verificación estática limitada, no un analizador semántico completo.

### Test de funcionamiento obligatorio

✅ Julia confirmó beacon encontrado y subida inmediata de 1234 al servidor.
La web mostró ID 81, tipo 11, valor 1234 y 16/10/2026 01:53:27 Europe/Madrid.
Una lectura posterior de API devolvió ID 270, tipo 11 y valor 1234.
Captura propia: doc/evidencias/web-morada-publicada.jpg. La primera fila web es
la última medición: el servidor ordena por fecha e ID descendentes.
La prueba debe poder repetirse en la defensa; compilación o captura no la sustituyen.

## Comparación y límites pendientes

Se consultó JoanCataSend/PBIO-O3-GTI3A-2026, commit 7ac3a8270b8d598e486c95d57820ba741b7794a7.
La organización de aceptación, prompts y ramas se usa como referencia. Sus datos,
autoría y decisiones de esquema/BLE no son los de este proyecto.

- ❌ Frecuencia histórica: crear las ramas ahora no demuestra contribuciones periódicas anteriores.
  Remediación: mostrar el historial auténtico y conservar el flujo de trabajo futuro; no inventar commits.
- ❌ Tareas obligatorias de PoliformaT: sus enunciados y entregas no se han aportado.
  Remediación: contrastar y completar esas tareas; el repositorio por sí solo no acredita su entrega.
- La trazabilidad corresponde a la revisión actual y su ampliación real del verificador; no reconstruye el uso inicial de IA.
- No se detectan otros incumplimientos técnicos/documentales en el alcance revisado. No es una garantía de nota ni de requisitos de otros sprints.

La placa conserva la medida deliberadamente ficticia 1234. No se afirma calibración
ni lectura real de un gas. Los respaldos SQL/archivos anteriores permanecen fuera
de httpdocs; la migración ya aplicada no debe repetirse en Plesk.
