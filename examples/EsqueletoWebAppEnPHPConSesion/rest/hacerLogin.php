<?php
/**
 * Nombre: hacerLogin.php
 * Descripción: Ejemplo docente de referencia; no pertenece al producto activo del Sprint 0.
 * Copyright: 2026 Julia Valén de Oliveira, solo aportaciones propias; se conservan derechos y atribuciones anteriores.
 * Fecha: 2026-10-16 (revisión; fechas originales conservadas si constan).
 * Autor: Julia Valén de Oliveira (catalogación); autoría original según atribuciones del archivo.
 * Aportación: Catalogación y documentación de referencia; se conservan la autoría y el código docente.
 */

/** Ejemplo docente de sesiones web; el Sprint 0 activo utiliza src/gui/ y src/business_logic/. */


require_once('../logica/hacerLogin.php');

// ----------------------------------------------------------------
//
// GET ../rest/hacerLogin.php?nombre=<Texto>&password=<Texto>
//
// @return
//  VoF: true si el login OK
// 
// @return
//  usuario:Texto
//       devuelto implicitamente en la variable global de sesión
//       (en el navegador no se pordrá acceder a la var. global)
//
// ----------------------------------------------------------------

$objetoResultado = new stdClass;

// creo una sesión 
session_start();

// obtengo valores de los parámetros
$nombre = $_GET["nombre"];
$password = $_GET["password"];

//
// llamada a la verdadera función.
//
if ( hacerLogin( $nombre, $password) == true  ) {

  $objetoResultado->resultado = true;
  $objetoResultado->usuario = $nombre;

  // guardo en la sesión el nombre del usuario
  session_start();
  $_SESSION["usuario"] = $_GET["nombre"];

} else {
  session_destroy();
  $objetoResultado->resultado = false;
}

// echo == devolver
echo json_encode( $objetoResultado );
?>
