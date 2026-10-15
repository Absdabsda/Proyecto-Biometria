// -*- mode: c++ -*-

// ----------------------------------------------------------
// Jordi Bataller i Mascarell
// 2019-07-17
// ----------------------------------------------------------
#ifndef SERVICIO_EMISORA_H_INCLUIDO
#define SERVICIO_EMISORA_H_INCLUIDO

// ----------------------------------------------------
// ----------------------------------------------------
#include <vector>

// ----------------------------------------------------
// alReves() utilidad
// pone al revés el contenido de una array en el mismo array
// ----------------------------------------------------
template< typename T >
T *  alReves( T * p, int n ) {
  T aux;

  for( int i=0; i < n/2; i++ ) {
	aux = p[i];
	p[i] = p[n-i-1];
	p[n-i-1] = aux;
  }
  return p;
} // ()

// ----------------------------------------------------
// ----------------------------------------------------
/** -------------------
 * Descripción: Copia el texto en orden inverso hasta el máximo de bytes permitido.
 * Diseño: pString:Text, pUint:[N], tamMax:Z --> stringAUint8AlReves() --> [N]
 * Parámetros: pString:Text, pUint:[N], tamMax:Z.
 * ------------------- */
uint8_t * stringAUint8AlReves( const char * pString, uint8_t * pUint, int tamMax ) {

	int longitudString =  strlen( pString );
	int longitudCopiar = ( longitudString > tamMax ? tamMax : longitudString );
	// copio nombreServicio -> uuidServicio pero al revés
	for( int i=0; i<=longitudCopiar-1; i++ ) {
	  pUint[ tamMax-i-1 ] = pString[ i ];
	} // for

	return pUint;
} // ()

// ----------------------------------------------------------
// ----------------------------------------------------------
class ServicioEnEmisora {

public:


  // --------------------------------------------------------
  // --------------------------------------------------------

  // .........................................................
  // .........................................................
  using CallbackCaracteristicaEscrita = void ( uint16_t conn_handle,
											   BLECharacteristic * chr,
											   uint8_t * data, uint16_t len); 
  // .........................................................
  // .........................................................
  class Caracteristica {
  private:
	uint8_t uuidCaracteristica[16] = { // el uuid se copia aquí (al revés) a partir de un string-c
	  // least signficant byte, el primero
	  '0', '1', '2', '3', 
	  '4', '5', '6', '7', 
	  '8', '9', 'A', 'B', 
	  'C', 'D', 'E', 'F'
	};

	// 
	// 
	// 
	BLECharacteristic laCaracteristica;

  public:

	// .........................................................
	// .........................................................
	/** -------------------
	 * Descripción: Construye y configura una característica del servicio BLE.
	 * Diseño: nombreCaracteristica_:Text --> Caracteristica() --> Caracteristica
	 * Parámetros: nombreCaracteristica_:Text.
	 * ------------------- */
	Caracteristica( const char * nombreCaracteristica_ )
	  :
	  laCaracteristica( stringAUint8AlReves( nombreCaracteristica_, &uuidCaracteristica[0], 16 ) )
	{
	  
	} // ()

	// .........................................................
	// .........................................................
	/** -------------------
	 * Descripción: Construye y configura una característica del servicio BLE.
	 * Diseño: nombreCaracteristica_:Text, props:N, permisoRead:SecureMode_t, permisoWrite:SecureMode_t, tam:N --> Caracteristica() --> Caracteristica
	 * Parámetros: nombreCaracteristica_:Text, props:N, permisoRead:SecureMode_t, permisoWrite:SecureMode_t, tam:N.
	 * ------------------- */
	Caracteristica( const char * nombreCaracteristica_ ,
					uint8_t props,
					SecureMode_t permisoRead,
					SecureMode_t permisoWrite, 
					uint8_t tam ) 
	  :
	  Caracteristica( nombreCaracteristica_ ) // llamada al otro constructor
	{
	  (*this).asignarPropiedadesPermisosYTamanyoDatos( props, permisoRead, permisoWrite, tam );
	} // ()

