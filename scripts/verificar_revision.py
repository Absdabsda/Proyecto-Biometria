"""Nombre: verificar_revision.py
Descripción: Herramienta de construcción o auditoría reproducible del Sprint 0.
Copyright: 2026 Julia Valén de Oliveira, solo aportaciones propias; se conservan derechos y atribuciones anteriores.
Fecha: 2026-10-16 (revisión; fechas originales conservadas si constan).
Autor: Julia Valén de Oliveira (aportaciones); base docente según atribuciones conservadas.
Aportación: Automatización de la entrega y comprobaciones de la rúbrica oficial.
"""
"""Audita requisitos estáticos del revisor; no sustituye pruebas ni revisión humana."""
from pathlib import Path
import re
import subprocess

ROOT = Path(__file__).resolve().parents[1]


def exigir(condicion, mensaje):
    """-------------------
    Descripción: detiene la auditoría al detectar una condición incumplida.
    Diseño: condicion:B, mensaje:Text --> exigir()
    Parámetros: resultado de una comprobación y diagnóstico concreto.
    -------------------"""
    if not condicion:
        raise AssertionError(mensaje)


def sin_comentarios(texto):
    """-------------------
    Descripción: oculta comentarios y literales conservando posiciones y líneas.
    Diseño: texto:Text --> sin_comentarios() --> Text
    Parámetros: fuente PHP, Java, JavaScript o C++ sin literales de plantilla complejos.
    -------------------"""
    patron = r'/\*[\s\S]*?\*/|//[^\n]*|"(?:\\.|[^"\\])*"|\'(?:\\.|[^\'\\])*\''
    return re.sub(patron, lambda m: re.sub(r'[^\n]', ' ', m[0]), texto)


def cabeceras(fichero):
    """-------------------
    Descripción: revisa cabeceras de declaraciones nominales de la fuente activa.
    Diseño: fichero:Text --> cabeceras() --> cantidad:N
    Parámetros: ruta del fichero; análisis léxico limitado, sin inferir callbacks anónimos.
    -------------------"""
    texto = fichero.read_text(encoding='utf-8')
    limpio = sin_comentarios(texto)
    limpio = re.sub(r'@\w+(?:\([^)]*\))?', lambda m: ' ' * len(m[0]), limpio)
    limpio = re.sub(r'\b(?:public|protected|private)\s*:|\btemplate\s*<[^>]*>',
                    lambda m: re.sub(r'[^\n]', ' ', m[0]), limpio)
    if fichero.suffix == '.php':
        patrones = [r'(?m)^[ \t]*(?:(?:public|private|protected|static|final)\s+)*function\s+(\w+)\s*\(']
    elif fichero.suffix in {'.mjs', '.js'}:
        patrones = [r'(?m)^[ \t]*(?:export\s+)?(?:async\s+)?function\s+(\w+)\s*\(']
    elif fichero.suffix == '.java':
        patrones = [r'(?m)^[ \t]*(?:@\w+(?:\([^)]*\))?\s*)*(?:(?:public|private|protected|static|final|synchronized)\s+)+(?:[\w.<>,?\[\] ]+\s+)?(\w+)\s*\([^;{}]*\)\s*(?:throws[\w., ]+)?\s*\{',
                    r'(?m)^[ \t]*(LogicaFake|Intento)\s*\([^;{}]*\)\s*\{']
    else:
        patrones = [r'(?m)^[ \t]*(?:inline\s+)?(?:[A-Za-z_][\w:<>]*(?:\s*[*&])?\s+)+(\w+)\s*\([^;{}]*\)\s*(?:const\s*)?\{',
                    r'(?m)^[ \t]*(Publicador|EmisoraBLE|Medidor|PuertoSerie|LED|ServicioEnEmisora|Caracteristica)\s*\([^;{}]*\)\s*(?::[^{}]*)?\{',
                    r'(?m)^[ \t]*operator\s+(BLEService)&\s*\(\)\s*\{']
    encontrados = {}
    for patron in patrones:
        for m in re.finditer(patron, limpio):
            if m[1] not in {'if', 'while', 'for', 'switch', 'catch'}:
                encontrados[m.start()] = m
    for posicion, m in encontrados.items():
        bloques = list(re.finditer(r'/\*\*[\s\S]*?\*/', texto[:posicion]))
        exigir(bool(bloques), f'{fichero.relative_to(ROOT)}: falta cabecera en {m[1]}')
        bloque = bloques[-1]
        intervalo = sin_comentarios(texto[bloque.end():posicion])
        intervalo = re.sub(r'@\w+(?:\([^)]*\))?|template\s*<[^>]*>', '', intervalo).strip()
        exigir(not intervalo and bloque[0].count('-------------------') >= 2,
               f'{fichero.relative_to(ROOT)}: cabecera no delimitada o no inmediata en {m[1]}')
        exigir('Descripción:' in bloque[0] and 'Parámetros:' in bloque[0],
               f'{fichero.relative_to(ROOT)}: descripción o parámetros ausentes en {m[1]}')
        exigir('-->' in bloque[0] or re.search(r'\w+\(\)', bloque[0]),
               f'{fichero.relative_to(ROOT)}: diseño lógico ausente en {m[1]}')
    return len(encontrados)


