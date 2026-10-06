package org.jordi.prueba2025;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import org.json.JSONObject;

/** Cliente REST: solo transporta HTTP, sin acceso a la base de datos. */
public final class PeticionarioREST {
    /** url:Text, cuerpo:Text --> enviar() --> codigo:N
     * Pre: hilo secundario, URL HTTPS. Post: realiza un POST JSON.
     * Errores: IOException por red; devuelve el estado HTTP del servidor. */
    public int enviar(String destino, JSONObject datos) throws IOException {
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
            byte[] cuerpo = datos.toString().getBytes(StandardCharsets.UTF_8);
            conexion.setFixedLengthStreamingMode(cuerpo.length);
            try (OutputStream salida = conexion.getOutputStream()) { salida.write(cuerpo); }
            return conexion.getResponseCode();
        } finally { conexion.disconnect(); }
    }
}
