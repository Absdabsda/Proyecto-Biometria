# business_logic

## Component Design

```text
MedidaEntrada = (tipo: N, valor: Z)
Medida = (medida_id: Text, tipo: N, valor: Z, fecha: Text)
RegistroSQL = (medida_id: Text, tipo: N, valor: Z, fecha: Text)
```
tipo 0..255; valor -32768..32767. ID decimal positivo como texto y fecha ISO 8601 UTC.

```text
entrada: MedidaEntrada --> guardarMedida() --> medida: Medida
limit: N --> listarMedidas() --> medidas: [Medida]
entrada: MedidaEntrada, tipo_permitido: N | Ausente --> validarMedida() --> MedidaEntrada
```
`guardarMedida` valida exactamente tipo y valor, inserta una fila y recupera ID y fecha.
`listarMedidas` exige límite 1..100 (defecto 50), orden fecha e ID descendentes.
Errores: entrada inválida o persistencia no disponible.
Usa exclusivamente tabla `medidas` de database_design.md, con columnas medidaID,
tipo, valor y fecha. No almacena identificadores de placa ni contadores de anuncios.
Implementación: `src/business_logic/Logica.php`; dependencia de persistencia PDO.

```text
                                                                                    -------- Logica ----------------------------
                                                                                    | bd: Persistencia
                                                                                    | tipo_permitido: N | Ausente
                                                                                    | fila:RegistroSQL --> serializar() --x
                                                                                    | medida:Medida <--
                                                                                    |
                                                                                    |
                                bd:Persistencia, tipo_permitido:N|Ausente --> __construct() -->
                                                                                    |
                                                                                    |
                                                   entrada:MedidaEntrada --> guardarMedida() -->
                                                                  medida:Medida <--
                                                                                    |
                                                                                    |
                                                                 limit:N --> listarMedidas() <--
                                                               medidas:[Medida] <--
                                                                                    |
                                                                                    |
                         entrada:MedidaEntrada, tipo_permitido:N|Ausente --> validarMedida() --x
                                                          entrada:MedidaEntrada <--
                                                                                    -----------------------------------------------
```

Persistencia representa la conexión a las cuatro columnas del diseño de base de datos.
serializar es privado: conserva ID decimal textual y convierte la fecha de recepción a UTC.

## Design Clarifications

- El código 11 identifica el tipo del prototipo sin afirmar una identificación química.
- El filtrado y la deduplicación BLE ocurren en el móvil; no forman parte de la base.
- Las operaciones usan parámetros enlazados. ID y fecha los genera la base de datos.
- La conexión es una dependencia de construcción; las firmas públicas usan tipos de dominio.

## General Rules

- Programming Language: PHP 7.4 o superior.
- Function/Method Headers: incluir descripción, diseño lógico y parámetros en un bloque delimitado por `-------------------`.
- Code Readability: nombres claros y código autoexplicativo.
- Automated Testing: probar funciones críticas, límites, errores y persistencia de forma independiente.
