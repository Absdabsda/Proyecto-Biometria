/**
 * Nombre: medidas.test.mjs
 * Descripción: Pruebas automáticas del contrato medidas.test del Sprint 0.
 * Copyright: 2026 Julia Valén de Oliveira, solo aportaciones propias; se conservan derechos y atribuciones anteriores.
 * Fecha: 2026-10-16 (revisión; fechas originales conservadas si constan).
 * Autor: Julia Valén de Oliveira (aportaciones); base docente según atribuciones conservadas.
 * Aportación: Verificación de casos válidos, límites, errores y aislamiento de pruebas.
 */
/** Pruebas aisladas del proxy: contrato, límites y errores con transporte inyectado sin red real. */
import test from 'node:test';
import assert from 'node:assert/strict';
import { obtenerMedidas, listarMedidas } from '../../src/frontend_business_logic/medidas.mjs';

test('interfaz pública coincide con listarMedidas del servidor', () => {
    assert.equal(obtenerMedidas, listarMedidas);
});

test('consulta REST y conserva 1234', async () => {
    const fila = { medidaID: '9007199254740993', uuid: 'EPSG-GTI-PROY-3A', gas: 11, contador: 1, valor: 1234, fecha: '2026-10-06T10:00:00.000Z' };
    const resultado = await obtenerMedidas(50, async url => {
        assert.equal(url, '../api/index.php?ruta=medidas&limit=50');
        return { ok: true, json: async () => [fila] };
    });
    assert.equal(resultado[0].valor, 1234);
    assert.equal(resultado[0].medidaID, '9007199254740993');
});

test('rechaza registros fuera del contrato sin perder precisión de identificadores', async () => {
    const fila = { medidaID: '1', uuid: 'EPSG-GTI-PROY-3A', gas: 11, contador: 1, valor: 1234, fecha: '2026-10-06T10:00:00.000Z' };
    for (const cambio of [{medidaID: 1}, {medidaID: '0'}, {tipo: -1}, {gas: -1},
        {gas: 256}, {tipo: 256}, {valor: -32769}, {valor: 32768}, {fecha: 'inválida'}]) {
        await assert.rejects(listarMedidas(50, async () => ({ok: true, json: async () => [{...fila, ...cambio}]})));
    }
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

test('normaliza la respuesta publicada y la MVP al mismo modelo de cuatro campos', async () => {
    for (const origen of [{tipo: 11}, {uuid: 'EPSG-GTI-PROY-3A', gas: 11, contador: 93}]) {
        const resultado = await listarMedidas(1, async () => ({ok: true, json: async () => [{medidaID: '31', valor: 1234, fecha: '2026-10-08T22:28:50.690Z', ...origen}]}));
        assert.deepEqual(Object.keys(resultado[0]), ['medidaID', 'tipo', 'valor', 'fecha']);
        assert.equal(resultado[0].tipo, 11);
    }
});
