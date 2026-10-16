# Arquitectura, patrones y correspondencia

## Flujo

Placa (iBeacon) -> Android/GUI -> LogicaFake -> PeticionarioREST -> REST -> Logica -> medidas.
GUI web -> proxy listarMedidas -> REST -> Logica -> medidas.

## Patrones utilizados

- Arquitectura por capas: comunicación depende de negocio y negocio de persistencia; negocio no depende de HTTP.
- Proxy remoto: las GUI consumen firmas de dominio; LogicaFake y el proxy web encapsulan la comunicación.
- Adaptador: PeticionarioREST traduce datos de dominio a HTTPS/JSON; atenderPeticion adapta HTTP a negocio.
- Inyección de dependencias: transporte, reloj y ejecutor del proxy Android, cliente de lectura y documento de GUI en las pruebas.
- Ejecución serial y estado de operación: evita envíos distintos simultáneos y comparte anuncios duplicados en curso.

## Fuentes canónicas

| Diseño | Código |
| --- | --- |
| firmware_design.md | src/firmware/HolaMundoIBeacon/ |
| android_design.md | src/android/Prueba2025/app/src/main/java/org/jordi/prueba2025/ |
| frontend_business_logic_design.md | src/frontend_business_logic/medidas.mjs y LogicaFake/MedidaEntrada/Medida de Android |
| communication_design.md | src/communication/ReglasREST.php; servidor/public/api/index.php configura su entrada |
| business_logic_design.md | src/business_logic/Logica.php |
| database_design.md | src/database/crear.sql |
| gui_design.md | src/gui/Aplicacion.html y medidas.mjs |

Los archivos públicos de servidor/public/ux son copias generadas; no se editan
como una segunda implementación. Los dos adaptadores PHP de servidor/ requieren
las fuentes de src/. scripts/preparar_plesk.py empaqueta esas fuentes para Plesk.
Los ejemplos de examples/ no son componentes activos del producto.
