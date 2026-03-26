package appswebs.tarea4.service;

import appswebs.tarea4.model.*;
import appswebs.tarea4.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class AvisoAdopcionService {

    @Autowired
    private AvisoAdopcionRepository avisoRepository;

    @Autowired
    private ComunaRepository comunaRepository;

    @Autowired
    private FotoRepository fotoRepository;

    @Autowired
    private ContactarPorRepository contactarPorRepository;

    @Autowired
    private ComentarioRepository comentarioRepository;

    @Autowired
    private RegionRepository regionRepository;

    private static final String UPLOAD_DIR = "static/uploads";

    public AvisoAdopcionService() {
        // Crear directorio de uploads si no existe
        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
        } catch (IOException e) {
            throw new RuntimeException("No se pudo crear el directorio de uploads", e);
        }
    }

    @Transactional(readOnly = true)
    public List<AvisoAdopcion> getUltimosAvisos(int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "fechaIngreso"));
        return avisoRepository.findAll(pageable).getContent();
    }

    @Transactional(readOnly = true)
    public Page<AvisoAdopcion> getAvisosPaginados(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "fechaIngreso"));
        return avisoRepository.findAllByOrderByFechaIngresoDesc(pageable);
    }

    @Transactional(readOnly = true)
    public AvisoAdopcion getAvisoById(Long id) {
        return avisoRepository.findById(id).orElse(null);
    }

    @Transactional
    public Long createAviso(AvisoAdopcion aviso, List<MultipartFile> fotos, 
                            Map<String, String> contactos) throws IOException {
        
        // Guardar el aviso
        AvisoAdopcion savedAviso = avisoRepository.save(aviso);

        // Guardar fotos
        if (fotos != null && !fotos.isEmpty()) {
            for (MultipartFile file : fotos) {
                if (!file.isEmpty()) {
                    String filename = saveFile(file);
                    Foto foto = new Foto();
                    foto.setNombreArchivo(filename);
                    foto.setRutaArchivo(UPLOAD_DIR + "/" + filename);
                    foto.setAviso(savedAviso);
                    fotoRepository.save(foto);
                }
            }
        }

        // Guardar contactos
        if (contactos != null && !contactos.isEmpty()) {
            for (Map.Entry<String, String> entry : contactos.entrySet()) {
                ContactarPor contacto = new ContactarPor();
                contacto.setNombre(entry.getKey());
                contacto.setIdentificador(entry.getValue());
                contacto.setAviso(savedAviso);
                contactarPorRepository.save(contacto);
            }
        }

        return savedAviso.getId();
    }

    private String saveFile(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String filename = timestamp + "_" + originalFilename;
        
        Path uploadPath = Paths.get(UPLOAD_DIR);
        Path filePath = uploadPath.resolve(filename);
        
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        return filename;
    }

    @Transactional(readOnly = true)
    public List<Region> getRegiones() {
        return regionRepository.findAllByOrderByNombreAsc();
    }

    @Transactional(readOnly = true)
    public List<Comuna> getComunasByRegionId(Long regionId) {
        return comunaRepository.findByRegionIdOrderByNombreAsc(regionId);
    }

    @Transactional(readOnly = true)
    public Comuna getComunaByNombre(String nombre) {
        return comunaRepository.findByNombre(nombre).orElse(null);
    }

    // Métodos para comentarios
    @Transactional(readOnly = true)
    public List<Comentario> getComentariosByAvisoId(Long avisoId) {
        return comentarioRepository.findByAvisoIdOrderByFechaDesc(avisoId);
    }

    @Transactional
    public Long addComentario(String nombre, String texto, Long avisoId) {
        AvisoAdopcion aviso = avisoRepository.findById(avisoId)
            .orElseThrow(() -> new RuntimeException("Aviso no encontrado"));
        
        Comentario comentario = new Comentario();
        comentario.setNombre(nombre);
        comentario.setTexto(texto);
        comentario.setAviso(aviso);
        comentario.setFecha(LocalDateTime.now());
        
        Comentario saved = comentarioRepository.save(comentario);
        return saved.getId();
    }

    // Métodos para estadísticas
    @Transactional(readOnly = true)
    public Map<String, Object> getAvisosPorDia() {
        List<Object[]> resultados = avisoRepository.countByDia();
        
        List<String> fechas = new ArrayList<>();
        List<Long> cantidades = new ArrayList<>();
        
        for (Object[] resultado : resultados) {
            LocalDate fecha = ((java.sql.Date) resultado[0]).toLocalDate();
            fechas.add(fecha.toString());
            cantidades.add(((Number) resultado[1]).longValue());
        }
        
        Map<String, Object> data = new HashMap<>();
        data.put("fechas", fechas);
        data.put("cantidades", cantidades);
        
        return data;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getAvisosPorTipo() {
        List<Object[]> resultados = avisoRepository.countByTipo();
        
        List<String> tipos = new ArrayList<>();
        List<Long> cantidades = new ArrayList<>();
        
        for (Object[] resultado : resultados) {
            tipos.add(resultado[0].toString());
            cantidades.add(((Number) resultado[1]).longValue());
        }
        
        Map<String, Object> data = new HashMap<>();
        data.put("tipos", tipos);
        data.put("cantidades", cantidades);
        
        return data;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getAvisosPorMesYTipo() {
        List<Object[]> resultados = avisoRepository.countByMesAndTipo();
        
        String[] meses = {"Ene", "Feb", "Mar", "Abr", "May", "Jun", 
                         "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"};
        List<Long> perros = new ArrayList<>(Arrays.asList(new Long[12]));
        List<Long> gatos = new ArrayList<>(Arrays.asList(new Long[12]));
        Collections.fill(perros, 0L);
        Collections.fill(gatos, 0L);
        
        for (Object[] resultado : resultados) {
            int mes = ((Number) resultado[0]).intValue() - 1;
            String tipo = resultado[1].toString();
            Long cantidad = ((Number) resultado[2]).longValue();
            
            if ("perro".equals(tipo)) {
                perros.set(mes, cantidad);
            } else {
                gatos.set(mes, cantidad);
            }
        }
        
        Map<String, Object> data = new HashMap<>();
        data.put("meses", Arrays.asList(meses));
        data.put("perros", perros);
        data.put("gatos", gatos);
        
        return data;
    }
}