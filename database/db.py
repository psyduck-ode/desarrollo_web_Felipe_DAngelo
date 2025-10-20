from sqlalchemy import create_engine, Column, Integer, String, DateTime, ForeignKey, Enum, Text
from sqlalchemy.orm import sessionmaker, declarative_base, relationship
from datetime import datetime

DB_NAME = "tarea2"
DB_USERNAME = "cc5002"
DB_PASSWORD = "programacionweb"
DB_HOST = "localhost"
DB_PORT = 3306
DATABASE_URL = f"mysql+pymysql://{DB_USERNAME}:{DB_PASSWORD}@{DB_HOST}:{DB_PORT}/{DB_NAME}"

engine = create_engine(DATABASE_URL, echo=False)
SessionLocal = sessionmaker(bind=engine)
Base = declarative_base()

# Modelos
class Region(Base):
    __tablename__ = "region"
    id = Column(Integer, primary_key=True, autoincrement=True)
    nombre = Column(String(200), nullable=False)

    comunas = relationship("Comuna" , back_populates="region")

class Comuna(Base):
    __tablename__ = "comuna"
    id = Column(Integer, primary_key=True, autoincrement=True)
    nombre = Column(String(200), nullable=False)
    region_id = Column(Integer, ForeignKey("region.id"), nullable=False)

    region = relationship("Region", back_populates="comunas")

class AvisoAdopcion(Base):
    __tablename__ = "aviso_adopcion"
    id = Column(Integer, primary_key=True, autoincrement=True)
    fecha_ingreso = Column(DateTime, default = datetime.now, nullable=False)
    comuna_id = Column(String(200), nullable=False)
    sector = Column(String(100))
    nombre = Column(String(200), nullable=False)
    email = Column(String(100), nullable=False)
    celular = Column(String(15), nullable=False)
    tipo = Column(Enum('perro', 'gato'), nullable=False)
    cantidad = Column(Integer, nullable=False)
    edad = Column(Integer, nullable=False)
    unidad_medida = Column(Enum('meses', 'años'), nullable=False)
    fecha_entrega = Column(DateTime, nullable=False)
    descripcion = Column(String(500))

    fotos = relationship("Foto", back_populates="aviso", cascade="all, delete")
    contactar_por = relationship("ContactarPor", back_populates="aviso", cascade="all, delete")

class Foto(Base):
    __tablename__ = "foto"
    id = Column(Integer, primary_key=True, autoincrement=True)
    ruta_archivo = Column(String(300), nullable=False)
    nombre_archivo = Column(String(300), nullable=False)
    actividad_id = Column(Integer, ForeignKey("aviso_adopcion.id"), nullable=False)

    actividad = relationship("AvisoAdopcion", back_populates="fotos")
    aviso = relationship('AvisoAdopcion', back_populates='fotos')

class ContactarPor(Base):
    __tablename__ = "contactar_por"
    id = Column(Integer, primary_key=True, autoincrement=True)
    nombre = Column(String(50), nullable=False)
    identificador = Column(String(50), nullable=False)
    actividad_id = Column(Integer, ForeignKey("aviso_adopcion.id"), nullable=False)

    actividad = relationship("AvisoAdopcion", back_populates="contactar_por")
    aviso = relationship('AvisoAdopcion', back_populates='contactar_por')

# Funciones
def get_ultimos_avisos(limit=5):
    session = SessionLocal()
    avisos = session.query(AvisoAdopcion).order_by(AvisoAdopcion.fecha_ingreso.desc()).limit(limit).all()
    session.close()
    return avisos

def get_avisos_paginados(page=1, per_page=5):
    session = SessionLocal()
    offset = (page - 1) * per_page
    avisos = session.query(AvisoAdopcion).order_by(AvisoAdopcion.fecha_ingreso.desc()).offset(offset).limit(per_page).all()
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