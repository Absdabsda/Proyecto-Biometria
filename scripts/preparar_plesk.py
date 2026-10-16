"""Nombre: preparar_plesk.py
Descripción: Herramienta de construcción o auditoría reproducible del Sprint 0.
Copyright: 2026 Julia Valén de Oliveira, solo aportaciones propias; se conservan derechos y atribuciones anteriores.
Fecha: 2026-10-16 (revisión; fechas originales conservadas si constan).
Autor: Julia Valén de Oliveira (aportaciones); base docente según atribuciones conservadas.
Aportación: Automatización de la entrega y comprobaciones de la rúbrica oficial.
"""
"""Construye el paquete privado/público desde las fuentes canónicas, sin secretos."""
from pathlib import Path
from zipfile import ZipFile, ZIP_DEFLATED

ROOT = Path(__file__).resolve().parents[1]
SERVER = ROOT / 'servidor'
OUTPUT = ROOT / 'output/plesk-servidor.zip'
OUTPUT.parent.mkdir(exist_ok=True)
client = ROOT / 'src/frontend_business_logic/medidas.mjs'
# Copia generada para que el directorio public sea autocontenido.
(SERVER / 'public/ux/logicaFake/medidas.mjs').write_bytes(client.read_bytes())
gui = ROOT / 'src/gui'
if gui.exists():
    (SERVER / 'public/ux/Aplicacion.html').write_bytes((gui / 'Aplicacion.html').read_bytes())
    (SERVER / 'public/ux/medidas.js').write_text(
        (gui / 'medidas.mjs').read_text(encoding='utf-8').replace('../frontend_business_logic/medidas.mjs', './logicaFake/medidas.mjs').replace('Nombre: medidas.mjs', 'Nombre: medidas.js'),
        encoding='utf-8')
with ZipFile(OUTPUT, 'w', ZIP_DEFLATED) as archive:
    for file in sorted((SERVER / 'public').rglob('*')):
        if file.is_file():
            archive.write(file, 'httpdocs/' + file.relative_to(SERVER / 'public').as_posix())
    for source, target in [
        ('src/business_logic/Logica.php', 'pbio_privado/logica/Logica.php'),
        ('src/communication/ReglasREST.php', 'pbio_privado/servidorREST/ReglasREST.php'),
        ('servidor/config.example.php', 'pbio_privado/config.example.php'),
    ]:
        archive.write(ROOT / source, target)
print(OUTPUT)
