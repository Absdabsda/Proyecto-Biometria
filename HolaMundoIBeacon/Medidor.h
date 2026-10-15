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
