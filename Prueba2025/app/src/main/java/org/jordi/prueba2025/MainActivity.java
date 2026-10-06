package org.jordi.prueba2025;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Recibo los anuncios de la placa y muestro sus medidas sin conectarme a ella. */
public class MainActivity extends AppCompatActivity {
    private static final String ETIQUETA_LOG = "GTI3A";
    private static final int CODIGO_PETICION_PERMISOS = 1122;
    private static final int MAX_DISPOSITIVOS = 100;
    private static final long INTERVALO_PANTALLA_MS = 500;
    private BluetoothLeScanner elEscanner;
    private ScanCallback callbackDelEscaneo;
    private TextView textoResultados;
    private TextView textoEstado;
    private TextView textoEnvio;
    private LogicaFake logicaFake;
    private boolean soloNuestroDispositivo;
    private boolean actualizacionPendiente;
    private final Handler manejador = new Handler(Looper.getMainLooper());

    // Uso la dirección como clave: cada anuncio actualiza su fila y no crea otra copia.
    private final Map<String, String> dispositivosDetectados = new LinkedHashMap<>();
    private final Runnable actualizarPantalla = () -> {
        actualizacionPendiente = false;
        StringBuilder texto = new StringBuilder("Dispositivos detectados: ")
                .append(dispositivosDetectados.size()).append("\n\n");
        for (String dispositivo : dispositivosDetectados.values()) {
            texto.append(dispositivo).append("\n\n");
        }
        textoResultados.setText(texto.toString());
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textoResultados = findViewById(R.id.textoResultados);
        textoEstado = findViewById(R.id.textoEstado);
        textoEnvio = findViewById(R.id.textoEnvio);
        logicaFake = new LogicaFake(getString(R.string.url_servidor_medida));
        // Primero pido los permisos; la búsqueda empieza cuando pulso un botón.
        if (!tengoPermisos()) {
            pedirPermisos();
        }
    }

    // Los dos botones comparten el mismo proceso. Solo cambia el filtro de resultados.
    public void botonBuscarDispositivosBTLEPulsado(View vista) { iniciarBusqueda(false); }
    public void botonBuscarNuestroDispositivoBTLEPulsado(View vista) { iniciarBusqueda(true); }
    public void botonDetenerBusquedaDispositivosBTLEPulsado(View vista) {
        detenerBusqueda();
        textoEstado.setText(R.string.busqueda_detenida);
    }

