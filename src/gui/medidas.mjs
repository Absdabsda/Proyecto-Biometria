import { listarMedidas } from '../frontend_business_logic/medidas.mjs';

/** -------------------
 * documento: Pantalla, listar: ConsultaMedidas --> iniciarPantalla() --> PantallaActiva
 * Descripción: consulta medidas por el proxy y representa carga, resultado, vacío y error.
 * Parámetros: documento de la pantalla y operación de dominio inyectable para pruebas.
 * Post: cada dato se escribe como texto; una consulta fallida no deja filas antiguas.
 * ------------------- */
export function iniciarPantalla(documento, listar = listarMedidas) {
    const estado = documento.getElementById('estado');
    const cuerpo = documento.getElementById('medidas');
    const boton = documento.getElementById('actualizar');
    /** -------------------
     * actualizar() --> ()
     * Descripción: obtiene hasta 50 medidas y actualiza la pantalla sin comunicación directa.
     * Parámetros: ninguno. Errores: se muestran al usuario y permiten volver a intentar.
     * ------------------- */
    async function actualizar() {
        if (boton.disabled) return;
        boton.disabled = true;
        estado.textContent = 'Consultando medidas…';
        cuerpo.replaceChildren();
        try {
            const medidas = await listar(50);
            for (const medida of medidas) {
                const fila = documento.createElement('tr');
                for (const valor of [medida.medidaID, new Date(medida.fecha).toLocaleString('es-ES'), medida.gas, medida.valor, medida.contador]) {
                    const celda = documento.createElement('td');
                    celda.textContent = String(valor);
                    fila.appendChild(celda);
                }
                cuerpo.appendChild(fila);
            }
            estado.textContent = medidas.length ? `${medidas.length} medidas recibidas.` : 'Todavía no hay medidas guardadas.';
        } catch {
            estado.textContent = 'No se pudieron cargar las medidas. Comprueba la conexión y pulsa Actualizar.';
        } finally {
            boton.disabled = false;
        }
    }
    boton.addEventListener('click', actualizar);
    const lista = actualizar();
    return { actualizar, lista };
}

if (typeof document !== 'undefined') iniciarPantalla(document);
