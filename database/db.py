from sqlalchemy import create_engine, Column, Integer, String, DateTime, ForeignKey, Enum, Text
from sqlalchemy.orm import sessionmaker, declarative_base, relationship
from datetime import datetime

def get_ultimos_avisos(limit=5):
    session = SessionLocal()
    avisos = session.query(AvisoAdopcion).order_by(AvisoAdopcion.fecha_publicacion.desc()).limit(limit).all()
    session.close()
    return avisos

def get_avisos_paginados(page=1, per_page=5):
    session = SessionLocal()
    offset = (page - 1) * per_page
    avisos = session.query(AvisoAdopcion).order_by(AvisoAdopcion.fecha_publicacion.desc()).offset(offset).limit(per_page).all()
    total = session.query(AvisoAdopcion).count()
    session.close()
    return avisos, total

def get_aviso_by_id(aviso_id):
    session = SessionLocal()
    aviso = session.query(AvisoAdopcion).get(aviso_id)
    session.close()
    return aviso

def create_aviso(data):
    session = SessionLocal()
    nuevo = AvisoAdopcion(**data)
    session.add(nuevo)
    session.commit()
    session.refresh(nuevo)
    aviso_id = nuevo.id
    session.close()
    return aviso_id

def add_contacto(aviso_id, tipo, valor):
    session = SessionLocal()
    contacto = ContactarPor(aviso_id=aviso_id, tipo=tipo, valor=valor)
    session.add(contacto)
    session.commit()
    session.close()

def add_foto(aviso_id, ruta_archivo, nombre_archivo):
    session = SessionLocal()
    foto = Foto(aviso_id=aviso_id, ruta_archivo=ruta_archivo, nombre_archivo=nombre_archivo)
    session.add(foto)
    session.commit()
    session.close()

def get_regiones():
    session = SessionLocal()
    regiones = session.query(Region).order_by(Region.nombre).all()
    session.close()
    return regiones

def get_comunas_by_region(region_nombre):
    session = SessionLocal()
    region = session.query(Region).filter(Region.nombre == region_nombre).first()
    if region:
        comunas = session.query(Comuna).filter(Comuna.region_id == region.id).order_by(Comuna.nombre).all()
    else:
        comunas = []
    session.close()
    return comunas