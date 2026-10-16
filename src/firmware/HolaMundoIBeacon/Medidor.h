/**
 * Nombre: Medidor.h
 * Descripción: Medidor simulado con valores de ejemplo; no realiza adquisición física.
 * Copyright: 2026 Julia Valén de Oliveira, solo aportaciones propias; se conservan derechos y atribuciones anteriores.
 * Fecha: 2026-10-16 (revisión; fechas originales conservadas si constan).
 * Autor: Julia Valén de Oliveira (aportaciones); base docente según atribuciones conservadas.
 * Aportación: Adaptación e integración del Sprint 0 y cabeceras formales; atribuciones docentes conservadas.
 */
/** Medidor: proporciona valores constantes de ejemplo; no realiza adquisición física. */
// -*- mode: c++ -*-

#ifndef MEDIDOR_H_INCLUIDO
#define MEDIDOR_H_INCLUIDO

// ------------------------------------------------------
// ------------------------------------------------------
class Medidor {

  // .....................................................
  // .....................................................
private:

public:

  // .....................................................
  // constructor
  // .....................................................
  /** -------------------
   * Descripción: Construye el medidor de ejemplo sin adquisición física.
   * Diseño: Medidor() --> Medidor
   * Parámetros: ninguno.
   * ------------------- */
  Medidor(  ) {
  } // ()

  // .....................................................
  // .....................................................
  /** -------------------
   * Descripción: Reserva la inicialización del medidor simulado.
   * Diseño: iniciarMedidor()
   * Parámetros: ninguno.
   * ------------------- */
  void iniciarMedidor() {
	// las cosas que no se puedan hacer en el constructor, if any
  } // ()

  // .....................................................
  // .....................................................
  /** -------------------
   * Descripción: Devuelve la constante de demostración 235; no lee un sensor.
   * Diseño: medirCO2() --> Z
   * Parámetros: ninguno.
   * ------------------- */
  int medirCO2() {
	return 235;
  } // ()

  // .....................................................
  // .....................................................
  /** -------------------
   * Descripción: Devuelve la constante de demostración -12; no lee un sensor.
   * Diseño: medirTemperatura() --> Z
   * Parámetros: ninguno.
   * ------------------- */
  int medirTemperatura() {
	return -12; // qué frío !
  } // ()
	
}; // class

// ------------------------------------------------------
// ------------------------------------------------------
// ------------------------------------------------------
// ------------------------------------------------------
#endif
