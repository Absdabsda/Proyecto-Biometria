# Entrega del Sprint 0 — Julia Valén de Oliveira

## Estado preparado

El proyecto conserva la medida ficticia 1234. GitHub contiene diseños, comentarios,
API documentada, pruebas y clientes separados por responsabilidades. La pantalla
de consulta y el backend mínimo están publicados en Plesk.

La tabla activa solo contiene medidaID, tipo, valor y fecha. Se conservaron y
compararon las 31 medidas anteriores. UUID y contador se utilizan en BLE, sin
almacenarse en la tabla activa. Existe un respaldo privado de la base y de los archivos.

## Demostración final con placa y móvil

Realizada por Julia el 16/10/2026: beacon detectado y subidas inmediatas.
Comprobación de la web: ID 81, tipo 11, valor 1234, fecha local 01:53:27.
La siguiente secuencia permite repetir la demostración ante el profesor.

1. Instalar el APK debug actualizado.
2. Encender la placa que anuncia 1234; activar Bluetooth, ubicación y los permisos de la app.
3. Pulsar «Buscar nuestro dispositivo» y comprobar 1234, tipo 11 y el contador local.
4. Comprobar «Medida 1234 guardada en el servidor».
5. Abrir https://jvaldeo.upv.edu.es/ux/Aplicacion.html y pulsar Actualizar.
6. Verificar una fila nueva con tipo 11, valor 1234 y fecha de hoy. Conservar captura o vídeo si se solicita.

No cargar firmware para un modelo de placa no confirmado. El sketch se ha compilado
para SparkFun Pro nRF52840 Mini; esa compilación no identifica la placa física.

## Qué enseñar en la revisión

- README, author.md, doc/, src/ y test/.
- Tabla mínima y diseño de BD coincidente con la implementación.
- Backend de negocio independiente de la API; lógica fake separada en Android y web.
- Operaciones POST y GET en doc/api.md y su uso por los clientes.
- Pruebas automáticas: 19 Android, 10 web, PHP/REST, persistencia y migración.
- Comentarios de descripción, diseño y parámetros; atribuciones del material docente.

## Copia local y nuevas instalaciones

El repositorio real es Proyecto-Biometria, dentro de Sprint_0 en la carpeta que
se abrió inicialmente en Codex. La carpeta envolvente tiene otro historial y no
se debe publicar sobre el repositorio real.

Si se usa el ZIP, extraer en una carpeta sin tildes, por ejemplo
C:/Users/Julia/Downloads/Proyecto-Biometria-Sprint0, y abrir src/android/Prueba2025 con Android Studio.

La migración del alojamiento ya se aplicó: **no volver a ejecutar migrar_mvp.sql**
sobre esa misma base. Es un procedimiento de actualización de una sola ejecución
para instalaciones que aún tengan el esquema anterior.