def verificar_cabecera_archivo(archivo):
    """-------------------
    Descripción: exige los seis metadatos de fichero de la rúbrica oficial.
    Diseño: archivo:Text --> verificar_cabecera_archivo()
    Parámetros: ruta de una fuente propia/adaptada; no interpreta licencias de terceros.
    -------------------"""
    encabezado = archivo.read_text(encoding='utf-8-sig')[:1600]
    for campo in ['Nombre', 'Descripción', 'Copyright', 'Fecha', 'Autor', 'Aportación']:
        exigir(re.search(re.escape(campo) + r':\s*[^\s\n]+', encabezado),
               f'{archivo.name}: falta {campo} en la cabecera.')
    nombre = re.search(r'Nombre:\s*([^\n\r]+)', encabezado)[1].strip()
    exigir(nombre == archivo.name, f'{archivo.name}: Nombre no coincide con el fichero.')
    exigir(re.search(r'Fecha:\s*\d{4}-\d{2}-\d{2}', encabezado),
           f'{archivo.name}: fecha de revisión no reconocible.')


def verificar_documentos_rubrica(raiz):
    """-------------------
    Descripción: exige criterios por componente y trazabilidad de esta revisión.
    Diseño: raiz:Text --> verificar_documentos_rubrica()
    Parámetros: raíz del repositorio o de un fixture aislado; no utiliza red.
    -------------------"""
    documentos = ['acceptance_test.md','architecture.md','ai_traceability.md','rubrica_correcta_revision.md']
    for nombre in documentos:
        exigir((raiz / 'doc' / nombre).is_file(), 'Falta documento de rúbrica: '+nombre)
    aceptacion = (raiz / 'doc/acceptance_test.md').read_text(encoding='utf-8')
    for componente in ['firmware','android','business_logic','database','communication','frontend_business_logic','gui']:
        exigir(re.search(r'\|\s*'+componente+r'\s*\|', aceptacion),
               'Falta criterio de aceptación: '+componente)
    prompts = ['01_database','02_business_logic','03_communication','04_android',
               '05_frontend','06_gui','07_firmware','08_auditoria']
    for nombre in prompts:
        exigir((raiz / 'doc/prompts' / (nombre+'.md')).is_file(), 'Falta prompt: '+nombre)
    traza = (raiz / 'doc/ai_traceability.md').read_text(encoding='utf-8')
    exigir('después de la' in traza and 'implementación inicial' in traza,
           'La trazabilidad debe distinguir los prompts actuales de la implementación inicial.')


