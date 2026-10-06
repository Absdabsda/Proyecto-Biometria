package org.jordi.prueba2025;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.json.JSONObject;

/** Convierte las lecturas en peticiones; la persistencia pertenece al servidor. */
public final class LogicaFake {
    public interface Estado { void mostrar(String mensaje); }
    private final ExecutorService red = Executors.newSingleThreadExecutor();
    private final Handler principal = new Handler(Looper.getMainLooper());
    private final Map<String, Long> enviados = new HashMap<>();
    private final Map<String, Long> intentos = new HashMap<>();
    private final String destino;
    private boolean pendiente;
    private volatile boolean cerrada;

    /** destino:Text --> LogicaFake() --> cliente:LogicaFake
     * Pre: endpoint HTTPS. Post: crea una cola serial. Errores: ninguno. */
    public LogicaFake(String destino) { this.destino = destino; }

    /** trama:TramaIBeacon, estado:Funcion --> enviarMedida() --> ()
     * Pre: llamada en hilo principal, trama del proyecto.
     * Post: una petición fuera del hilo principal, deduplicación 60 s tras éxito.
     * Errores: estado legible; reintento al recibir otro anuncio tras 10 s.
     * No mantiene una cola persistente cuando la app se cierra. */
    public void enviarMedida(TramaIBeacon trama, Estado estado) {
        if (cerrada || !trama.esNuestroDispositivo() || pendiente) return;
        long ahora = SystemClock.elapsedRealtime();
        enviados.entrySet().removeIf(e -> ahora - e.getValue() >= 60000);
        intentos.entrySet().removeIf(e -> ahora - e.getValue() >= 10000);
        String clave = trama.getTipoMedicion() + ":" + trama.getContador() + ":" + trama.getValor();
        if (enviados.containsKey(clave) || intentos.containsKey(clave)) return;
        intentos.put(clave, ahora);
        pendiente = true;
        estado.mostrar("Enviando medida " + trama.getValor() + "...");
        red.execute(() -> {
            boolean exito = false;
            String mensaje;
            try {
                JSONObject datos = new JSONObject();
                datos.put("uuid", "EPSG-GTI-PROY-3A");
                datos.put("gas", trama.getTipoMedicion());
                datos.put("valor", trama.getValor());
                datos.put("contador", trama.getContador());
                int codigo = new PeticionarioREST().enviar(destino, datos);
                exito = codigo == 201;
                mensaje = exito ? "Medida " + trama.getValor() + " guardada en el servidor"
                    : "No se guardó la medida. Respuesta HTTP " + codigo;
            } catch (Exception error) {
                mensaje = "No se pudo enviar. Revisa Internet y la dirección del servidor.";
            }
            final boolean guardada = exito;
            final String resultado = mensaje;
            principal.post(() -> {
                pendiente = false;
                if (cerrada) return;
                if (guardada) enviados.put(clave, SystemClock.elapsedRealtime());
                estado.mostrar(resultado);
            });
        });
    }

    /** () --> cerrar() --> ()
     * Pre: actividad terminada. Post: evita nuevos envíos y avisos a esa pantalla.
     * Errores: una petición iniciada puede finalizar en el servidor. */
    public void cerrar() { cerrada = true; red.shutdownNow(); }
}
