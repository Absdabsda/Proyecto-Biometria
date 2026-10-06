<?php
declare(strict_types=1);

/** metodo:Text, ruta:Text, tipo:Text, cuerpo:Text, query:Objeto, fabrica:Funcion
 * --> atenderPeticion() --> (estado:N, respuesta:Objeto|[Medida])
 * Pre: fábrica devuelve Logica o equivalente. Post: adapta HTTP sin SQL.
 * Errores: devuelve códigos HTTP estables; no expone detalles internos. */
function atenderPeticion(string $metodo, string $ruta, string $tipo, string $cuerpo, array $query, callable $fabrica): array {
    if (!in_array($ruta, ['/medida', '/medidas'], true)) {
        return [404, ['codigo' => 'NO_ENCONTRADO', 'mensaje' => 'Ruta no encontrada.']];
    }
    if (($ruta === '/medida' && $metodo !== 'POST') || ($ruta === '/medidas' && $metodo !== 'GET')) {
        return [405, ['codigo' => 'METODO_NO_PERMITIDO', 'mensaje' => 'Método no permitido.']];
    }
    try {
        if ($metodo === 'POST') {
            if (strtolower(trim(explode(';', $tipo)[0])) !== 'application/json') {
                return [415, ['codigo' => 'TIPO_NO_ADMITIDO', 'mensaje' => 'Se requiere application/json.']];
            }
            if (strlen($cuerpo) > 4096) {
                return [413, ['codigo' => 'CUERPO_DEMASIADO_GRANDE', 'mensaje' => 'Petición demasiado grande.']];
            }
            $objeto = json_decode($cuerpo, false, 16, JSON_THROW_ON_ERROR);
            if (!is_object($objeto)) { throw new InvalidArgumentException('Se requiere un objeto JSON.'); }
            return [201, $fabrica()->guardarMedida((array) $objeto)];
        }
        $limite = $query['limit'] ?? '50';
        if (!is_string($limite) || !preg_match('/^[1-9][0-9]{0,2}$/D', $limite) || (int) $limite > 100) {
            throw new InvalidArgumentException('El límite debe ser un entero entre 1 y 100.');
        }
        return [200, $fabrica()->listarMedidas((int) $limite)];
    } catch (JsonException $error) {
        return [400, ['codigo' => 'JSON_INVALIDO', 'mensaje' => 'El JSON no es válido.']];
    } catch (InvalidArgumentException $error) {
        return [400, ['codigo' => 'DATOS_INVALIDOS', 'mensaje' => $error->getMessage()]];
    } catch (Throwable $error) {
        error_log('PBIO: fallo interno al atender una petición.');
        return [500, ['codigo' => 'ERROR_SERVIDOR', 'mensaje' => 'No se pudo acceder a las mediciones.']];
    }
}
