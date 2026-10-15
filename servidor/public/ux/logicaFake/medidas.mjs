/** -------------------
 * limit:N, fetchImpl:Funcion --> listarMedidas() --> medidas:[Medida]
 * Pre: limit entero 1..100. Post: datos reales de REST; no toca el DOM.
 * Errores: límite, red, HTTP o respuesta inválida.
 * ------------------- */
export async function listarMedidas(limit = 50, fetchImpl = fetch) {
    if (!Number.isInteger(limit) || limit < 1 || limit > 100) throw new Error('El límite debe estar entre 1 y 100.');
    let respuesta;
    try { respuesta = await fetchImpl(`../api/index.php?ruta=medidas&limit=${limit}`, { headers: { Accept: 'application/json' }, cache: 'no-store' }); }
    catch { throw new Error('No se pudo conectar con el servidor.'); }
    if (!respuesta.ok) throw new Error('No se pudieron consultar las mediciones.');
    let medidas;
    try { medidas = await respuesta.json(); } catch { throw new Error('La respuesta del servidor no es válida.'); }
    if (!Array.isArray(medidas) || medidas.some(m => !m || m.uuid !== 'EPSG-GTI-PROY-3A'
        || typeof m.medidaID !== 'string' || !/^[1-9][0-9]*$/.test(m.medidaID)
        || !Number.isInteger(m.valor) || m.valor < -32768 || m.valor > 32767
        || !Number.isInteger(m.gas) || m.gas < 0 || m.gas > 255
        || !Number.isInteger(m.contador) || m.contador < 0 || m.contador > 255 || typeof m.fecha !== 'string'
        || !Number.isFinite(Date.parse(m.fecha)))) throw new Error('Los datos recibidos no son válidos.');
    return medidas;
}

// Compatibilidad con consumidores anteriores.
export const obtenerMedidas = listarMedidas;
