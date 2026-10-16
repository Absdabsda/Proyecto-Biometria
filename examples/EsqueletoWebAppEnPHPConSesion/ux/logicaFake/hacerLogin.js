/**
 * Nombre: hacerLogin.js
 * Descripción: Ejemplo docente de referencia; no pertenece al producto activo del Sprint 0.
 * Copyright: 2026 Julia Valén de Oliveira, solo aportaciones propias; se conservan derechos y atribuciones anteriores.
 * Fecha: 2026-10-16 (revisión; fechas originales conservadas si constan).
 * Autor: Julia Valén de Oliveira (catalogación); autoría original según atribuciones del archivo.
 * Aportación: Catalogación y documentación de referencia; se conservan la autoría y el código docente.
 */
/** Ejemplo docente de sesiones web; el Sprint 0 activo utiliza src/gui/ y src/business_logic/. */
// ---------------------------------------------------
//
// versión fake de una función de la lógica
//
// nombre:Texto, password:Texto -> hacerLogin() -> Boolean
//
// (Boolean devuelto via callback)
//
// ---------------------------------------------------
function hacerLogin( nombre, password, cb ) {

	// preparar la llamada remota
	var xmlhttp = new XMLHttpRequest();
	xmlhttp.onreadystatechange = function() {
		// callback para cuando llegue la respuesta
		// de la petición que haremos más abajo

		if( this.readyState == 4 && this.status == 200 ){
			// este es el texto JSON recibido la llamada a
			// demo_file.php, pasado a objeto JSON 
			console.log( "recibo: " + this.responseText )
			var resultado = JSON.parse(this.responseText);

			cb( resultado ) // devuelvo el resultado
		}
	};
	
	// llamamos *remotamente* al fichero hacerLogin.php
	// (la verdadera función de la lógica)
	xmlhttp.open("GET", "../rest/hacerLogin.php?nombre="+nombre+"&password="+password, true);
	xmlhttp.send();

} // ()
