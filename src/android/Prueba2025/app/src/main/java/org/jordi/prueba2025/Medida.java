package org.jordi.prueba2025;

import java.time.Instant;

/** Medida persistida, con identificador decimal textual y fecha de recepción UTC. */
public final class Medida {
    public final String medidaID;
    public final MedidaEntrada entrada;
    public final String fecha;

    /** -------------------
     * medida_id:Text, entrada:MedidaEntrada, fecha:Text --> Medida() --> Medida
     * Descripción: conserva el resultado confirmado por el servidor.
     * Parámetros: ID decimal positivo, medida original y fecha ISO 8601 UTC.
     * Errores: datos incompletos, ID o fecha inválidos.
     * ------------------- */
    public Medida(String medidaID, MedidaEntrada entrada, String fecha) {
        if (medidaID == null || !medidaID.matches("[1-9][0-9]*") || entrada == null || fecha == null || !fecha.endsWith("Z")) {
            throw new IllegalArgumentException("Respuesta de medida inválida");
        }
        Instant.parse(fecha);
        this.medidaID = medidaID;
        this.entrada = entrada;
        this.fecha = fecha;
    }
}
