<?php
/**
 * Nombre: diHola.php
 * Descripción: Ejemplo docente de referencia; no pertenece al producto activo del Sprint 0.
 * Copyright: 2026 Julia Valén de Oliveira, solo aportaciones propias; se conservan derechos y atribuciones anteriores.
 * Fecha: 2026-10-16 (revisión; fechas originales conservadas si constan).
 * Autor: Julia Valén de Oliveira (catalogación); autoría original según atribuciones del archivo.
 * Aportación: Catalogación y documentación de referencia; se conservan la autoría y el código docente.
 */

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
