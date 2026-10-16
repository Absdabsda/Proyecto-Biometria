/**
 * Nombre: firmware_contract.cpp
 * Descripción: Pruebas automáticas del contrato firmware_contract del Sprint 0.
 * Copyright: 2026 Julia Valén de Oliveira, solo aportaciones propias; se conservan derechos y atribuciones anteriores.
 * Fecha: 2026-10-16 (revisión; fechas originales conservadas si constan).
 * Autor: Julia Valén de Oliveira (aportaciones); base docente según atribuciones conservadas.
 * Aportación: Verificación de casos válidos, límites, errores y aislamiento de pruebas.
 */
/** Pruebas del Publicador real con una emisora sustituida: sin placa, radio ni esperas. */
#include <array>
#include <algorithm>
#include <cstdint>
#include <iostream>
#include <stdexcept>
#include <string>

static long esperaRegistrada = 0;

/** -------------------
 * Descripción: registra el intervalo sin detener el proceso de pruebas.
 * Diseño: tiempo:Z --> esperar()
 * Parámetros: intervalo solicitado por el publicador real.
 * ------------------- */
void esperar(long tiempo) { esperaRegistrada = tiempo; }

/** Doble de radio que conserva el último anuncio y su estado observable. */
class EmisoraBLE {
public:
    std::array<uint8_t, 16> uuid{};
    uint16_t major = 0;
    int16_t minor = 0;
    int8_t rssi = 0;
    bool encendida = false;
    bool anunciando = false;

    /** -------------------
     * Descripción: satisface la construcción del adaptador sin inicializar hardware.
     * Diseño: nombre:Text, fabricante:N, potencia:Z --> EmisoraBLE()
     * Parámetros: configuración no utilizada por este doble de prueba.
     * ------------------- */
    EmisoraBLE(const char*, uint16_t, int8_t) {}

    /** -------------------
     * Descripción: registra la activación explícita de la radio.
     * Diseño: encenderEmisora()
     * Parámetros: ninguno.
     * ------------------- */
    void encenderEmisora() { encendida = true; }

    /** -------------------
     * Descripción: copia el anuncio solicitado para verificar su contenido.
     * Diseño: uuid:[N]_16, major:N, minor:Z, potencia:Z --> emitirAnuncioIBeacon()
     * Parámetros: los campos que el Publicador entrega a Bluefruit.
     * ------------------- */
    void emitirAnuncioIBeacon(uint8_t* identificador, uint16_t cabecera, int16_t valor, int8_t potencia) {
        std::copy_n(identificador, 16, uuid.begin());
        major = cabecera;
        minor = valor;
        rssi = potencia;
        anunciando = true;
    }

    /** -------------------
     * Descripción: registra el final del anuncio de temperatura de ejemplo.
     * Diseño: detenerAnuncio()
     * Parámetros: ninguno.
     * ------------------- */
    void detenerAnuncio() { anunciando = false; }
};

#include "../src/firmware/HolaMundoIBeacon/Publicador.h"
#include "../src/firmware/HolaMundoIBeacon/Medidor.h"

/** -------------------
 * Descripción: exige una propiedad y comunica exactamente el incumplimiento.
 * Diseño: condicion:B, mensaje:Text --> verificar()
 * Parámetros: resultado esperado y explicación del contrato.
 * ------------------- */
void verificar(bool condicion, const char* mensaje) {
    if (!condicion) throw std::runtime_error(mensaje);
}

/** -------------------
 * Descripción: prueba UUID, tipo, signo, extremos, contador y permanencia del anuncio.
 * Diseño: comprobarPublicador()
 * Parámetros: ninguno; cada caso construye un publicador nuevo.
 * ------------------- */
void comprobarPublicador() {
    for (int16_t valor : {int16_t(-32768), int16_t(-1), int16_t(0), int16_t(1234), int16_t(32767)}) {
        for (uint8_t contador : {uint8_t(0), uint8_t(1), uint8_t(255)}) {
            Publicador publicador;
            verificar(!publicador.laEmisora.encendida, "El constructor no debe encender la radio.");
            publicador.encenderEmisora();
            publicador.publicarCO2(valor, contador, 3000);
            const auto& anuncio = publicador.laEmisora;
            verificar(anuncio.encendida, "La radio debe estar encendida tras setup.");
            verificar(std::string(anuncio.uuid.begin(), anuncio.uuid.end()) == "EPSG-GTI-PROY-3A", "UUID de 16 bytes incompatible con Android.");
            verificar(anuncio.major == (uint16_t(11) << 8) + contador, "Major debe empaquetar tipo 11 y contador.");
            verificar(anuncio.minor == valor, "Minor debe preservar el valor con signo.");
            verificar(anuncio.rssi == -53, "Potencia de referencia distinta del contrato.");
            verificar(anuncio.anunciando, "El anuncio de 1234 debe continuar entre iteraciones.");
            verificar(esperaRegistrada == 3000, "Intervalo de publicacion incorrecto.");
        }
    }
}

/** -------------------
 * Descripción: contrasta el ejemplo de temperatura y los valores simulados del medidor.
 * Diseño: comprobarEjemplos()
 * Parámetros: ninguno; no modifica archivos ni requiere dispositivos.
 * ------------------- */
void comprobarEjemplos() {
    Publicador publicador;
    publicador.publicarTemperatura(-12, 255, 1000);
    verificar(publicador.laEmisora.major == 0x0cff, "Tipo de temperatura o contador incorrecto.");
    verificar(publicador.laEmisora.minor == -12, "La temperatura debe conservar el signo.");
    verificar(!publicador.laEmisora.anunciando, "El ejemplo de temperatura debe detener el anuncio.");
    verificar(esperaRegistrada == 1000, "Intervalo del ejemplo de temperatura incorrecto.");
    Medidor medidor;
    medidor.iniciarMedidor();
    verificar(medidor.medirCO2() == 235 && medidor.medirTemperatura() == -12, "Las constantes del Medidor no coinciden con el diseño.");
}

/** -------------------
 * Descripción: ejecuta los contratos y devuelve un estado útil para CI.
 * Diseño: main() --> estado:Z
 * Parámetros: ninguno; cero indica que todas las propiedades se cumplen.
 * ------------------- */
int main() {
    try {
        comprobarPublicador();
        comprobarEjemplos();
        std::cout << "Firmware: 15 combinaciones de valor/contador y ejemplos correctos.\n";
        return 0;
    } catch (const std::exception& error) {
        std::cerr << error.what() << '\n';
        return 1;
    }
}
