<?php
declare(strict_types=1);
require_once __DIR__ . '/../logica/Logica.php';
require_once __DIR__ . '/../servidorREST/ReglasREST.php';

/** condicion:B --> comprobar() --> ()
 * Pre: prueba calculada. Post: falla explícitamente si es falsa. Errores: RuntimeException. */
function comprobar(bool $condicion): void {
    if (!$condicion) { throw new RuntimeException('Prueba fallida.'); }
}
final class LogicaPrueba {
    /** entrada:MedidaEntrada --> guardarMedida() --> medida:Medida
     * Pre: entrada válida. Post: resultado simulado; no prueba MySQL. Errores: validación. */
    public function guardarMedida(array $entrada): array {
        return ['medidaID' => '1', 'fecha' => '2026-10-06T00:00:00.000Z'] + Logica::validarMedida($entrada);
    }
    /** limit:N --> listarMedidas() --> medidas:[Medida]
     * Pre: límite válido. Post: lista simulada con tamaño acotado. Errores: ninguno. */
    public function listarMedidas(int $limit): array { return []; }
}
$fabrica = fn() => new LogicaPrueba();
$medida = ['uuid' => 'EPSG-GTI-PROY-3A', 'gas' => 11, 'valor' => 1234, 'contador' => 1];
[$estado, $fila] = atenderPeticion('POST', '/medida', 'application/json', json_encode($medida), [], $fabrica);
comprobar($estado === 201 && $fila['valor'] === 1234);
foreach ([-32768, 32767] as $valor) comprobar(Logica::validarMedida(array_replace($medida, ['valor' => $valor]))['valor'] === $valor);
foreach ([['valor' => 32768], ['contador' => 256], ['contador' => -1], ['gas' => 256], ['valor' => '1234'], ['valor' => true], ['uuid' => 'otro']] as $cambio) {
    comprobar(atenderPeticion('POST', '/medida', 'application/json', json_encode(array_replace($medida, $cambio)), [], $fabrica)[0] === 400);
}
comprobar(atenderPeticion('POST', '/medida', 'application/json', '{', [], $fabrica)[0] === 400);
comprobar(atenderPeticion('POST', '/medida', 'text/plain', '{}', [], $fabrica)[0] === 415);
comprobar(atenderPeticion('POST', '/medida', 'application/json', str_repeat(' ', 4097), [], $fabrica)[0] === 413);
comprobar(atenderPeticion('GET', '/otra', '', '', [], $fabrica)[0] === 404);
comprobar(atenderPeticion('GET', '/medida', '', '', [], $fabrica)[0] === 405);
foreach (['0', '101', '1.5', 'abc'] as $limite) comprobar(atenderPeticion('GET', '/medidas', '', '', ['limit' => $limite], $fabrica)[0] === 400);
comprobar(atenderPeticion('GET', '/medidas', '', '', [], $fabrica)[0] === 200);
comprobar(atenderPeticion('GET', '/medidas', '', '', [], function () { throw new RuntimeException('secreto'); })[0] === 500);
echo "Pruebas REST y validación correctas (sin BD real).\n";
