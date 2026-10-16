<?php
/** Ejemplo docente de sesiones web; el Sprint 0 activo utiliza src/gui/ y src/business_logic/. */

// -------------------------------------------------
//
// usuario:Texto -> diHola() -> (nombre:Texto, saludo:Texto) 
//
// -------------------------------------------------

function diHola( $usuario ) {
  $objetoResultado = new stdClass;
  $objetoResultado->nombre = $usuario;
  $objetoResultado->saludo = "That's all folks";

  return $objetoResultado;
}

?>
