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


def main():
    """-------------------
    Descripción: contrasta estructura, contratos, capas, artefactos y documentación.
    Diseño: main()
    Parámetros: ninguno; lee el repositorio sin escribir ni consultar el alojamiento.
    -------------------"""
    exigir('Julia Valén de Oliveira' in (ROOT / 'author.md').read_text(encoding='utf-8'), 'Autoría incorrecta.')
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
                if clase == 'MainActivity':
                    continue  # El documento del revisor excluye la verificación formal de GUI.
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
    gui = sin_comentarios((ROOT / 'src/gui/medidas.mjs').read_text(encoding='utf-8'))
    exigir(not re.search(r'\b(?:fetch|XMLHttpRequest|WebSocket)\s*\(', gui), 'La GUI contiene comunicación directa.')
    exigir("from '../frontend_business_logic/medidas.mjs'" in (ROOT / 'src/gui/medidas.mjs').read_text(encoding='utf-8'), 'La GUI no importa el proxy.')
    android = ROOT / 'src/android/Prueba2025/app/src/main/java/org/jordi/prueba2025'
    exigir('guardarMedida(' in (android / 'MainActivity.java').read_text(encoding='utf-8'), 'Android no usa el proxy.')
    exigir('HttpURLConnection' not in (android / 'MainActivity.java').read_text(encoding='utf-8'), 'La GUI Android conoce el transporte.')
    exigir((ROOT / 'src/gui/Aplicacion.html').read_bytes() == (ROOT / 'servidor/public/ux/Aplicacion.html').read_bytes(), 'La pantalla generada no coincide con su fuente.')
    exigir((ROOT / 'src/frontend_business_logic/medidas.mjs').read_bytes() == (ROOT / 'servidor/public/ux/logicaFake/medidas.mjs').read_bytes(), 'El proxy generado no coincide con su fuente.')
    esperado = (ROOT / 'src/gui/medidas.mjs').read_text(encoding='utf-8').replace('../frontend_business_logic/medidas.mjs','./logicaFake/medidas.mjs')
    exigir(esperado == (ROOT / 'servidor/public/ux/medidas.js').read_text(encoding='utf-8'), 'La GUI generada no coincide con su fuente.')
    fuentes = [p for p in (ROOT / 'src').rglob('*') if p.suffix in {'.php','.mjs','.java','.h','.ino'} and not any(x in p.parts for x in ['build','.gradle','.idea'])]
    fuentes += [ROOT / 'servidor/public/api/index.php']
    total = sum(cabeceras(p) for p in fuentes)
    seguidos = subprocess.run(['git','ls-files'], cwd=ROOT, capture_output=True, text=True, check=True).stdout.splitlines()
    secretos = [p for p in seguidos if Path(p).name in {'config.php','local.properties','.env'} or Path(p).suffix in {'.apk','.jks','.keystore'}]
    exigir(not secretos, 'Hay configuración privada o binarios en los archivos versionados: '+str(secretos))
    exigir((ROOT / 'outputs/2026-10-16/Julia_Valen_de_Oliveira_review.md').is_file(), 'Falta el informe por autora y fecha.')
    print(f'Revision estatica correcta: {len(componentes)} componentes; {total} declaraciones con cabecera; modelo MVP y capas coherentes.')
    print('Limites: no interpreta toda la semantica ni acredita frecuencia historica, hardware o calificacion.')


if __name__ == '__main__':
    main()
