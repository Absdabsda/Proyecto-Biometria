<?php
declare(strict_types=1);
require_once __DIR__ . '/../logica/Logica.php';
require_once __DIR__ . '/../servidorREST/ReglasREST.php';

/** -------------------
 * condicion:B --> comprobar()
 * Descripción: exige que se cumpla el comportamiento esperado.
 * Parámetros: condición de la prueba. Errores: RuntimeException.
 * ------------------- */
function comprobar(bool $condicion): void {
    if (!$condicion) { throw new RuntimeException('Prueba fallida.'); }
}
/** Doble de negocio: permite verificar REST sin conexión a una base de datos. */
final class LogicaPrueba {
    /** -------------------
     * entrada:MedidaEntrada --> guardarMedida() --> Medida
     * Descripción: devuelve el modelo MVP usando la validación real, sin base de datos.
     * Parámetros: tipo y valor de dominio.
     * ------------------- */
    public function guardarMedida(array $entrada): array {
        return ['medidaID' => '1', 'fecha' => '2026-10-16T00:00:00.000Z'] + Logica::validarMedida($entrada);
    }
    /** -------------------
     * limit:N --> listarMedidas() --> [Medida]
     * Descripción: simula una base vacía. Parámetros: límite validado por comunicación.
     * ------------------- */
    public function listarMedidas(int $limit): array { return []; }
}
$fabrica = fn() => new LogicaPrueba();
$medida = ['tipo' => 11, 'valor' => 1234];
$anuncioAnterior = ['uuid' => 'EPSG-GTI-PROY-3A', 'gas' => 11, 'valor' => 1234, 'contador' => 1];
foreach ([$medida, $anuncioAnterior] as $entrada) {
    [$estado, $fila] = atenderPeticion('POST', '/medida', 'application/json', json_encode($entrada), [], $fabrica);
    comprobar($estado === 201 && $fila['valor'] === 1234 && $fila['tipo'] === 11);
    comprobar(array_keys($fila) === ['medidaID', 'fecha', 'tipo', 'valor']);
}
foreach ([-32768, 32767] as $valor) comprobar(Logica::validarMedida(array_replace($medida, ['valor' => $valor]))['valor'] === $valor);
foreach ([['valor' => 32768], ['tipo' => -1], ['tipo' => 256], ['valor' => '1234'], ['valor' => true], ['uuid' => 'otro'], ['extra' => 1]] as $cambio) {
    comprobar(atenderPeticion('POST', '/medida', 'application/json', json_encode(array_replace($medida, $cambio)), [], $fabrica)[0] === 400);
}
foreach ([['contador' => 256], ['contador' => -1], ['gas' => 256], ['uuid' => 'otro']] as $cambio) {
    comprobar(atenderPeticion('POST', '/medida', 'application/json', json_encode(array_replace($anuncioAnterior, $cambio)), [], $fabrica)[0] === 400);
}
foreach (['{', '[]', 'null', '{"tipo":11}'] as $json) comprobar(atenderPeticion('POST', '/medida', 'application/json', $json, [], $fabrica)[0] === 400);
comprobar(atenderPeticion('POST', '/medida', 'text/plain', '{}', [], $fabrica)[0] === 415);
comprobar(atenderPeticion('POST', '/medida', 'application/json', str_repeat(' ', 4097), [], $fabrica)[0] === 413);
comprobar(atenderPeticion('GET', '/otra', '', '', [], $fabrica)[0] === 404);
comprobar(atenderPeticion('GET', '/medida', '', '', [], $fabrica)[0] === 405);
foreach (['0', '101', '1.5', 'abc', ['50']] as $limite) comprobar(atenderPeticion('GET', '/medidas', '', '', ['limit' => $limite], $fabrica)[0] === 400);
comprobar(atenderPeticion('GET', '/medidas', '', '', [], $fabrica)[0] === 200);
comprobar(atenderPeticion('GET', '/medidas', '', '', [], function () { throw new RuntimeException('simulado'); })[0] === 500);
echo "Pruebas REST correctas: modelo MVP y compatibilidad sin persistir metadatos.\n";
