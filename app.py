from flask import Flask, request, render_template, redirect, url_for, jsonify
import database.db as db
from werkzeug.utils import secure_filename
import os
from datetime import datetime
from math import ceil
import utils.validations as val

UPLOAD_FOLDER = "static/uploads"

app = Flask(__name__)
app.secret_key = "s3cr3t_k3y_adopcion"
app.config["UPLOAD_FOLDER"] = UPLOAD_FOLDER

# Crear carpeta de uploads si no existe
os.makedirs(UPLOAD_FOLDER, exist_ok=True)

# PORTADA
@app.route("/", methods=["GET"])
def index():
    # Obtener mensaje de éxito si existe
    mensaje = request.args.get('mensaje', '')
    
    # Obtener últimos 5 avisos
    avisos = db.get_ultimos_avisos(5)
    
    return render_template("index.html", avisos=avisos, mensaje=mensaje)

# AGREGAR AVISO
@app.route("/agregar", methods=["GET", "POST"])
def agregar_aviso():
    if request.method == "POST":
        form = request.form
        files = request.files
        
        # Validaciones del lado del servidor
        errores = {}
        
        if not val.validate_region(form.get("select-region")):
            errores["select-region"] = "Debe seleccionar una región"
        
        if not val.validate_comuna(form.get("select-comuna")):
            errores["select-comuna"] = "Debe seleccionar una comuna"
        
        if not val.validate_sector(form.get("input-sector")):
            errores["input-sector"] = "Sector demasiado largo (máx. 100 caracteres)"
        
        if not val.validate_nombre(form.get("nombre")):
            errores["nombre"] = "Nombre debe tener entre 3 y 200 caracteres"
        
        if not val.validate_email(form.get("email")):
            errores["email"] = "Email inválido"
        
        if not val.validate_phone(form.get("numTel")):
            errores["numTel"] = "Teléfono debe ser formato +569XXXXXXXX"
        
        # Validar contactar por
        contactos_validos, contactos = val.validate_contactar_por(form)
        if not contactos_validos:
            errores["contactar_por"] = "Debe seleccionar entre 1 y 5 formas de contacto con sus IDs"
        
        if not val.validate_tipo(form.get("select-tipo")):
            errores["select-tipo"] = "Debe seleccionar perro o gato"
        
        if not val.validate_cantidad(form.get("input-cantidad")):
            errores["input-cantidad"] = "Cantidad debe ser un número mayor a 0"
        
        if not val.validate_edad(form.get("input-edad")):
            errores["input-edad"] = "Edad debe ser un número mayor a 0"
        
        if not val.validate_medida_edad(form.get("select-medidaEdad")):
            errores["select-medidaEdad"] = "Debe seleccionar meses o años"
        
        if not val.validate_fecha_entrega(form.get("fecha-disponible-entrega")):
            errores["fecha-disponible-entrega"] = "Fecha debe ser al menos 3 horas en el futuro"
        
        if not val.validate_descripcion(form.get("input-descripcion")):
            errores["input-descripcion"] = "Descripción demasiado larga (máx. 1000 caracteres)"
        
        # Validar fotos
        fotos_errors = val.validate_fotos(files)
        errores.update(fotos_errors)
        
        # Si hay errores, mostrar formulario con errores
        if errores:
            return render_template(
                "form.html",
                errores=errores,
                data=form
            ), 400
        
        # Si todo está bien, guardar en base de datos
        try:
            data = {
                "comuna_id": form.get("select-comuna"),
                "sector": form.get("input-sector", "").strip() or None,
                "nombre": form.get("nombre"),
                "email": form.get("email"),
                "celular": form.get("numTel"),
                "tipo": form.get("select-tipo"),
                "cantidad": int(form.get("input-cantidad")),
                "edad": int(form.get("input-edad")),
                "unidad_medida": form.get("select-medidaEdad"),
                "fecha_entrega": datetime.strptime(form.get("fecha-disponible-entrega"), "%Y-%m-%dT%H:%M"),
                "descripcion": form.get("input-descripcion", "").strip() or None
            }
            
            # Crear aviso
            aviso_id = db.create_aviso(data)
            
            # Guardar contactos
            for tipo, valor in contactos:
                db.add_contacto(aviso_id, tipo, valor)
            
            # Guardar fotos
            foto1 = files.get('input-foto')
            if foto1 and foto1.filename:
                filename = secure_filename(foto1.filename)
                filepath = os.path.join(app.config["UPLOAD_FOLDER"], filename)
                foto1.save(filepath)
                db.add_foto(aviso_id, filepath, filename)
            
            # Fotos adicionales
            for i in range(2, 6):
                foto = files.get(f'foto{i}')
                if foto and foto.filename:
                    filename = secure_filename(foto.filename)
                    filepath = os.path.join(app.config["UPLOAD_FOLDER"], filename)
                    foto.save(filepath)
                    db.add_foto(aviso_id, filepath, filename)
            
            # Redirigir a portada con mensaje de éxito
            return redirect(url_for('index', mensaje='Aviso agregado exitosamente'))
            
        except Exception as e:
            print(f"Error al guardar: {e}")
            errores["general"] = "Error al guardar el aviso. Intente nuevamente."
            return render_template("form.html", errores=errores, data=form), 500
    
    # GET: Mostrar formulario vacío
    return render_template("form.html", errores={}, data={})

# LISTADO DE AVISOS
@app.route("/listado", methods=["GET"])
def listado_avisos():
    page = request.args.get("page", 1, type=int)
    per_page = 5
    
    avisos, total = db.get_avisos_paginados(page, per_page)
    total_pages = ceil(total / per_page)
    has_prev = page > 1
    has_next = page < total_pages
    
    return render_template(
        "listado.html",
        avisos=avisos,
        page=page,
        total_pages=total_pages,
        has_prev=has_prev,
        has_next=has_next
    )

# DETALLE DE AVISO (para cuando hagan click en una fila)
@app.route("/aviso/<int:aviso_id>", methods=["GET"])
def detalle_aviso(aviso_id):
    aviso = db.get_aviso_by_id(aviso_id)
    if not aviso:
        return redirect(url_for('listado_avisos'))
    
    return render_template("informacion_actividad.html", aviso=aviso)

# ESTADÍSTICAS
@app.route("/estadisticas", methods=["GET"])
def estadisticas():
    return render_template("estadisticas.html")

if __name__ == "__main__":
    app.run(debug=True)