import re
from datetime import datetime

def validate_nombre(nombre):
    if not nombre:
        return False
    nombre = nombre.strip()
    return 3 <= len(nombre) <= 200

def validate_email(email):
    if not email:
        return False
    if len(email) > 100:
        return False
    pattern = r'^[\w.]+@[a-zA-Z_]+?\.[a-zA-Z]{2,3}$'
    return re.match(pattern, email) is not None

def validate_phone(phone):
    if not phone:
        return False
    pattern = r'^\+569[0-9]{8}$'
    return re.match(pattern, phone) is not None

def validate_region(region):
    return bool(region and region.strip())

def validate_comuna(comuna):
    return bool(comuna and comuna.strip())

def validate_sector(sector):
    if not sector:
        return True  # Es opcional
    return len(sector.strip()) <= 100

def validate_tipo(tipo):
    return tipo in ['perro', 'gato']

def validate_cantidad(cantidad):
    try:
        num = int(cantidad)
        return num >= 1
    except (ValueError, TypeError):
        return False

def validate_edad(edad):
    try:
        num = int(edad)
        return num >= 1
    except (ValueError, TypeError):
        return False

def validate_medida_edad(medida):
    return medida in ['m', 'a']

def validate_fecha_entrega(fecha_str):
    if not fecha_str:
        return False
    try:
        fecha = datetime.strptime(fecha_str, "%Y-%m-%dT%H:%M")
        ahora = datetime.now()
        # Debe ser al menos 3 horas en el futuro
        return (fecha - ahora).total_seconds() >= 3 * 3600
    except ValueError:
        return False

def validate_descripcion(descripcion):
    if not descripcion:
        return True  # Es opcional
    return len(descripcion) <= 1000

def validate_contactar_por(form):
    """Valida que al menos 1 y máximo 5 métodos de contacto estén seleccionados"""
    tipos = ['whatsapp', 'telegram', 'x', 'instagram', 'tiktok', 'otra']
    count = 0
    contactos = []
    
    for tipo in tipos:
        if tipo in form:  # Si el checkbox fue enviado
            valor_id = form.get(f'{tipo}-id', '').strip()
            if valor_id and 4 <= len(valor_id) <= 50:
                count += 1
                contactos.append((tipo, valor_id))
    
    if 1 <= count <= 5:
        return True, contactos
    return False, []

def validate_fotos(files):
    """Valida que haya entre 1 y 5 fotos válidas"""
    errores = {}
    fotos_validas = []
    
    foto1 = files.get('input-foto')
    if not foto1 or not foto1.filename:
        errores['input-foto'] = 'Debe subir al menos una foto'
        return errores
    
    # Validar foto principal
    if not validar_extension_foto(foto1.filename):
        errores['input-foto'] = 'Formato de imagen inválido. Use JPG, JPEG o PNG'
        return errores
    
    fotos_validas.append(foto1)
    
    # Validar fotos adicionales (máximo 5 en total)
    for i in range(2, 6):
        foto = files.get(f'foto{i}')
        if foto and foto.filename:
            if not validar_extension_foto(foto.filename):
                errores[f'foto{i}'] = f'Formato de imagen inválido en foto {i}'
            else:
                fotos_validas.append(foto)
    
    if len(fotos_validas) > 5:
        errores['fotos'] = 'Máximo 5 fotos permitidas'
    
    return errores

def validar_extension_foto(filename):
    """Valida que la extensión sea jpg, jpeg o png"""
    extensiones_permitidas = {'jpg', 'jpeg', 'png', 'gif'}
    return '.' in filename and filename.rsplit('.', 1)[1].lower() in extensiones_permitidas

def validate_nombre_comentario(nombre):
    if not nombre:
        return False
    nombre = nombre.strip()
    if 3 <= len(nombre) <= 80:
        return True
    else:
        return False
    
def valdiate_texto_comentario(texto):
    if not texto:
        return False
    texto = texto.strip()
    if len(texto) >=5:
        return True
    else:
        return False