    private void iniciarBusqueda(boolean soloNuestro) {
        // Paro la búsqueda anterior para no registrar dos escaneos al cambiar de botón.
        detenerBusqueda();
        if (!tengoPermisos()) {
            pedirPermisos();
            return;
        }
        if (!prepararEscanner()) { return; }
        soloNuestroDispositivo = soloNuestro;
        dispositivosDetectados.clear();
        textoResultados.setText(soloNuestro ? R.string.buscando_nuestro : R.string.buscando_todos);
        textoEstado.setText(R.string.busqueda_activa);
        callbackDelEscaneo = new ScanCallback() {
            @Override
            public void onScanResult(int tipo, ScanResult resultado) {
                recibirResultado(this, resultado);
            }
            @Override
            public void onBatchScanResults(List<ScanResult> resultados) {
                // También proceso los resultados si el móvil los entrega en un lote.
                for (ScanResult resultado : resultados) { recibirResultado(this, resultado); }
            }
            @Override
            public void onScanFailed(int codigo) {
                ScanCallback origen = this;
                runOnUiThread(() -> {
                    if (callbackDelEscaneo != origen) { return; }
                    detenerBusqueda();
                    textoEstado.setText(getString(R.string.error_busqueda, codigo));
                });
            }
        };
        ScanSettings ajustes = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).setReportDelay(0).build();
        try {
            // El nombre puede no venir en el anuncio. Compruebo el UUID al recibirlo.
            elEscanner.startScan(null, ajustes, callbackDelEscaneo);
        } catch (SecurityException | IllegalStateException error) {
            detenerBusqueda();
            textoEstado.setText(R.string.error_inicio);
            Log.e(ETIQUETA_LOG, "No se pudo iniciar el escaneo", error);
        }
    }

    private void recibirResultado(ScanCallback origen, ScanResult resultado) {
        runOnUiThread(() -> {
            // Descarto los avisos que llegan tarde de una búsqueda ya detenida.
            if (callbackDelEscaneo == origen) { mostrarDispositivo(resultado); }
        });
    }

    private void mostrarDispositivo(ScanResult resultado) {
        ScanRecord registro = resultado.getScanRecord();
        if (registro == null) { return; }
        // Android ya separa el fabricante. Así no dependo de posiciones fijas
        // dentro del paquete BLE completo, donde puede haber otros campos antes.
        byte[] datos = registro.getManufacturerSpecificData(TramaIBeacon.FABRICANTE_APPLE);
        TramaIBeacon trama = TramaIBeacon.esIBeacon(datos) ? new TramaIBeacon(datos) : null;
        boolean esNuestro = trama != null && trama.esNuestroDispositivo();
        if (soloNuestroDispositivo && !esNuestro) { return; }
        String nombre = registro.getDeviceName();
        if (nombre == null || nombre.isEmpty()) {
            nombre = esNuestro ? "Julia Beacon" : "(sin nombre)";
        }
        String direccion = resultado.getDevice().getAddress();
        String informacion = "Nombre: " + nombre + "\nDirección: " + direccion
                + "\nRSSI: " + resultado.getRssi() + " dBm";
        if (esNuestro) {
            informacion += "\n" + trama.getDescripcionMedicion()
                    + "\nContador: " + trama.getContador()
                    + "\nMajor: " + trama.getMajor() + " | Minor: " + trama.getMinor();
            textoEstado.setText(R.string.recibiendo_nuestro);
            // Envío solo el gas del prototipo actual; la placa también anuncia temperatura.
            if (trama.getTipoMedicion() == 11) {
                logicaFake.enviarMedida(trama, mensaje -> textoEnvio.setText(mensaje));
            }
        }
        dispositivosDetectados.put(direccion, informacion);
        if (dispositivosDetectados.size() > MAX_DISPOSITIVOS) {
            dispositivosDetectados.remove(dispositivosDetectados.keySet().iterator().next());
        }
        // Agrupo los anuncios durante medio segundo. La última lectura siempre
        // se dibuja, aunque después no llegue ningún otro anuncio.
        if (!actualizacionPendiente) {
            actualizacionPendiente = true;
            manejador.postDelayed(actualizarPantalla, INTERVALO_PANTALLA_MS);
        }
    }

    private boolean prepararEscanner() {
        BluetoothManager gestor = getSystemService(BluetoothManager.class);
        BluetoothAdapter adaptador = gestor == null ? null : gestor.getAdapter();
        if (adaptador == null) {
            textoEstado.setText(R.string.sin_bluetooth);
            return false;
        }
        try {
            if (!adaptador.isEnabled()) {
                textoEstado.setText(R.string.activar_bluetooth);
                startActivity(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE));
                return false;
            }
            // Mantengo ubicación habilitada para recibir beacons sin usar
            // neverForLocation, que puede filtrar algunos anuncios iBeacon.
            LocationManager ubicacion = getSystemService(LocationManager.class);
            if (ubicacion == null || !ubicacion.isLocationEnabled()) {
                textoEstado.setText(R.string.activar_ubicacion);
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                return false;
            }
            elEscanner = adaptador.getBluetoothLeScanner();
            if (elEscanner == null) {
                textoEstado.setText(R.string.sin_escaner);
                return false;
            }
            return true;
        } catch (SecurityException error) {
            textoEstado.setText(R.string.revisar_permisos);
            return false;
        }
    }

    private String[] permisosNecesarios() {
        // En Android 12 o superior añado los permisos de dispositivos cercanos.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return new String[]{Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION};
        }
        return new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION};
    }

    private boolean tengoPermisos() {
        // Compruebo todos; que se conceda el primero no garantiza los demás.
        for (String permiso : permisosNecesarios()) {
            if (ContextCompat.checkSelfPermission(this, permiso) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void pedirPermisos() {
        textoEstado.setText(R.string.pedir_permisos);
        ActivityCompat.requestPermissions(this, permisosNecesarios(), CODIGO_PETICION_PERMISOS);
    }

    @Override
    public void onRequestPermissionsResult(int codigo, String[] permisos, int[] resultados) {
        super.onRequestPermissionsResult(codigo, permisos, resultados);
        if (codigo == CODIGO_PETICION_PERMISOS) {
            textoEstado.setText(tengoPermisos()
                    ? R.string.permisos_concedidos
                    : R.string.faltan_permisos);
        }
    }

    private void detenerBusqueda() {
        ScanCallback anterior = callbackDelEscaneo;
        callbackDelEscaneo = null;
        manejador.removeCallbacks(actualizarPantalla);
        if (actualizacionPendiente) { actualizarPantalla.run(); }
        if (anterior != null && elEscanner != null) {
            try {
                elEscanner.stopScan(anterior);
            } catch (SecurityException | IllegalStateException error) {
                // El usuario puede apagar Bluetooth o retirar permisos mientras busco.
                Log.w(ETIQUETA_LOG, "Bluetooth dejó de estar disponible", error);
            }
        }
        elEscanner = null;
    }

    @Override
    protected void onDestroy() {
        logicaFake.cerrar();
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        // Dejo de usar Bluetooth cuando la pantalla de la aplicación ya no se ve.
        detenerBusqueda();
        textoEstado.setText(R.string.busqueda_pausada);
        super.onStop();
    }
}

