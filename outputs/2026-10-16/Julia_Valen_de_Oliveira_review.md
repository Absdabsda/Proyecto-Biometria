# Revisión del proyecto — Julia Valén de Oliveira

Fecha: 16/10/2026. Criterios: PDF del revisor y especificaciones del ZIP facilitados por la autora.
GitHub revisado: Absdabsda/Proyecto-Biometria, main, commit 6da03a88b272fafe49b0d6a842082549a54506bb.
Resultado: **cumplimiento parcial; no se certifica la entrega completa**.

## Verificaciones aprobadas en la propuesta corregida

- ✅ Autoría: `author.md` contiene Julia Valén de Oliveira y se conservan las atribuciones docentes.
- ✅ Diseños: se añaden diseños independientes de comunicación, negocio, BD, proxy web, Android y firmware.
- ✅ Formato documental: los seis diseños incluyen Component Design, Design Clarifications y General Rules con lenguaje, encabezados, legibilidad y pruebas.
- ✅ Backend: `src/business_logic/Logica.php` no importa ni invoca transporte; comunicación delega en negocio y este utiliza PDO y la tabla documentada.
- ✅ Base de datos: diseño con TABLE, DESCRIPTION, COLUMNS, PRIMARY KEY, FOREIGN KEYS y CONSTRAINTS, coherente con SQL.
- ✅ Proxy web: `listarMedidas(limit: N) -> [Medida]` coincide con el subconjunto de lectura del backend; se conserva el alias anterior.
- ✅ Encabezados de la lógica y comunicación PHP y del proxy web: bloques delimitados por guiones con diseño lógico.
- ✅ Pruebas ejecutadas: 6 de JavaScript y 14 Java/JUnit del parser, todas satisfactorias.
- ✅ Despliegue: paquete regenerable con configuración de ejemplo privada; las fuentes canónicas se incorporan al paquete.

## Incumplimientos y comprobaciones pendientes

- ❌ Estructura completa: Android y firmware siguen fuera de `src/`.
  Corrección: migrar los proyectos a `src/android/` y `src/firmware/`, actualizando rutas y verificando sus compilaciones.
- ❌ Paridad del proxy Android: `enviarMedida(trama)` no coincide con `guardarMedida(entrada)` del backend.
  Corrección: definir un DTO de dominio y una interfaz equivalente; mantener la asincronía como detalle del adaptador.
- ❌ Encabezados de Android y firmware: no todos los métodos incluyen el diseño lógico con delimitadores exigidos.
  Corrección: documentar todas las funciones de producción y contrastarlas con sus diseños.
- ❌ Cobertura crítica incompleta: no hay pruebas ejecutadas de persistencia, ciclo de vida Android, envío/deduplicación ni firmware.
  Corrección: añadir y ejecutar pruebas con MySQL de prueba, entorno Android y placa; ejecutar las pruebas PHP existentes.
- ❌ Interfaz web de medidas: solo existe el proxy y ejemplos docentes; falta pantalla propia.
  Corrección: implementar la pantalla conforme al enunciado, consumiendo exclusivamente el proxy.
- ❌ Sensor físico: el loop activo transmite 1234 ficticio; los métodos de Medidor también son simulados.
  Corrección: si el sprint exige lectura real, conectar y calibrar el sensor con sus especificaciones. Confirmar gas y unidades.
- ❌ Validación de entrega: falta el enunciado y número del sprint; no es posible confirmar el alcance ni si PHP está permitido.
  Corrección: contrastar requisitos funcionales y tecnología con el enunciado real antes de dar por terminada la entrega.

## Límites de la revisión

No se ejecutó PHP, MySQL, compilación Android completa, prueba física BLE ni prueba contra Plesk.
Las modificaciones se preparan localmente; este informe no prueba que estén publicadas en main.
El repositorio abierto en Codex y el de GitHub tienen historiales y estructuras distintos; la propuesta para GitHub debe partir de su main real.
