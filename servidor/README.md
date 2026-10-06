# Servidor de medidas: Plesk, PHP y MariaDB

## Decisiones y correspondencia con los diseños

El PDF propone Node.js. Esta implementación adapta el lenguaje a PHP 7.4 o superior, siguiendo el servidor PHP del repositorio y el alojamiento Plesk. `servidorREST/ReglasREST.php` adapta HTTP; `logica/Logica.php` valida y accede mediante SQL parametrizado; `public/api/index.php` configura el servidor y la conexión. No se mezcla Bluetooth con persistencia. Pendiente validar esta adaptación en la entrega académica.

El código del gas continúa pendiente: se preserva el byte recibido. Android envía solo tipo 11 del prototipo actual; no se afirma que esté confirmado como O3. `codigo_o3=null` admite 0..255 en el servidor; cuando se confirme el código, debe fijarse en configuración, Android y SQL. Valor 1234 no está fijado en el programa: llega de Minor. La etiqueta de la medida 11 se expresa en ppb según el diseño y debe contrastarse con firmware. La fecha se asigna en BD al recibir; ID BIGINT se devuelve como texto para conservar precisión en navegador.

## Subida a Plesk

Necesitas PHP habilitado y extensión `pdo_mysql`, base `jvaldeo_pbio` con la tabla del archivo `bd/crear.sql` y HTTPS válido.

En el Administrador de archivos del dominio:

El paquete `plesk-servidor.zip` contiene `httpdocs` y `pbio_privado`. Puedes subirlo y extraerlo en la carpeta que contiene `httpdocs`; antes revisa que no tengas ya `api/index.php` con otro contenido. Después renombra `pbio_privado/config.example.php` como `config.php` y configura la contraseña. El paquete no contiene credenciales reales ni la página visible pendiente del wireframe.

1. Entra en `httpdocs` y sube/descomprime el contenido de `public`: debe quedar `httpdocs/api/index.php` y `httpdocs/ux/logicaFake/medidas.mjs`. No subas todo el repositorio ni Android.
2. Vuelve a la carpeta que contiene `httpdocs` y crea `pbio_privado` a su lado.
3. Dentro de `pbio_privado`, sube las carpetas `logica` y `servidorREST`.
4. Sube `config.example.php` a `pbio_privado`, renómbralo `config.php` y escribe tu contraseña en el campo `password`. Mantén los demás datos si coinciden con Plesk. No compartas la contraseña. Configuración y lógica quedan fuera de la carpeta pública.
5. Si aparece una restricción `open_basedir`, el administrador del alojamiento debe permitir la lectura de `pbio_privado`; no publiques config.php como solución.

Estructura final:

```text
carpeta-del-dominio/
  pbio_privado/
    config.php
    logica/Logica.php
    servidorREST/ReglasREST.php
  httpdocs/
    api/index.php
    api/.htaccess
    ux/logicaFake/medidas.mjs
```

La web visible y sus estilos se añadirán al recibir el wireframe. El archivo de conexión ya está listo para usarse desde `ux/Aplicacion.html`.

## Probar después de subir

Abre `https://jvaldeo.upv.edu.es/api/index.php?ruta=medidas&limit=50`: debe devolver `[]` si está vacío, o una lista JSON. Esto consulta datos reales; no inventa medidas.

En Windows, para probar un POST desde PowerShell:

```powershell
$medidaPrueba = @{ uuid='EPSG-GTI-PROY-3A'; gas=11; valor=1234; contador=1 } | ConvertTo-Json
Invoke-RestMethod -Method Post -Uri 'https://jvaldeo.upv.edu.es/api/index.php?ruta=medida' -ContentType 'application/json; charset=utf-8' -Body $medidaPrueba
```

Ese POST guarda una fila real de prueba: anota su medidaID para identificarla. No prueba BLE. Para la prueba completa, instala de nuevo Prueba2025, recibe el beacon en el teléfono y observa «Medida 1234 guardada en el servidor». Consulta GET o phpMyAdmin y verifica que coincida.

## Contrato y límites

Rutas lógicas: POST `/medida`, GET `/medidas?limit=50`. Con `.htaccess` funcionan como `/api/medida` y `/api/medidas`. Para evitar depender de rewrite en Plesk, los clientes usan `/api/index.php?ruta=medida` y `/api/index.php?ruta=medidas&limit=50`; el significado es el mismo.

POST acepta exactamente UUID, gas, valor y contador y responde 201 con la fila. GET responde 200 con lista ordenada por fecha e ID descendentes. Errores: 400 validación/JSON, 404 ruta, 405 método, 413 tamaño, 415 tipo, 500 acceso interno. El cuerpo se limita a 4096 bytes. No se habilita CORS: navegador del mismo origen; Android no necesita CORS.

Es un prototipo sin autenticación de escritura. Quien conozca el endpoint puede enviar medidas; no proporciona atribución verificable de una placa. Antes de darle uso real habría que definir autenticación y límites de uso.

Android deduplica por gas/contador/valor del UUID del proyecto durante 60 s tras éxito y limita a una petición en curso. Tras fallo vuelve a intentarlo cuando recibe otro anuncio, con separación mínima de 10 s. No hay almacenamiento offline ni recuperación de medidas tras cerrar la app. Un timeout después de que el servidor inserte puede producir una repetición al reintentar; el contrato actual no incluye una clave de idempotencia.

## Pruebas

```text
php tests/rest.php
node --test tests/medidas.test.mjs
```

Las pruebas PHP verifican adaptación HTTP y validaciones con lógica simulada, sin BD. Las de navegador prueban su cliente con fetch simulado. La comprobación SQL, la configuración de Plesk y el recorrido real placa-teléfono-servidor requieren el entorno real. La interfaz queda pendiente del wireframe.

Verificación local realizada: se generó el APK debug y pasaron 4 pruebas del adaptador web. Gradle no pudo cargar la clase de pruebas Android; ejecutadas directamente con Java/JUnit pasaron las 13 pruebas del parser, incluidas 1234 y los límites con signo. No hay PHP instalado localmente, por lo que las pruebas PHP no se han ejecutado. Tampoco se ha conectado al servidor ni a la BD de Plesk.
