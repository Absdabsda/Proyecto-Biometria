package org.jordi.prueba2025;

import org.junit.Test;
import static org.junit.Assert.*;
import java.io.IOException;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/** Pruebas independientes: transporte inyectado y reloj controlado, sin red ni espera temporal. */
public class LogicaFakeTest {
    private final MedidaEntrada entrada = new MedidaEntrada("EPSG-GTI-PROY-3A", 11, 1234, 1);

    @Test public void devuelveLaMedidaConfirmadaYDeduplicaDuranteSesentaSegundos() throws Exception {
        AtomicLong reloj = new AtomicLong();
        AtomicInteger llamadas = new AtomicInteger();
        LogicaFake cliente = new LogicaFake(dato -> {
            llamadas.incrementAndGet();
            return new Medida("9007199254740993", dato, "2026-10-16T00:00:00.000Z");
        }, reloj::get, Executors.newSingleThreadExecutor());
        try {
            CompletableFuture<Medida> primera = cliente.guardarMedida(entrada);
            Medida medida = primera.get(2, TimeUnit.SECONDS);
            assertEquals(1234, medida.entrada.valor);
            assertEquals("9007199254740993", medida.medidaID);
            reloj.set(59999);
            assertSame(primera, cliente.guardarMedida(entrada));
            reloj.set(60000);
            cliente.guardarMedida(entrada).get(2, TimeUnit.SECONDS);
            assertEquals(2, llamadas.get());
        } finally { cliente.cerrar(); }
    }

    @Test public void unFalloNoSeConsideraGuardadoYPermiteReintentoTrasDiezSegundos() throws Exception {
        AtomicLong reloj = new AtomicLong();
        AtomicInteger llamadas = new AtomicInteger();
        LogicaFake cliente = new LogicaFake(dato -> {
            if (llamadas.getAndIncrement() == 0) throw new IOException("fallo simulado");
            return new Medida("1", dato, "2026-10-16T00:00:00.000Z");
        }, reloj::get, Executors.newSingleThreadExecutor());
        try {
            CompletableFuture<Medida> primera = cliente.guardarMedida(entrada);
            try { primera.get(2, TimeUnit.SECONDS); fail(); }
            catch (ExecutionException error) { assertTrue(error.getCause() instanceof IOException); }
            reloj.set(9999);
            assertSame(primera, cliente.guardarMedida(entrada));
            reloj.set(10000);
            assertEquals(1234, cliente.guardarMedida(entrada).get(2, TimeUnit.SECONDS).entrada.valor);
            assertEquals(2, llamadas.get());
        } finally { cliente.cerrar(); }
    }

    @Test public void soloExisteUnaPeticionEnCursoYSeComparteElMismoAnuncio() throws Exception {
        CountDownLatch iniciada = new CountDownLatch(1);
        CountDownLatch liberar = new CountDownLatch(1);
        LogicaFake cliente = new LogicaFake(dato -> {
            iniciada.countDown();
            if (!liberar.await(2, TimeUnit.SECONDS)) throw new IOException("No se liberó la prueba");
            return new Medida("1", dato, "2026-10-16T00:00:00.000Z");
        }, () -> 0, Executors.newSingleThreadExecutor());
        try {
            CompletableFuture<Medida> primera = cliente.guardarMedida(entrada);
            assertTrue(iniciada.await(2, TimeUnit.SECONDS));
            assertSame(primera, cliente.guardarMedida(entrada));
            try {
                cliente.guardarMedida(new MedidaEntrada(entrada.uuid, 11, 1235, 2)).get(2, TimeUnit.SECONDS);
                fail();
            } catch (ExecutionException error) { assertTrue(error.getCause() instanceof IllegalStateException); }
            liberar.countDown();
            assertEquals(1234, primera.get(2, TimeUnit.SECONDS).entrada.valor);
        } finally { liberar.countDown(); cliente.cerrar(); }
    }

    @Test public void cerrarImpideEnviosNuevos() throws Exception {
        LogicaFake cliente = new LogicaFake(dato -> { throw new AssertionError("No debe enviar"); }, () -> 0, Executors.newSingleThreadExecutor());
        cliente.cerrar();
        try { cliente.guardarMedida(entrada).get(2, TimeUnit.SECONDS); fail(); }
        catch (ExecutionException error) { assertTrue(error.getCause() instanceof IllegalStateException); }
    }

    @Test public void contratoDeEntradaYResultadoPreservaLimitesYRechazaDatosInvalidos() {
        assertEquals(-32768, new MedidaEntrada(entrada.uuid, 0, -32768, 0).valor);
        assertEquals(32767, new MedidaEntrada(entrada.uuid, 255, 32767, 255).valor);
        for (int[] datos : new int[][]{{-1, 0, 0}, {256, 0, 0}, {11, 32768, 0}, {11, -32769, 0}, {11, 0, 256}}) {
            try { new MedidaEntrada(entrada.uuid, datos[0], datos[1], datos[2]); fail(); }
            catch (IllegalArgumentException esperado) { }
        }
        try { new MedidaEntrada("otro", 11, 1234, 1); fail(); }
        catch (IllegalArgumentException esperado) { }
        try { new Medida("0", entrada, "2026-10-16T00:00:00Z"); fail(); }
        catch (IllegalArgumentException esperado) { }
    }
}
