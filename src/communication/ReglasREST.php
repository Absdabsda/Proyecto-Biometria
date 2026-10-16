<?php
/**
 * Nombre: ReglasREST.php
 * Descripción: Adaptación REST de peticiones a operaciones del negocio y respuestas de protocolo.
 * Copyright: 2026 Julia Valén de Oliveira, solo aportaciones propias; se conservan derechos y atribuciones anteriores.
 * Fecha: 2026-10-16 (revisión; fechas originales conservadas si constan).
 * Autor: Julia Valén de Oliveira (aportaciones); base docente según atribuciones conservadas.
 * Aportación: Contrato GET/POST, compatibilidad y tratamiento de errores sin exponer credenciales.
 */

declare(strict_types=1);

/** -------------------
 * Descripción: traduce las peticiones REST a operaciones de negocio y sus resultados a HTTP.
 * Parámetros: método, ruta, tipo de contenido, cuerpo JSON, consulta y fábrica de negocio.
 * metodo:Text, ruta:Text, tipo:Text, cuerpo:Text, query:Objeto, fabrica:Funcion
 * --> atenderPeticion() --> (estado:N, respuesta:Objeto|[Medida])
 * Pre: fábrica devuelve Logica o equivalente. Post: adapta HTTP sin SQL.
 * Errores: devuelve códigos HTTP estables; no expone detalles internos.
 * ------------------- */
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
            $entrada = (array) $objeto;
            if (array_key_exists('uuid', $entrada) || array_key_exists('gas', $entrada) || array_key_exists('contador', $entrada)) {
                $claves = array_keys($entrada);
                sort($claves);
                if ($claves !== ['contador', 'gas', 'uuid', 'valor'] || $entrada['uuid'] !== 'EPSG-GTI-PROY-3A'
                    || !is_int($entrada['contador']) || $entrada['contador'] < 0 || $entrada['contador'] > 255) {
                    throw new InvalidArgumentException('Anuncio de compatibilidad inválido.');
                }
                $entrada = ['tipo' => $entrada['gas'], 'valor' => $entrada['valor']];
            }
            return [201, $fabrica()->guardarMedida($entrada)];
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
