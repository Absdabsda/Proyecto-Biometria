package org.jordi.prueba2025;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.LongSupplier;

/** Proxy de dominio: comparte guardarMedida con el backend y delega la red a un adaptador. */
public final class LogicaFake {
    /** Adaptador sustituible: persiste una entrada y devuelve la medida confirmada. */
    public interface Transporte {
        /** -------------------
         * entrada:MedidaEntrada --> guardarMedida() --> Medida
         * Descripción: contrato del adaptador de persistencia remota.
         * Parámetros: medida validada. Errores: comunicación o respuesta inválida.
         * ------------------- */
        Medida guardarMedida(MedidaEntrada entrada) throws Exception;
    }
    private final ExecutorService red;
    private final LongSupplier reloj;
    private final Transporte transporte;
    private final Map<String, Intento> intentos = new HashMap<>();
    private Intento pendiente;
    private boolean cerrada;

    /** Estado local de una operación: resultado compartido, instante y confirmación. */
    private static final class Intento {
        final CompletableFuture<Medida> resultado = new CompletableFuture<>();
        long instante;
        boolean exito;
        /** -------------------
         * instante:N --> Intento() --> Intento
         * Descripción: registra una operación y su instante monotónico.
         * Parámetros: milisegundos del reloj de la sesión.
         * ------------------- */
        Intento(long instante) { this.instante = instante; }
    }

    /** -------------------
     * destino:Text --> LogicaFake() --> LogicaFake
     * Descripción: configura adaptador remoto, cola serial y reloj monotónico.
     * Parámetros: endpoint de escritura del servidor.
     * ------------------- */
    public LogicaFake(String destino) {
        this(new PeticionarioREST(destino), () -> System.nanoTime() / 1000000L, Executors.newSingleThreadExecutor());
    }

    /** -------------------
     * transporte:Repositorio, reloj:Reloj, red:Ejecutor --> LogicaFake() --> LogicaFake
     * Descripción: permite verificar el proxy sin teléfono, red ni tiempos de espera.
     * Parámetros: dependencias de implementación, excluidas de la interfaz pública de dominio.
     * ------------------- */
    LogicaFake(Transporte transporte, LongSupplier reloj, ExecutorService red) {
        this.transporte = transporte;
        this.reloj = reloj;
        this.red = red;
    }

    /** -------------------
     * entrada:MedidaEntrada --> guardarMedida() --> Medida
     * Descripción: guarda en segundo plano y devuelve la medida confirmada por el servidor.
     * Parámetros: medida de dominio; la promesa expresa la asincronía de implementación.
     * Post: una operación en curso; reutiliza éxitos durante 60 s y fallos durante 10 s.
     * Errores: cierre, operación distinta en curso o fallo del adaptador.
     * ------------------- */
    public synchronized CompletableFuture<Medida> guardarMedida(MedidaEntrada entrada) {
        if (cerrada) return fallo(new IllegalStateException("Cliente cerrado"));
        if (entrada == null) return fallo(new IllegalArgumentException("Medida ausente"));
        long ahora = reloj.getAsLong();
        intentos.entrySet().removeIf(e -> e.getValue().resultado.isDone()
                && ahora - e.getValue().instante >= (e.getValue().exito ? 60000 : 10000));
        String clave = entrada.uuid + ":" + entrada.gas + ":" + entrada.contador + ":" + entrada.valor;
        Intento anterior = intentos.get(clave);
        if (anterior != null) return anterior.resultado;
        if (pendiente != null) return fallo(new IllegalStateException("Hay otra medida en curso"));
        Intento intento = new Intento(ahora);
        intentos.put(clave, intento);
        pendiente = intento;
        red.execute(() -> ejecutar(entrada, intento));
        return intento.resultado;
    }

    /** -------------------
     * entrada:MedidaEntrada, intento:Intento --> ejecutar()
     * Descripción: ejecuta el adaptador y cierra el estado antes de avisar a consumidores.
     * Parámetros: medida y registro de la operación actual.
     * ------------------- */
    private void ejecutar(MedidaEntrada entrada, Intento intento) {
        Medida medida = null;
        Exception error = null;
        try {
            medida = transporte.guardarMedida(entrada);
            if (medida == null) throw new IllegalStateException("El adaptador no devolvió una medida");
        }
        catch (Exception fallo) { error = fallo; }
        synchronized (this) {
            pendiente = null;
            intento.exito = error == null;
            if (intento.exito) intento.instante = reloj.getAsLong();
        }
        if (error == null) intento.resultado.complete(medida);
        else intento.resultado.completeExceptionally(error);
    }

    /** -------------------
     * error:Error --> fallo() --> ResultadoFallido
     * Descripción: representa errores sin bloquear el hilo de la pantalla.
     * Parámetros: causa de dominio o estado.
     * ------------------- */
    private static CompletableFuture<Medida> fallo(Exception error) {
        CompletableFuture<Medida> resultado = new CompletableFuture<>();
        resultado.completeExceptionally(error);
        return resultado;
    }

    /** -------------------
     * cerrar()
     * Descripción: impide nuevos envíos y cancela el resultado pendiente.
     * Parámetros: ninguno. Una inserción remota ya iniciada puede finalizar.
     * ------------------- */
    public synchronized void cerrar() {
        cerrada = true;
        if (pendiente != null) pendiente.resultado.completeExceptionally(new CancellationException("Cliente cerrado"));
        red.shutdownNow();
    }
}
