import xml.etree.ElementTree as ET
from pathlib import Path

ET.register_namespace('', "http://www.rki.de/PlzToolTransmittingSiteSearchText.xsd")

base_dir = Path(__file__).parent
input_file = base_dir / 'src' / 'main' / 'resources' / 'TransmittingSiteSearchText.xml'
output_file = base_dir / 'src' / 'main' / 'resources' / 'TransmittingSiteSearchText_out.xml'

# Schritt 1: Header und XML-Teil trennen
with open(input_file, encoding='utf-8') as f:
    content = f.read()

split_token = '<TransmittingSites'
header, xml_start = content.split(split_token, 1)
xml_content = f'<TransmittingSites{xml_start}'

# Schritt 2: XML parsen und bearbeiten
tree = ET.ElementTree(ET.fromstring(xml_content))
root = tree.getroot()

ns = 'http://www.rki.de/PlzToolTransmittingSiteSearchText.xsd'
nsmap = {'ns': ns}

for ts in root.findall('.//ns:TransmittingSite', nsmap):
    for search_text in list(ts.findall('ns:SearchText', nsmap)):
        ts.remove(search_text)

neuer_transmittingsite = {
    "Name": "Robert Koch-Institut",
    "Code": "1.",
    "Department": "",
    "Street": "Nordufer 20",
    "Postalcode": "13353",
    "Place": "Berlin",
    "Phone": "",
    "Fax": "",
    "Email": "demis-support@rki.de ",
    "Covid19Hotline": "",
    "Covid19EMail": "",
    "Covid19Fax": "",
    "EinreiseAnmeldungHotline": "",
    "EinreiseAnmeldungEMail": "",
    "EinreiseAnmeldungFax": ""
}
new_ts = ET.Element(f'{{{ns}}}TransmittingSite', neuer_transmittingsite)
root.append(new_ts)

# Schritt 3: Geänderte XML in einen String schreiben
xmlstr = ET.tostring(root, encoding='utf-8', xml_declaration=False).decode('utf-8')

# Schritt 4: Header + neuen XML-Inhalt zusammenfügen und speichern
with open(output_file, 'w', encoding='utf-8') as f:
    f.write(header.strip() + '\n')
    f.write(xmlstr)
