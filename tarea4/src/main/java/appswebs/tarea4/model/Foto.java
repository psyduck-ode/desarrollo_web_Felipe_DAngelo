package appswebs.tarea4.model;

import jakarta.persistence.*;

@Entity
@Table(name = "foto")
public class Foto {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "ruta_archivo", nullable = false, length = 300)
    private String rutaArchivo;
    
    @Column(name = "nombre_archivo", nullable = false, length = 300)
    private String nombreArchivo;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actividad_id", nullable = false)
    private AvisoAdopcion aviso;

    // Constructores
    public Foto() {}
    
    public Foto(String rutaArchivo, String nombreArchivo, AvisoAdopcion aviso) {
        this.rutaArchivo = rutaArchivo;
        this.nombreArchivo = nombreArchivo;
        this.aviso = aviso;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRutaArchivo() {
        return rutaArchivo;
    }

    public void setRutaArchivo(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    public AvisoAdopcion getAviso() {
        return aviso;
    }

    public void setAviso(AvisoAdopcion aviso) {
        this.aviso = aviso;
    }
}