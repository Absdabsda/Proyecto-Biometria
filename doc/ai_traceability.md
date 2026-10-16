# Uso de IA y trazabilidad real

## Alcance y cronología

Julia solicitó ayuda para revisar y corregir su Sprint 0. Durante la sesión se
revisaron el repositorio, el material docente, los diseños, el código y las pruebas;
Julia realizó la prueba física. La primera revisión se hizo con una rúbrica
distinta que Julia sustituyó el 16/10/2026 por la rúbrica oficial del Sprint 0.

Los prompts de doc/prompts/ se escriben en esta revisión oficial, después de la
implementación inicial y a partir de los diseños ya existentes. Son instrucciones
de revisión y de ampliación de las comprobaciones, no una transcripción de los
prompts antiguos ni una prueba de que todo el código se generó inicialmente con ellos.
El nuevo prompt 08 guía la ampliación de la auditoría automática en esta revisión.

## Cadena de revisión

Tras recibir el enunciado de PoliformaT se preparan además seis especificaciones
de generación autocontenidas en doc/especificaciones_entrega/. Incorporan los
diseños y las directrices solicitadas. Su finalidad es la entrega y la reproducción
del código vigente; no se presentan como los prompts históricos de generación.

| Diseño previo/documento | Prompt de esta revisión | Implementación o resultado revisado | Verificación |
| --- | --- | --- | --- |
| database_design.md | prompts/01_database.md | Esquema de cuatro campos y metadatos de cabecera | Integración y migración MySQL/MariaDB |
| business_logic_design.md | prompts/02_business_logic.md | Logica.php y cabeceras completas | PHP/REST, límites y persistencia |
| communication_design.md | prompts/03_communication.md | Adaptación REST y entrada de servidor | Contratos HTTP y errores |
| android_design.md | prompts/04_android.md | GUI/BLE, proxy y comentarios | 19 pruebas y compilación APK |
| frontend_business_logic_design.md | prompts/05_frontend.md | Proxy web e interfaz de cliente documentada | Tests de proxy y paridad |
| gui_design.md | prompts/06_gui.md | Pantalla morada y cabeceras | Tests de carga, vacío, error y recuperación |
| firmware_design.md | prompts/07_firmware.md | Publicador, auxiliares y cabeceras | Contrato C++ y compilación Arduino |
| acceptance_test.md, rubrica_correcta_revision.md | prompts/08_auditoria.md | Nuevos chequeos de rúbrica en scripts/verificar_revision.py | Ejecución local y CI |

Los commits separan primero los diseños/prompts y después su aplicación. Los
resultados se revisan mediante pruebas y lectura del código; el repositorio del
compañero se utiliza como referencia de organización, sin atribuirle código a Julia.
No se incluyen contraseñas, no se inventan pruebas físicas ni commits pasados.
La revisión automática no sustituye que Julia entienda y pueda explicar la solución.
