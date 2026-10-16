"""Nombre: revision_test.py
Descripción: Pruebas automáticas del contrato revision_test del Sprint 0.
Copyright: 2026 Julia Valén de Oliveira, solo aportaciones propias; se conservan derechos y atribuciones anteriores.
Fecha: 2026-10-16 (revisión; fechas originales conservadas si constan).
Autor: Julia Valén de Oliveira (aportaciones); base docente según atribuciones conservadas.
Aportación: Verificación de casos válidos, límites, errores y aislamiento de pruebas.
"""
"""Pruebas de la auditoría oficial con fixtures temporales y sin acceso de red."""
import importlib.util
from pathlib import Path
import tempfile
import unittest

ROOT = Path(__file__).resolve().parents[1]
spec = importlib.util.spec_from_file_location('revision', ROOT / 'scripts/verificar_revision.py')
revision = importlib.util.module_from_spec(spec)
spec.loader.exec_module(revision)


class RevisionTest(unittest.TestCase):
    """Comprueba aceptación y rechazo de evidencias documentales de forma aislada."""

    def test_acepta_cabecera_completa_y_rechaza_cada_campo_ausente(self):
        """-------------------
        Descripción: cada metadato obligatorio tiene efecto sobre la aceptación.
        Diseño: test_acepta_cabecera_completa_y_rechaza_cada_campo_ausente()
        Parámetros: ninguno; la fuente temporal se elimina al finalizar.
        -------------------"""
        campos = ['Nombre: ejemplo.py','Descripción: prueba','Copyright: aportaciones de prueba',
                  'Fecha: 2026-10-16','Autor: autora de prueba','Aportación: documentación']
        with tempfile.TemporaryDirectory() as temporal:
            archivo = Path(temporal) / 'ejemplo.py'
            archivo.write_text('\n'.join(campos), encoding='utf-8')
            revision.verificar_cabecera_archivo(archivo)
            for omitido in campos:
                with self.subTest(campo=omitido.split(':')[0]):
                    archivo.write_text('\n'.join(c for c in campos if c != omitido), encoding='utf-8')
                    with self.assertRaises(AssertionError):
                        revision.verificar_cabecera_archivo(archivo)

    def test_rechaza_nombre_que_no_corresponde_al_archivo(self):
        """-------------------
        Descripción: detecta una cabecera copiada de otro archivo sin actualizar.
        Diseño: test_rechaza_nombre_que_no_corresponde_al_archivo()
        Parámetros: ninguno; datos nuevos en cada ejecución.
        -------------------"""
        with tempfile.TemporaryDirectory() as temporal:
            archivo = Path(temporal) / 'real.py'
            archivo.write_text('Nombre: otro.py\nDescripción: prueba\nCopyright: prueba\nFecha: 2026-10-16\nAutor: prueba\nAportación: prueba', encoding='utf-8')
            with self.assertRaisesRegex(AssertionError, 'Nombre no coincide'):
                revision.verificar_cabecera_archivo(archivo)

    def test_documentacion_real_valida_y_falta_de_prompt_detectada(self):
        """-------------------
        Descripción: valida la documentación real y detecta un prompt faltante.
        Diseño: test_documentacion_real_valida_y_falta_de_prompt_detectada()
        Parámetros: ninguno; copia únicamente documentación a un fixture temporal.
        -------------------"""
        revision.verificar_documentos_rubrica(ROOT)
        with tempfile.TemporaryDirectory() as temporal:
            raiz = Path(temporal)
            (raiz / 'doc').mkdir()
            for nombre in ['acceptance_test.md','architecture.md','ai_traceability.md','rubrica_correcta_revision.md']:
                (raiz / 'doc' / nombre).write_bytes((ROOT / 'doc' / nombre).read_bytes())
            with self.assertRaisesRegex(AssertionError, 'Falta prompt'):
                revision.verificar_documentos_rubrica(raiz)


if __name__ == '__main__':
    unittest.main()
