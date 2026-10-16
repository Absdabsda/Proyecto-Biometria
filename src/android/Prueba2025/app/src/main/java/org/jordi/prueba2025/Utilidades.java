/**
 * Nombre: Utilidades.java
 * Descripción: Conversiones big endian de los bytes recibidos.
 * Copyright: 2026 Julia Valén de Oliveira, solo aportaciones propias; se conservan derechos y atribuciones anteriores.
 * Fecha: 2026-10-16 (revisión; fechas originales conservadas si constan).
 * Autor: Julia Valén de Oliveira (aportaciones); base docente según atribuciones conservadas.
 * Aportación: Integración de recepción, proxy y datos del Sprint 0; documentación y comprobaciones.
 */
package org.jordi.prueba2025;

/** Conversiones usadas por la trama, a partir de las utilidades de Jordi Bataller i Mascarell. */
public final class Utilidades {
    /** -------------------
     * Descripción: Impide crear instancias de la clase de conversiones.
     * Diseño: Utilidades() --> Utilidades
     * Parámetros: ninguno.
     * ------------------- */
    private Utilidades() { }

    /** -------------------
     * Descripción: Combina entre uno y cuatro bytes en orden big endian; rechaza entrada vacía.
     * Diseño: bytes:[N] --> bytesToInt() --> Z
     * Parámetros: bytes:[N].
     * ------------------- */
    public static int bytesToInt(byte[] bytes) {
        // Major y Minor llegan en orden big endian: primero el byte de mayor peso.
        // Quito el signo de cada byte para leer FF FF como 65535.
        if (bytes == null || bytes.length == 0 || bytes.length > 4) {
            throw new IllegalArgumentException("Necesito entre uno y cuatro bytes");
        }
        int resultado = 0;
        for (byte valor : bytes) {
            resultado = (resultado << 8) | (valor & 0xff);
        }
        // Con cuatro bytes conservo el patrón binario del int de Java.
        return resultado;
    }
}
