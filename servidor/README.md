# Servidor de medidas: Plesk, PHP y MariaDB

## Decisiones y correspondencia con los diseños

El backend utiliza PHP 7.4 o superior, compatible con el alojamiento Plesk. Las fuentes canónicas están en `src/communication/` y `src/business_logic/`: comunicación adapta HTTP y negocio valida y accede mediante SQL parametrizado. `public/api/index.php` configura el servidor y la conexión. Los archivos de `servidorREST/` y `logica/` son adaptadores para la ejecución local; el generador de despliegue empaqueta las fuentes canónicas. UUID y contador se descartan antes de persistir.

## Modelo mínimo viable

La base activa guarda solo medidaID, tipo, valor y fecha. El UUID y el contador
se usan localmente en BLE; la capa de comunicación acepta el formato antiguo,
convierte gas a tipo y descarta los metadatos antes de persistir.
La tabla publicada se migró el 16/10/2026. Una instalación anterior nueva necesita `bd/migrar_mvp.sql` antes de usar este backend.
Exportar primero la base y detener la captura durante el cambio; seguir `bd/README.md`.
No ejecutar solo crear.sql sobre la tabla antigua: no migra sus columnas.

## Subida a Plesk

Necesitas PHP habilitado y extensión `pdo_mysql`, base `jvaldeo_pbio` con la tabla del archivo `bd/crear.sql` y HTTPS válido.

En el Administrador de archivos del dominio:

Generar desde la raíz con `python scripts/preparar_plesk.py`. El nuevo paquete `output/plesk-servidor.zip` contiene `httpdocs` y `pbio_privado`. Puedes subirlo y extraerlo en la carpeta que contiene `httpdocs`; antes revisa que no tengas ya `api/index.php` con otro contenido. Después renombra `pbio_privado/config.example.php` como `config.php` y configura la contraseña. El paquete no contiene credenciales reales e incluye la pantalla de consulta de medidas.

1. Entra en `httpdocs` y sube/descomprime el contenido de `public`: debe quedar `httpdocs/api/index.php` , `httpdocs/ux/logicaFake/medidas.mjs`, `httpdocs/ux/Aplicacion.html` y `httpdocs/ux/medidas.js`. No subas todo el repositorio ni Android.
2. Vuelve a la carpeta que contiene `httpdocs` y crea `pbio_privado` a su lado.
3. Dentro de `pbio_privado`, extrae las carpetas `logica` y `servidorREST` del paquete generado; contienen la implementación canónica de `src/`, no los adaptadores locales.
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

La pantalla visible está en `ux/Aplicacion.html`; consume el proxy de medidas y permite actualizar la tabla. Sus fuentes canónicas están en `src/gui/`.

## Probar después de subir

Abre `https://jvaldeo.upv.edu.es/api/index.php?ruta=medidas&limit=50`: debe devolver `[]` si está vacío, o una lista JSON. Esto consulta datos reales; no inventa medidas.

En Windows, para probar un POST desde PowerShell:

```powershell
$medidaPrueba = @{ tipo=11; valor=1234 } | ConvertTo-Json
Invoke-RestMethod -Method Post -Uri 'https://jvaldeo.upv.edu.es/api/index.php?ruta=medida' -ContentType 'application/json; charset=utf-8' -Body $medidaPrueba
```

Ese POST guarda una fila real de prueba: anota su medidaID para identificarla. No prueba BLE. Para la prueba completa, instala la aplicación Android del proyecto src/android/Prueba2025, recibe el beacon en el teléfono y observa «Medida 1234 guardada en el servidor». Consulta GET o phpMyAdmin y verifica que coincida.

## Contrato y límites

Rutas lógicas: POST `/medida`, GET `/medidas?limit=50`. Con `.htaccess` funcionan como `/api/medida` y `/api/medidas`. Para evitar depender de rewrite en Plesk, los clientes usan `/api/index.php?ruta=medida` y `/api/index.php?ruta=medidas&limit=50`; el significado es el mismo.

POST acepta tipo y valor y responde 201 con ID, tipo, valor y fecha; admite el anuncio antiguo como compatibilidad sin almacenarlo. GET responde 200 con lista ordenada por fecha e ID descendentes. Errores: 400 validación/JSON, 404 ruta, 405 método, 413 tamaño, 415 tipo, 500 acceso interno. El cuerpo se limita a 4096 bytes. No se habilita CORS: navegador del mismo origen; Android no necesita CORS.

Es un prototipo sin autenticación de escritura. Quien conozca el endpoint puede enviar medidas; no proporciona atribución verificable de una placa. Antes de darle uso real habría que definir autenticación y límites de uso.

Android deduplica por gas/contador/valor del UUID del proyecto durante 60 s tras éxito y limita a una petición en curso. Tras fallo vuelve a intentarlo cuando recibe otro anuncio, con separación mínima de 10 s. No hay almacenamiento offline ni recuperación de medidas tras cerrar la app. Un timeout después de que el servidor inserte puede producir una repetición al reintentar; el contrato actual no incluye una clave de idempotencia.

## Pruebas

```text
php tests/rest.php
node --test tests/medidas.test.mjs
```

Las pruebas PHP verifican adaptación HTTP y validaciones con lógica simulada, sin BD. Las de navegador prueban su cliente con fetch simulado. La persistencia y la migración se prueban con MySQL en CI. La pantalla dispone de pruebas de estados y actualización; Plesk y el recorrido físico requieren su entorno real.

Verificación del 16/10/2026: 10 pruebas web y 19 pruebas Android superadas; APK generado. Sintaxis PHP, pruebas REST y persistencia con MySQL real superadas en GitHub Actions. GET del alojamiento responde con medidas 1234 antiguas; se migró el esquema preservando las 31 filas antiguas; no se insertaron medidas desde el agente. La prueba física nueva placa-móvil-servidor sigue pendiente.
