<?php
/** Ejemplo docente de sesiones web; el Sprint 0 activo utiliza src/gui/ y src/business_logic/. */


// ---------------------------------------------------------------
//
// nombre:Texto, password:Texto -> hacerLogin() -> VoF
//
// ---------------------------------------------------------------

function hacerLogin( $nombre, $password ) {

  // comprobación "rigurosa" del password
  if ( $password == "1234" ) {
    return true;
  }

  return false;
}
?>