def main():
    """-------------------
    Descripción: contrasta estructura, contratos, capas, artefactos y documentación.
    Diseño: main()
    Parámetros: ninguno; lee el repositorio sin escribir ni consultar el alojamiento.
    -------------------"""
    exigir('Julia Valén de Oliveira' in (ROOT / 'author.md').read_text(encoding='utf-8'), 'Autoría incorrecta.')
    verificar_documentos_rubrica(ROOT)
    componentes = ['communication', 'business_logic', 'database', 'frontend_business_logic', 'gui', 'android', 'firmware']
    for componente in componentes:
        exigir((ROOT / 'src' / componente).is_dir(), f'Falta src/{componente}.')
        diseno = ROOT / 'doc' / (componente + '_design.md')
        exigir(diseno.is_file(), f'Falta el diseño de {componente}.')
        contenido = diseno.read_text(encoding='utf-8')
        for titulo in ['## Component Design', '## Design Clarifications', '## General Rules',
                       'Programming Language:', 'Function/Method Headers:', 'Code Readability:', 'Automated Testing:']:
            exigir(titulo in contenido, f'{diseno.name}: falta {titulo}')
    for componente, carpeta in [('business_logic', ROOT / 'src/business_logic'),
                                ('android', ROOT / 'src/android/Prueba2025/app/src/main/java'),
                                ('firmware', ROOT / 'src/firmware')]:
        contenido = (ROOT / 'doc' / (componente + '_design.md')).read_text(encoding='utf-8')
        for fichero in carpeta.rglob('*'):
            if fichero.suffix not in {'.php', '.java', '.h'}:
                continue
            for clase in re.findall(r'\b(?:class|interface)\s+(\w+)', sin_comentarios(fichero.read_text(encoding='utf-8'))):
                exigir(re.search(r'-{3,}\s+' + re.escape(clase) + r'\b', contenido), f'Falta diagrama formal de {clase}.')
    esquema = (ROOT / 'src/database/crear.sql').read_text(encoding='utf-8')
    columnas = re.findall(r'^\s+(\w+)\s+(?:BIGINT|TINYINT|SMALLINT|TIMESTAMP)\b', esquema, re.M)
    exigir(columnas == ['medidaID', 'tipo', 'valor', 'fecha'], 'El esquema no tiene exactamente los cuatro campos MVP.')
    exigir(esquema == (ROOT / 'bd/crear.sql').read_text(encoding='utf-8'), 'Las dos copias SQL difieren.')
    negocio = (ROOT / 'src/business_logic/Logica.php').read_text(encoding='utf-8')
    exigir(not re.search(r'\$_(?:GET|POST|SERVER)|http_response_code|header\(|curl_|src/communication|HttpRequest|HttpResponse', negocio), 'Negocio depende del transporte.')
    for campo in columnas:
        exigir(campo in negocio, f'Negocio no referencia {campo}.')
    interfaz = 'limit: N --> listarMedidas() --> medidas: [Medida]'
    for componente in ['business_logic', 'frontend_business_logic']:
        exigir(interfaz in (ROOT / 'doc' / (componente + '_design.md')).read_text(encoding='utf-8'), 'Las firmas lógicas del proxy y negocio no coinciden.')
        exigir('entrada: MedidaEntrada --> guardarMedida() --> medida: Medida' in
               (ROOT / 'doc' / (componente + '_design.md')).read_text(encoding='utf-8'),
               'Las firmas lógicas de guardarMedida en Android y negocio no coinciden.')
    gui = sin_comentarios((ROOT / 'src/gui/medidas.mjs').read_text(encoding='utf-8'))
    exigir(not re.search(r'\b(?:fetch|XMLHttpRequest|WebSocket)\s*\(', gui), 'La GUI contiene comunicación directa.')
    exigir("from '../frontend_business_logic/medidas.mjs'" in (ROOT / 'src/gui/medidas.mjs').read_text(encoding='utf-8'), 'La GUI no importa el proxy.')
    android = ROOT / 'src/android/Prueba2025/app/src/main/java/org/jordi/prueba2025'
    exigir('guardarMedida(' in (android / 'MainActivity.java').read_text(encoding='utf-8'), 'Android no usa el proxy.')
    exigir('HttpURLConnection' not in (android / 'MainActivity.java').read_text(encoding='utf-8'), 'La GUI Android conoce el transporte.')
    exigir((ROOT / 'src/gui/Aplicacion.html').read_bytes() == (ROOT / 'servidor/public/ux/Aplicacion.html').read_bytes(), 'La pantalla generada no coincide con su fuente.')
    exigir((ROOT / 'src/frontend_business_logic/medidas.mjs').read_bytes() == (ROOT / 'servidor/public/ux/logicaFake/medidas.mjs').read_bytes(), 'El proxy generado no coincide con su fuente.')
    esperado = (ROOT / 'src/gui/medidas.mjs').read_text(encoding='utf-8').replace('../frontend_business_logic/medidas.mjs','./logicaFake/medidas.mjs').replace('Nombre: medidas.mjs','Nombre: medidas.js')
    exigir(esperado == (ROOT / 'servidor/public/ux/medidas.js').read_text(encoding='utf-8'), 'La GUI generada no coincide con su fuente.')
    fuentes = [p for p in (ROOT / 'src').rglob('*') if p.suffix in {'.php','.mjs','.java','.h','.ino'} and not any(x in p.parts for x in ['build','.gradle','.idea'])]
    fuentes += [ROOT / 'servidor/public/api/index.php']
    total = sum(cabeceras(p) for p in fuentes)
    seguidos = subprocess.run(['git','ls-files'], cwd=ROOT, capture_output=True, text=True, check=True).stdout.splitlines()
    secretos = [p for p in seguidos if Path(p).name in {'config.php','local.properties','.env'} or Path(p).suffix in {'.apk','.jks','.keystore'}]
    exigir(not secretos, 'Hay configuración privada o binarios en los archivos versionados: '+str(secretos))
    extensiones = {'.php','.java','.mjs','.js','.h','.ino','.cpp','.py','.sql','.html','.css','.gradle','.xml','.properties','.yml','.pro'}
    archivos = [ROOT / p for p in seguidos if Path(p).suffix in extensiones and '/gradle/wrapper/' not in p]
    for archivo in archivos:
        verificar_cabecera_archivo(archivo)
    exigir((ROOT / 'outputs/2026-10-16/Julia_Valen_de_Oliveira_review.md').is_file(), 'Falta el informe por autora y fecha.')
    print(f'Revision estatica correcta: {len(componentes)} componentes; {total} declaraciones con cabecera; modelo MVP y capas coherentes.')
    print(f'Rubrica oficial: {len(archivos)} cabeceras de fichero, 7 criterios de componente y 8 prompts comprobados.')
    print('Limites: no interpreta toda la semantica ni acredita frecuencia historica, hardware o calificacion.')


if __name__ == '__main__':
    main()
