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
   * Diseño: mensaje:T --> escribir()
   * Parámetros: mensaje:T.
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
