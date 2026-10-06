package org.jordi.prueba2025;

import org.junit.Test;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import static org.junit.Assert.*;

/** Compruebo el formato compartido con Arduino sin necesitar una conexión Bluetooth. */
public class TramaIBeaconTest {
    private byte[] anuncio(int major, int minor) {
        byte[] datos = new byte[23];
        datos[0] = 0x02;
        datos[1] = 0x15;
        byte[] uuid = "EPSG-GTI-PROY-3A".getBytes(StandardCharsets.US_ASCII);
        System.arraycopy(uuid, 0, datos, 2, uuid.length);
        datos[18] = (byte) (major >>> 8);
        datos[19] = (byte) major;
        datos[20] = (byte) (minor >>> 8);
        datos[21] = (byte) minor;
        datos[22] = -53;
        return datos;
    }

    @Test public void reconoceLosDieciseisBytesDelUuidDeArduino() {
        TramaIBeacon trama = new TramaIBeacon(anuncio(0x0bff, 42));
        assertTrue(trama.esNuestroDispositivo());
        assertEquals(16, trama.getUUID().length);
        assertEquals('A', trama.getUUID()[15]);
        assertEquals(11, trama.getTipoMedicion());
        assertEquals(255, trama.getContador());
        assertEquals(42, trama.getMinor());
        assertEquals(-53, trama.getTxPower());
        assertTrue(trama.getDescripcionMedicion().contains("O₃: 42 ppm"));
    }

    @Test public void distingueOtroUuidAunqueCoincidanLosPrimerosQuinceBytes() {
        byte[] datos = anuncio(0x0b00, 0);
        datos[17] = 1;
        assertFalse(new TramaIBeacon(datos).esNuestroDispositivo());
    }

    @Test public void rechazaDatosAusentesCortosLargosYDeOtroFormato() {
        assertFalse(TramaIBeacon.esIBeacon(null));
        for (int longitud = 0; longitud < 23; longitud++) {
            assertFalse(TramaIBeacon.esIBeacon(Arrays.copyOf(anuncio(0, 0), longitud)));
        }
        assertFalse(TramaIBeacon.esIBeacon(Arrays.copyOf(anuncio(0, 0), 24)));
        byte[] datos = anuncio(0, 0);
        datos[0] = 1;
        assertFalse(TramaIBeacon.esIBeacon(datos));
        datos[0] = 2;
        datos[1] = 0x14;
        assertFalse(TramaIBeacon.esIBeacon(datos));
    }

    @Test(expected = IllegalArgumentException.class)
    public void elConstructorRechazaUnaTramaIncompleta() { new TramaIBeacon(new byte[2]); }

    @Test public void leeMinorSinSignoYContadorAlVolverACero() {
        TramaIBeacon trama = new TramaIBeacon(anuncio(0x0b00, 65535));
        assertEquals(65535, trama.getMinor());
        assertEquals(0, trama.getContador());
    }

    @Test public void recuperaTemperaturasNegativas() {
        TramaIBeacon trama = new TramaIBeacon(anuncio(0x0c01, -12));
        assertEquals("Temperatura: -12 °C", trama.getDescripcionMedicion());
    }

    @Test public void noAsignaPpmAUnTipoDesconocido() {
        String descripcion = new TramaIBeacon(anuncio(0x0d01, 5)).getDescripcionMedicion();
        assertFalse(descripcion.contains("ppm"));
        assertTrue(descripcion.contains("sin unidad definida"));
    }

    @Test public void protegeElUuidDeCambiosExternos() {
        byte[] datos = anuncio(0x0b01, 1);
        TramaIBeacon trama = new TramaIBeacon(datos);
        datos[2] = 0;
        trama.getUUID()[0] = 0;
        assertTrue(trama.esNuestroDispositivo());
    }

    @Test public void convierteBytesEnOrdenBigEndian() {
        assertEquals(255, Utilidades.bytesToInt(new byte[]{(byte) 0xff}));
        assertEquals(32768, Utilidades.bytesToInt(new byte[]{(byte) 0x80, 0}));
        assertEquals(0x12345678, Utilidades.bytesToInt(new byte[]{0x12, 0x34, 0x56, 0x78}));
    }

    @Test(expected = IllegalArgumentException.class)
    public void rechazaConversionVacia() { Utilidades.bytesToInt(new byte[0]); }

    @Test(expected = IllegalArgumentException.class)
    public void rechazaConversionNula() { Utilidades.bytesToInt(null); }

    @Test(expected = IllegalArgumentException.class)
    public void rechazaConversionDemasiadoLarga() { Utilidades.bytesToInt(new byte[5]); }
}
