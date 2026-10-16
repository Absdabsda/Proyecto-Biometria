/**
 * Nombre: PuertoSerie.h
 * Descripción: Apoyo del firmware para radio, servicios, diagnóstico o señalización de la placa.
 * Copyright: 2026 Julia Valén de Oliveira, solo aportaciones propias; se conservan derechos y atribuciones anteriores.
 * Fecha: 2026-10-16 (revisión; fechas originales conservadas si constan).
 * Autor: Julia Valén de Oliveira (aportaciones); base docente según atribuciones conservadas.
 * Aportación: Adaptación e integración del Sprint 0 y cabeceras formales; atribuciones docentes conservadas.
 */
/** PuertoSerie: encapsula la inicialización y escritura del puerto de diagnóstico. */

// -*- mode: c++ -*-

// ----------------------------------------------------------
// Jordi Bataller i Mascarell
// 2019-07-07
// ----------------------------------------------------------

#ifndef PUERTO_SERIE_H_INCLUIDO
#define PUERTO_SERIE_H_INCLUIDO

// ----------------------------------------------------------
// ----------------------------------------------------------
class PuertoSerie  {

public:
  // .........................................................
  // .........................................................
  /** -------------------
   * Descripción: Configura la velocidad del puerto serie.
   * Diseño: baudios:Z --> PuertoSerie() --> PuertoSerie
   * Parámetros: baudios:Z.
   * ------------------- */
  PuertoSerie (long baudios) {
	Serial.begin( baudios );
	// mejor no poner esto aquí: while ( !Serial ) delay(10);   
  } // ()

  // .........................................................
  // .........................................................
  /** -------------------
   * Descripción: Espera a que el puerto serie esté disponible.
   * Diseño: esperarDisponible()
   * Parámetros: ninguno.
   * ------------------- */
  void esperarDisponible() {

	while ( !Serial ) {
	  delay(10);   
	}

  } // ()

  // .........................................................
  // .........................................................
  template<typename T>
  /** -------------------
   * Descripción: Escribe el mensaje en el puerto serie.
   * Diseño: mensaje:Elemento --> escribir()
   * Parámetros: mensaje:Elemento.
   * ------------------- */
  void escribir (T mensaje) {
	Serial.print( mensaje );
  } // ()
  
}; // class PuertoSerie

// ----------------------------------------------------------
// ----------------------------------------------------------
// ----------------------------------------------------------
// ----------------------------------------------------------
#endif
