# Revisión del Sprint 0 — Julia Valén de Oliveira

Fecha: 16/10/2026. Segunda auditoría: criterios del AGENTS.md aportado, sus dos
especificaciones de context/ y la rúbrica. Alcance funcional: valor ficticio 1234.
Base de revisión de código: commit 14c3482 y PR #4; informe actualizado después.

## Comprobaciones del agente

| Estado | Requisito | Evidencia y resultado |
| --- | --- | --- |
| ✅ | Autoría y estructura | author.md identifica a Julia Valén de Oliveira; README, doc/, src/ y test/ existen. Los siete componentes activos tienen carpeta y diseño con el mismo nombre. |
| ✅ | Formato de los diseños | Todos los *_design.md contienen Component Design, Design Clarifications y General Rules; indican lenguaje, cabeceras delimitadas, legibilidad y pruebas. |
| ✅ | Notación lógica | Tipos N, Z, B, Text, agregaciones y colecciones; diagramas ASCII de clases, estado privado y operaciones públicas, separación y flechas de interacción. Se completaron los adaptadores y auxiliares de Android y firmware. |
| ✅ | Tríada del backend | communication_design.md, business_logic_design.md y database_design.md separados. |
| ✅ | Diseño de base de datos | TABLE, DESCRIPTION, COLUMNS, PRIMARY KEY, FOREIGN KEYS y CONSTRAINTS. Modelo activo: medidaID, tipo, valor y fecha; coincide con src/database/crear.sql y bd/crear.sql. |
| ✅ | Independencia de negocio | Logica.php valida tipo/valor y utiliza PDO/SQL parametrizado; no conoce HTTP, rutas ni el componente de comunicación. El diseño identifica tabla y columnas. |
| ✅ | Flujo de dependencias | La entrada REST configura el adaptador; comunicación invoca guardarMedida/listarMedidas; negocio accede a la base. No hay dependencia inversa. |
| ✅ | Separación frontend | La GUI web usa exclusivamente el proxy; MainActivity delega guardarMedida en LogicaFake. Los adaptadores encapsulan la comunicación. |
| ✅ | Paridad de interfaces | Web y negocio comparten limit:N --> listarMedidas() --> medidas:[Medida]. Android y negocio comparten entrada:MedidaEntrada --> guardarMedida() --> medida:Medida. Asincronía e inyección son detalles de implementación documentados. |
| ✅ | Cabeceras | La auditoría léxica identifica 126 declaraciones con bloques delimitados, descripción, diseño y parámetros. Se completaron alReves, la conversión de servicio y getConexion. Los callbacks anónimos quedan dentro de sus operaciones documentadas. |
| ✅ | Pruebas críticas | Validación y REST, persistencia real y migración MySQL/MariaDB, proxy/GUI web, parser/proxy Android y contrato del Publicador de Arduino. |
| ✅ | Informe | Guardado en outputs/2026-10-16/Julia_Valen_de_Oliveira_review.md, con el nombre de la autora y estados explícitos. |

Los ejemplos docentes se conservan en examples/ con atribuciones y están
identificados como referencia, fuera de los componentes activos y del despliegue.
scripts/ y .github/ contienen herramientas de construcción y comprobación.
skills/ y memory/ del ZIP pertenecen al entorno del revisor y están vacíos;
no son componentes de la aplicación que necesiten diseños de producto.

## Evidencias funcionales y de la rúbrica

- ✅ 10 pruebas web; 19 Android (14 parser y 5 proxy/dominio), con APK compilado.
- ✅ CI verifica sintaxis PHP, REST, persistencia y migración en MySQL 8.4 y MariaDB 10.11.
- ✅ Publicador real probado con una radio sustituida: 15 combinaciones de valor/contador, UUID de 16 bytes, tipo 11, signo, intervalo y continuidad del anuncio; también temperatura y Medidor.
- ✅ Pruebas de persistencia aisladas: crean y eliminan su base desechable; no alteran tablas ni secuencias AUTO_INCREMENT de la base original. La migración tiene su base independiente y limpieza en finally.
- ✅ Auditoría estática reproducible: python scripts/verificar_revision.py; comprueba estructura, documentos, contratos, capas, cabeceras, copias generadas y ausencia de configuración privada/binarios en archivos versionados.
- ✅ Firmware completo compilado para SparkFun Pro nRF52840 Mini con Adafruit nRF52 1.7.0: 98584 bytes de programa y 15380 de RAM. La compilación no identifica por sí sola la placa física.
- ✅ Prueba física confirmada por Julia: beacon detectado y subida inmediata. Web observada con ID 81, tipo 11, valor 1234 y fecha local 16/10/2026 01:53:27 (Europe/Madrid). No se insertó esa fila desde el agente.
- ✅ Web morada publicada, API funcional y base mínima. Las 31 filas anteriores se compararon y conservaron durante la migración. Respaldos SQL y archivos fuera de httpdocs; la tabla anterior es un respaldo temporal y no forma parte del modelo activo.
- ✅ GitHub contiene main, develop y ramas de revisión con commits y pull requests reales. No se publican config.php, contraseñas ni APK en Git.

## Pendientes y límites

- ❌ Frecuencia histórica exigida por la rúbrica: el estado actual no acredita varias contribuciones semanales previas.
  Remediación: mostrar y explicar el historial auténtico; mantener el flujo de ramas a partir de ahora. No inventar fechas ni commits.

No se detectan otros incumplimientos en los requisitos documentales y de separación
de capas revisados. Esta es una auditoría razonada apoyada por comprobaciones
automáticas, no una calificación del profesor ni una garantía de toda la semántica.
La revisión formal de GUI está excluida por el documento del agente; sus estados
web sí tienen pruebas. La medida 1234 es deliberadamente ficticia: no acredita
calibración, lectura de un sensor real ni requisitos de sprints posteriores.
