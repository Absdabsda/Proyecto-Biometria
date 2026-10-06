package org.jordi.prueba2025;

/** Conversiones usadas por la trama, a partir de las utilidades de Jordi Bataller i Mascarell. */
public final class Utilidades {
    private Utilidades() { }

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
