// -*- mode: c++ -*-

#ifndef LED_H_INCLUIDO
#define LED_H_INCLUIDO

// ----------------------------------------------------------
// Jordi Bataller i Mascarell
// 2019-07-07
// ----------------------------------------------------------

// ----------------------------------------------------------
// ----------------------------------------------------------
/** -------------------
 * Descripción: Espera el intervalo indicado en milisegundos.
 * Diseño: tiempo:Z --> esperar()
 * Parámetros: tiempo:Z.
 * ------------------- */
void esperar (long tiempo) {
  delay (tiempo);
}

// ----------------------------------------------------------
// ----------------------------------------------------------
class LED {
private:
  int numeroLED;
  bool encendido;
public:

  // .........................................................
  // .........................................................
  /** -------------------
   * Descripción: Configura el pin del LED y su estado inicial.
   * Diseño: numero:Z --> LED() --> LED
   * Parámetros: numero:Z.
   * ------------------- */
  LED (int numero)
	: numeroLED (numero), encendido(false)
  {
	pinMode(numeroLED, OUTPUT);
	apagar ();
  }

  // .........................................................
  // .........................................................
  /** -------------------
   * Descripción: Activa el LED y conserva su estado encendido.
   * Diseño: encender()
   * Parámetros: ninguno.
   * ------------------- */
  void encender () {
	digitalWrite(numeroLED, HIGH); 
	encendido = true;
  }

  // .........................................................
  // .........................................................
  /** -------------------
   * Descripción: Desactiva el LED y conserva su estado apagado.
   * Diseño: apagar()
   * Parámetros: ninguno.
   * ------------------- */
  void apagar () {
	  digitalWrite(numeroLED, LOW);
	  encendido = false;
  }

  // .........................................................
  // .........................................................
  /** -------------------
   * Descripción: Invierte el estado actual del LED.
   * Diseño: alternar()
   * Parámetros: ninguno.
   * ------------------- */
  void alternar () {
	if (encendido) {
	  apagar();
	} else {
	  encender ();
	}
  } // ()

  // .........................................................
  // .........................................................
  /** -------------------
   * Descripción: Enciende el LED durante el intervalo y después lo apaga.
   * Diseño: tiempo:Z --> brillar()
   * Parámetros: tiempo:Z.
   * ------------------- */
  void brillar (long tiempo) {
	encender ();
	esperar(tiempo); 
	apagar ();
  }
}; // class

// ----------------------------------------------------------
// ----------------------------------------------------------
// ----------------------------------------------------------
// ----------------------------------------------------------
#endif
