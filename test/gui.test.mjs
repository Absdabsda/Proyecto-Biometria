import test from 'node:test';
import assert from 'node:assert/strict';
import { iniciarPantalla } from '../src/gui/medidas.mjs';

/** -------------------
 * Descripción: construye un documento aislado para comprobar los estados de la GUI.
 * Diseño: pantalla() --> PantallaDePrueba
 * Parámetros: ninguno. Cada llamada crea nodos nuevos sin estado compartido.
 * ------------------- */
function pantalla() {
    const nodo = () => ({textContent: '', disabled: false, children: [],
        appendChild(child) { this.children.push(child); },
        replaceChildren() { this.children = []; },
        addEventListener() {}});
    const elementos = Object.fromEntries(['estado', 'medidas', 'actualizar'].map(id => [id, nodo()]));
    return {getElementById: id => elementos[id], createElement: nodo, elementos};
}

test('la pantalla presenta 1234 y mantiene el identificador grande como texto', async () => {
    const doc = pantalla();
    const app = iniciarPantalla(doc, async limite => {
        assert.equal(limite, 50);
        return [{medidaID: '9007199254740993', fecha: '2026-10-16T00:00:00.000Z', tipo: 11, valor: 1234}];
    });
    await app.lista;
    const celdas = doc.elementos.medidas.children[0].children;
    assert.equal(celdas[0].textContent, '9007199254740993');
    assert.equal(celdas[3].textContent, '1234');
    assert.equal(doc.elementos.actualizar.disabled, false);
});

test('distingue una base vacía de un fallo y permite recuperar la consulta', async () => {
    const doc = pantalla();
    let fallo = false;
    const app = iniciarPantalla(doc, async () => { if (fallo) throw Error('red'); return []; });
    await app.lista;
    assert.match(doc.elementos.estado.textContent, /Todavía no hay/);
    fallo = true;
    await app.actualizar();
    assert.match(doc.elementos.estado.textContent, /No se pudieron/);
    fallo = false;
    await app.actualizar();
    assert.match(doc.elementos.estado.textContent, /Todavía no hay/);
});

test('no superpone consultas ni conserva filas al fallar la actualización', async () => {
    const doc = pantalla();
    let completar;
    let llamadas = 0;
    const app = iniciarPantalla(doc, () => { llamadas++; return new Promise(resolve => { completar = resolve; }); });
    await app.actualizar();
    assert.equal(llamadas, 1);
    completar([{medidaID: '1', fecha: '2026-10-16T00:00:00.000Z', tipo: 11, valor: 1234}]);
    await app.lista;
    assert.equal(doc.elementos.medidas.children.length, 1);
    const fallida = iniciarPantalla(doc, async () => { throw Error(); });
    await fallida.lista;
    assert.equal(doc.elementos.medidas.children.length, 0);
});
