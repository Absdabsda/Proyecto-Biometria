package org.jordi.prueba2025;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/** Adaptación de la clase de Jordi Bataller i Mascarell para leer los datos del fabricante. */
public final class TramaIBeacon {
    public static final int FABRICANTE_APPLE = 0x004c;
    private static final byte[] UUID_PROYECTO =
            "EPSG-GTI-PROY-3A".getBytes(StandardCharsets.US_ASCII);
    private final byte[] uuid;
    private final int major;
    private final int minor;
    private final byte txPower;

    public static boolean esIBeacon(byte[] datos) {
        // Antes de leer compruebo la cabecera 02 15 y los 23 bytes necesarios.
        return datos != null && datos.length == 23 && datos[0] == 0x02 && datos[1] == 0x15;
    }

    public TramaIBeacon(byte[] datosFabricante) {
        if (!esIBeacon(datosFabricante)) {
            throw new IllegalArgumentException("Los datos no son una trama iBeacon válida");
        }
        // El fabricante ya lo ha separado Android. Aquí empiezan tipo y longitud,
        // seguidos por UUID (16), Major (2), Minor (2) y potencia de referencia (1).
        uuid = Arrays.copyOfRange(datosFabricante, 2, 18);
        major = Utilidades.bytesToInt(Arrays.copyOfRange(datosFabricante, 18, 20));
        minor = Utilidades.bytesToInt(Arrays.copyOfRange(datosFabricante, 20, 22));
        txPower = datosFabricante[22];
    }

    public boolean esNuestroDispositivo() {
        // Comparo los 16 bytes completos con el mismo identificador de Arduino.
        return Arrays.equals(uuid, UUID_PROYECTO);
    }

    public int getMajor() { return major; }
    public int getMinor() { return minor; }
    public int getTipoMedicion() { return major >>> 8; }
    public int getContador() { return major & 0xff; }
    public byte getTxPower() { return txPower; }
    public byte[] getUUID() { return uuid.clone(); }

    public String getDescripcionMedicion() {
        // Conservo el código 11 para la placa del proyecto. Antes se llamaba
        // CO2 en el programa, aunque el sensor instalado mide O3.
        switch (getTipoMedicion()) {
            case 11:
                return "O₃: " + minor + " ppm\nTemperatura: no enviada";
            case 12:
                // Recupero el signo, porque la temperatura puede ser negativa.
                return "Temperatura: " + (short) minor + " °C";
            default:
                return "Medición tipo " + getTipoMedicion() + ": " + minor + " (valor sin unidad definida)";
        }
    }
}
