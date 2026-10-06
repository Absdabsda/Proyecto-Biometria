import test from 'node:test';
import assert from 'node:assert/strict';
import { obtenerMedidas } from '../public/ux/logicaFake/medidas.mjs';

test('consulta REST y conserva 1234', async () => {
    const fila = { uuid: 'EPSG-GTI-PROY-3A', gas: 11, contador: 1, valor: 1234, fecha: '2026-10-06T10:00:00.000Z' };
    const resultado = await obtenerMedidas(50, async url => {
        assert.equal(url, '../api/index.php?ruta=medidas&limit=50');
        return { ok: true, json: async () => [fila] };
    });
    assert.equal(resultado[0].valor, 1234);
});
test('lista vacía', async () => assert.deepEqual(await obtenerMedidas(1, async () => ({ ok: true, json: async () => [] })), []));
test('límites inválidos', async () => {
    for (const limite of [0, 101, 1.5, '50']) await assert.rejects(obtenerMedidas(limite));
});
test('red, HTTP, JSON y forma inválida', async () => {
    for (const cliente of [async () => { throw Error(); }, async () => ({ok: false}),
        async () => ({ok: true, json: async () => { throw Error(); }}),
        async () => ({ok: true, json: async () => ({})})]) await assert.rejects(obtenerMedidas(50, cliente));
});