  private:
	// .........................................................
	// CHR_PROPS_WRITE , CHR_PROPS_READ ,  CHR_PROPS_NOTIFY 
	// .........................................................
	/** -------------------
	 * Descripción: Configura las propiedades de lectura, escritura y notificación.
	 * Diseño: props:N --> asignarPropiedades()
	 * Parámetros: props:N.
	 * ------------------- */
	void asignarPropiedades ( uint8_t props ) {
	  // no puedo escribir AUN si el constructor llama a esto: Serial.println( " laCaracteristica.setProperties( props ); ");
	  (*this).laCaracteristica.setProperties( props );
	} // ()

	// .........................................................
	// SECMODE_OPEN, SECMODE_NO_ACCESS (Bluefruit nRF52 1.7.0)
	// .........................................................
	/** -------------------
	 * Descripción: Configura los permisos de lectura y escritura.
	 * Diseño: permisoRead:SecureMode_t, permisoWrite:SecureMode_t --> asignarPermisos()
	 * Parámetros: permisoRead:SecureMode_t, permisoWrite:SecureMode_t.
	 * ------------------- */
	void asignarPermisos( SecureMode_t permisoRead, SecureMode_t permisoWrite ) {
	  // no puedo escribir AUN si el constructor llama a esto: Serial.println( "laCaracteristica.setPermission( permisoRead, permisoWrite ); " );
	  (*this).laCaracteristica.setPermission( permisoRead, permisoWrite );
	} // ()

	// .........................................................
	// .........................................................
	/** -------------------
	 * Descripción: Configura la longitud de los datos de la característica.
	 * Diseño: tam:N --> asignarTamanyoDatos()
	 * Parámetros: tam:N.
	 * ------------------- */
	void asignarTamanyoDatos( uint8_t tam ) {
	  // no puedo escribir AUN si el constructor llama a esto: Serial.print( " (*this).laCaracteristica.setFixedLen( tam = " );
	  // no puedo escribir AUN si el constructor llama a esto: Serial.println( tam );
	  // (*this).laCaracteristica.setFixedLen( tam );
	  (*this).laCaracteristica.setMaxLen( tam );
	} // ()

  public:
	// .........................................................
	// .........................................................
	/** -------------------
	 * Descripción: Aplica conjuntamente propiedades, permisos y longitud.
	 * Diseño: props:N, permisoRead:SecureMode_t, permisoWrite:SecureMode_t, tam:N --> asignarPropiedadesPermisosYTamanyoDatos()
	 * Parámetros: props:N, permisoRead:SecureMode_t, permisoWrite:SecureMode_t, tam:N.
	 * ------------------- */
	void asignarPropiedadesPermisosYTamanyoDatos( uint8_t props,
												 SecureMode_t permisoRead,
												 SecureMode_t permisoWrite, 
												 uint8_t tam ) {
	  asignarPropiedades( props );
	  asignarPermisos( permisoRead, permisoWrite );
	  asignarTamanyoDatos( tam );
	} // ()
												 

	// .........................................................
	// .........................................................
	/** -------------------
	 * Descripción: Escribe los datos en la característica y devuelve la cantidad aceptada.
	 * Diseño: str:Text --> escribirDatos() --> N
	 * Parámetros: str:Text.
	 * ------------------- */
	uint16_t escribirDatos( const char * str ) {
	  // Serial.print( " return (*this).laCaracteristica.write( str  = " );
	  // Serial.println( str );

	  uint16_t r = (*this).laCaracteristica.write( str );

	  // Serial.print( ">>>Escritos " ); Serial.print( r ); Serial.println( " bytes con write() " );

	  return r;
	} // ()

	// .........................................................
	// .........................................................
	/** -------------------
	 * Descripción: Notifica los datos a los clientes suscritos y devuelve el resultado.
	 * Diseño: str:Text --> notificarDatos() --> N
	 * Parámetros: str:Text.
	 * ------------------- */
	uint16_t notificarDatos( const char * str ) {
	  
	  uint16_t r = laCaracteristica.notify( &str[0] );

	  return r;
	} //  ()

