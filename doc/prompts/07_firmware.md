# Prompt de revisión — firmware

Fecha: 16/10/2026. Uso: revisión oficial de una implementación existente.

Lee primero doc/firmware_design.md, doc/architecture.md y context/Software_Engineering_Spec.md.
Revisa src/firmware/HolaMundoIBeacon/ a partir de ese diseño y aplica únicamente las correcciones de
documentación descritas abajo. Conserva las interfaces, datos y funcionamiento.

## Contrato que debe conservarse

Mantener MEDIDA_DE_PRUEBA=1234. Major=(tipo11<<8)+contador; minor conserva signo. Publicador mantiene anuncio entre iteraciones. Helpers y comentarios coinciden con los diagramas de clases.

## Cambios solicitados

1. Comprueba que código, firmas lógicas y diseño coincidan; documenta diferencias reales.
2. Completa cada cabecera de fichero propio/adaptado: nombre, descripción concreta,
copyright limitado a aportaciones propias, fecha real de revisión, autor y aportación.
3. Conserva las atribuciones originales y los bloques de cada función con diseño
lógico, descripción y parámetros. No introduzcas comentarios de bloque anidados.
4. No añadas credenciales ni cambies el modelo mínimo ni los identificadores BLE.
5. Regenera las copias públicas con el generador; no crees implementaciones paralelas.

## Aceptación

Ejecuta la auditoría y test/firmware_contract.cpp y compilación completa de Arduino. Si una comprobación no puede ejecutarse,
regístrala como pendiente. No declares pruebas físicas que no se hayan realizado.
