package org.jordi.prueba2025;

/** Datos de dominio compartidos con guardarMedida del backend; no contienen transporte. */
public final class MedidaEntrada {
    public final int tipo;
    // Metadatos locales para filtrar/deduplicar BLE y adaptar el servidor anterior; no se persisten.
    public final String uuid;
    public final int gas;
    public final int valor;
    public final int contador;

    /** -------------------
     * uuid:Text, gas:N, valor:Z, contador:N --> MedidaEntrada() --> MedidaEntrada
     * Descripción: construye una medida válida del proyecto.
     * Parámetros: UUID exacto, gas y contador 0..255, valor -32768..32767.
     * Errores: IllegalArgumentException si algún dato queda fuera del contrato.
     * ------------------- */
    public MedidaEntrada(String uuid, int gas, int valor, int contador) {
        if (!"EPSG-GTI-PROY-3A".equals(uuid) || gas < 0 || gas > 255 || valor < -32768
                || valor > 32767 || contador < 0 || contador > 255) {
            throw new IllegalArgumentException("Medida fuera del contrato del proyecto");
        }
        this.uuid = uuid;
        this.tipo = gas;
        this.gas = gas;
        this.valor = valor;
        this.contador = contador;
    }

    /** -------------------
     * tipo:N, valor:Z --> MedidaEntrada() --> MedidaEntrada
     * Descripción: construye la entrada de dominio del modelo mínimo viable.
     * Parámetros: tipo 0..255 y valor -32768..32767; los metadatos locales usan valores de compatibilidad.
     * ------------------- */
    public MedidaEntrada(int tipo, int valor) {
        this("EPSG-GTI-PROY-3A", tipo, valor, 0);
    }

    /** -------------------
     * trama:Trama --> desdeTrama() --> MedidaEntrada
     * Descripción: adapta una trama del proyecto a los datos de dominio.
     * Parámetros: trama iBeacon ya decodificada. Errores: UUID ajeno o trama ausente.
     * ------------------- */
    public static MedidaEntrada desdeTrama(TramaIBeacon trama) {
        if (trama == null || !trama.esNuestroDispositivo()) throw new IllegalArgumentException("Trama ajena al proyecto");
        return new MedidaEntrada("EPSG-GTI-PROY-3A", trama.getTipoMedicion(), trama.getValor(), trama.getContador());
    }
}
