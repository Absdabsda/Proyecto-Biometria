/**
 * Nombre: Medida.java
 * Descripción: Resultado persistido con ID textual y fecha UTC.
 * Copyright: 2026 Julia Valén de Oliveira, solo aportaciones propias; se conservan derechos y atribuciones anteriores.
 * Fecha: 2026-10-16 (revisión; fechas originales conservadas si constan).
 * Autor: Julia Valén de Oliveira (aportaciones); base docente según atribuciones conservadas.
 * Aportación: Integración de recepción, proxy y datos del Sprint 0; documentación y comprobaciones.
 */
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