	// .........................................................
	// .........................................................
	/** -------------------
	 * Descripción: Registra el manejador de escritura de la característica.
	 * Diseño: cb:CallbackCaracteristicaEscrita --> instalarCallbackCaracteristicaEscrita()
	 * Parámetros: cb:CallbackCaracteristicaEscrita.
	 * ------------------- */
	void instalarCallbackCaracteristicaEscrita( CallbackCaracteristicaEscrita cb ) {
	  (*this).laCaracteristica.setWriteCallback( cb );
	} // ()

	// .........................................................
	// .........................................................
	/** -------------------
	 * Descripción: Activa la característica configurada.
	 * Diseño: activar()
	 * Parámetros: ninguno.
	 * ------------------- */
	void activar() {
	  err_t error = (*this).laCaracteristica.begin();
	  Globales::elPuerto.escribir(  " (*this).laCaracteristica.begin(); error = " );
	  Globales::elPuerto.escribir(  error );
	} // ()

  }; // class Caracteristica
  
  // --------------------------------------------------------
  // --------------------------------------------------------
private:
  
  uint8_t uuidServicio[16] = { // el uuid se copia aquí (al revés) a partir de un string-c
	// least signficant byte, el primero
	'0', '1', '2', '3', 
	'4', '5', '6', '7', 
	'8', '9', 'A', 'B', 
	'C', 'D', 'E', 'F'
  };

  //
  //
  //
  BLEService elServicio;

  //
  //
  //
  std::vector< Caracteristica * > lasCaracteristicas;

public:
  
  // .........................................................
  // .........................................................
  /** -------------------
   * Descripción: Construye el servicio a partir de su identificador textual.
   * Diseño: nombreServicio_:Text --> ServicioEnEmisora() --> ServicioEnEmisora
   * Parámetros: nombreServicio_:Text.
   * ------------------- */
  ServicioEnEmisora( const char * nombreServicio_ )
	:
	elServicio( stringAUint8AlReves( nombreServicio_, &uuidServicio[0], 16 ) )
  {
	
  } // ()
  
  // .........................................................
  // .........................................................
  /** -------------------
   * Descripción: Muestra el UUID del servicio en el puerto serie para diagnóstico.
   * Diseño: escribeUUID()
   * Parámetros: ninguno.
   * ------------------- */
  void escribeUUID() {
	Serial.println ( "**********" );
	for (int i=0; i<= 15; i++) {
	  Serial.print( (char) uuidServicio[i] );
	}
	Serial.println ( "\n**********" );
  } // ()

  // .........................................................
  // .........................................................
  /** -------------------
   * Descripción: Añade una característica a la colección del servicio.
   * Diseño: car:Caracteristica --> anyadirCaracteristica()
   * Parámetros: car:Caracteristica.
   * ------------------- */
  void anyadirCaracteristica( Caracteristica & car ) {
	(*this).lasCaracteristicas.push_back( & car );
  } // ()

  // .........................................................
  // .........................................................
  /** -------------------
   * Descripción: Activa el servicio y cada característica registrada.
   * Diseño: activarServicio()
   * Parámetros: ninguno.
   * ------------------- */
  void activarServicio( ) {
	// entiendo que al llegar aquí ya ha sido configurado
	// todo: características y servicio

	err_t error = (*this).elServicio.begin();
	Serial.print( " (*this).elServicio.begin(); error = " );
	Serial.println( error );

	for( auto pCar : (*this).lasCaracteristicas ) {
	  (*pCar).activar();
	} // for

  } // ()

  // .........................................................
  // .........................................................
  operator BLEService&() {
	// "conversión de tipo": si pongo esta clase en un sitio donde necesitan un BLEService
	return elServicio;
  } // ()
	
}; // class

#endif

// ----------------------------------------------------------
// ----------------------------------------------------------
// ----------------------------------------------------------
// ----------------------------------------------------------

