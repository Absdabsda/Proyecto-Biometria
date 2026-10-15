# Preparar la entrega del Sprint 0

Autora: Julia Valén de Oliveira. Demostración: medida ficticia **1234**.

## Lo que ya está comprobado

- Android compila y sus 19 pruebas automáticas pasan desde una ruta sin tildes.
- El firmware compila para SparkFun Pro nRF52840 Mini con Adafruit nRF52 1.7.0.
- Pasan 10 pruebas web y las pruebas PHP y MySQL de GitHub Actions.
- El servidor publicado responde a GET y contiene medidas 1234 antiguas.

## Demostración que debes realizar con tu placa y tu móvil

1. Confirma el modelo de placa antes de cargar el firmware. No se ha cargado nada desde esta revisión.
2. Instala el APK debug nuevo. Activa Bluetooth, ubicación y los permisos que solicite Android.
3. Pulsa «Buscar nuestro dispositivo». Debe aparecer el valor 1234 y su contador.
4. Comprueba el mensaje «Medida 1234 guardada en el servidor».
5. Consulta `https://jvaldeo.upv.edu.es/api/index.php?ruta=medidas&limit=5` y compara
   el tipo, valor y fecha de una fila nueva. Una fila de días anteriores no sirve como prueba del envío actual.
6. Conserva capturas o vídeo de la placa, la recepción y la nueva fila si el profesor los pide.

## Web y alojamiento

`output/plesk-servidor.zip` se genera con `python scripts/preparar_plesk.py`.
Incluye `httpdocs/ux/Aplicacion.html`, su módulo y el proxy, junto con la API y
la lógica privada. Sigue `servidor/README.md`. No sobrescribas tu `config.php`
privado con la plantilla: conserva los datos de conexión actuales.

Si solo necesitas añadir la pantalla al servidor ya funcional, copia los tres
archivos generados de `httpdocs/ux/` conservando su estructura. Después abre
`https://jvaldeo.upv.edu.es/ux/Aplicacion.html` y pulsa Actualizar.
La propuesta no se despliega en Plesk por el mero hecho de subirla a GitHub.

## GitHub y pruebas

Los cambios parten del historial real de `Absdabsda/Proyecto-Biometria`, no del
repositorio envolvente abierto originalmente en Codex. Usa la rama o PR de revisión
que acompaña la entrega. No copies la carpeta envolvente `Sprint_0` sobre la raíz de GitHub.

Para trabajar con Android en este Windows, extrae el ZIP en una carpeta **sin tildes**,
por ejemplo `C:/Users/Julia/Downloads/Proyecto-Biometria-Sprint0`, y abre `Prueba2025`
en Android Studio. El SDK local se configura en Android Studio y no se publica.

Los diseños están en `doc/`, los tests en `test/`, `servidor/tests/` y
`Prueba2025/app/src/test/`. El informe explica las verificaciones y límites sin
atribuir a las pruebas automáticas una demostración física que no se ha observado.

## Aclaración del modelo mínimo viable

La autora confirmó que se exige tipo de medida. La tabla activa queda en medidaID, tipo, valor y fecha, sin UUID ni contador. La migración del alojamiento aún requiere acceso a phpMyAdmin y subida del backend coordinada; las consultas actuales siguen usando el esquema anterior. Ver bd/README.md y bd/migrar_mvp.sql.
