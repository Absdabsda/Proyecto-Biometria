/**
 * Nombre: PeticionarioREST.java
 * Descripción: Adaptador HTTPS/JSON para guardar una medida y validar la confirmación.
 * Copyright: 2026 Julia Valén de Oliveira, solo aportaciones propias; se conservan derechos y atribuciones anteriores.
 * Fecha: 2026-10-16 (revisión; fechas originales conservadas si constan).
 * Autor: Julia Valén de Oliveira (aportaciones); base docente según atribuciones conservadas.
 * Aportación: Integración de recepción, proxy y datos del Sprint 0; documentación y comprobaciones.
 */
package org.jordi.prueba2025;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import org.json.JSONObject;

/** Adaptador de comunicación: traduce medidas de dominio a la API REST. */
public final class PeticionarioREST implements LogicaFake.Transporte {
    private final String destino;
    /** -------------------
     * destino:Text --> PeticionarioREST() --> Adaptador
     * Descripción: conserva la dirección configurada.
     * Parámetros: endpoint HTTPS de escritura.
     * ------------------- */
    public PeticionarioREST(String destino) { this.destino = destino; }

    /** -------------------
     * entrada:MedidaEntrada --> guardarMedida() --> Medida
     * Descripción: realiza POST JSON y valida la medida confirmada por el servidor.
     * Parámetros: medida de dominio. Pre: ejecutar fuera del hilo principal.
     * Errores: red, estado distinto de 201, respuesta >4096 bytes o datos inconsistentes.
     * ------------------- */
    @Override public Medida guardarMedida(MedidaEntrada entrada) throws Exception {
        URL url = new URL(destino);
        if (!"https".equals(url.getProtocol())) throw new IOException("Se requiere HTTPS");
        HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
        try {
            conexion.setRequestMethod("POST");
            conexion.setConnectTimeout(10000);
            conexion.setReadTimeout(10000);
            conexion.setInstanceFollowRedirects(false);
            conexion.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            conexion.setRequestProperty("Accept", "application/json");
            conexion.setDoOutput(true);
            JSONObject datos = new JSONObject();
            datos.put("uuid", entrada.uuid);
            datos.put("gas", entrada.gas);
            datos.put("valor", entrada.valor);
            datos.put("contador", entrada.contador);
            byte[] cuerpo = datos.toString().getBytes(StandardCharsets.UTF_8);
            conexion.setFixedLengthStreamingMode(cuerpo.length);
            try (OutputStream salida = conexion.getOutputStream()) { salida.write(cuerpo); }
            if (conexion.getResponseCode() != 201) throw new IOException("El servidor no confirmó la inserción");
            ByteArrayOutputStream respuesta = new ByteArrayOutputStream();
            try (InputStream flujo = conexion.getInputStream()) {
                byte[] bloque = new byte[512];
                int leidos;
                while ((leidos = flujo.read(bloque)) != -1) {
                    if (respuesta.size() + leidos > 4096) throw new IOException("Respuesta demasiado grande");
                    respuesta.write(bloque, 0, leidos);
                }
            }
            JSONObject fila = new JSONObject(new String(respuesta.toByteArray(), StandardCharsets.UTF_8));
            int tipoRecibido = fila.has("tipo") ? fila.getInt("tipo") : fila.getInt("gas");
            if (entrada.gas != tipoRecibido || entrada.valor != fila.getInt("valor")) {
                throw new IOException("La respuesta no coincide con la medida enviada");
            }
            return new Medida(fila.getString("medidaID"), entrada, fila.getString("fecha"));
        } finally { conexion.disconnect(); }
    }
}
