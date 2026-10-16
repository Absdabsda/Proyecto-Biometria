# Prompt de revisión — business_logic

Fecha: 16/10/2026. Uso: revisión oficial de una implementación existente.

Lee primero doc/business_logic_design.md, doc/architecture.md y context/Software_Engineering_Spec.md.
Revisa src/business_logic/Logica.php a partir de ese diseño y aplica únicamente las correcciones de
documentación descritas abajo. Conserva las interfaces, datos y funcionamiento.

## Contrato que debe conservarse

entrada:MedidaEntrada --> guardarMedida() --> medida:Medida; limit:N --> listarMedidas() --> medidas:[Medida]. Validar tipo 0..255 y valor -32768..32767. Sin dependencias de comunicación.

## Cambios solicitados

1. Comprueba que código, firmas lógicas y diseño coincidan; documenta diferencias reales.
2. Completa cada cabecera de fichero propio/adaptado: nombre, descripción concreta,
copyright limitado a aportaciones propias, fecha real de revisión, autor y aportación.
3. Conserva las atribuciones originales y los bloques de cada función con diseño
lógico, descripción y parámetros. No introduzcas comentarios de bloque anidados.
4. No añadas credenciales ni cambies el modelo mínimo ni los identificadores BLE.
5. Regenera las copias públicas con el generador; no crees implementaciones paralelas.

## Aceptación

Ejecuta la auditoría y servidor/tests/rest.php y test/backend_integration.php. Si una comprobación no puede ejecutarse,
regístrala como pendiente. No declares pruebas físicas que no se hayan realizado.
