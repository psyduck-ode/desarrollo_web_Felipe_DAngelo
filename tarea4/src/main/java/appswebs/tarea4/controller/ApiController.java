package appswebs.tarea4.controller;

import appswebs.tarea4.model.Comentario;
import appswebs.tarea4.service.AvisoAdopcionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ApiController {

    @Autowired
    private AvisoAdopcionService avisoService;

    @GetMapping("/avisos/{avisoId}/comentarios")
    public ResponseEntity<?> obtenerComentarios(@PathVariable Long avisoId) {
        try {
            List<Comentario> comentarios = avisoService.getComentariosByAvisoId(avisoId);
            
            List<Map<String, Object>> resultado = comentarios.stream().map(c -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", c.getId());
                map.put("nombre", c.getNombre());
                map.put("texto", c.getTexto());
                map.put("fecha", c.getFecha().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                return map;
            }).collect(Collectors.toList());
            
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al cargar comentarios: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping("/avisos/{avisoId}/comentarios")
    public ResponseEntity<?> agregarComentario(
            @PathVariable Long avisoId,
            @RequestBody Map<String, String> datos) {
        
        try {
            String nombre = datos.get("nombre");
            String texto = datos.get("texto");
            
            Map<String, String> errores = new HashMap<>();
            
            // Validaciones
            if (nombre == null || nombre.trim().isEmpty() || nombre.length() < 3 || nombre.length() > 80) {
                errores.put("nombre", "El nombre debe tener entre 3 y 80 caracteres");
            }
            
            if (texto == null || texto.trim().isEmpty() || texto.length() < 5 || texto.length() > 300) {
                errores.put("texto", "El comentario debe tener entre 5 y 300 caracteres");
            }
            
            if (!errores.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("errores", errores);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            
            Long comentarioId = avisoService.addComentario(nombre.trim(), texto.trim(), avisoId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Comentario agregado exitosamente");
            response.put("id", comentarioId);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (RuntimeException e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "Aviso no encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al guardar el comentario: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/estadisticas/avisos-por-dia")
    public ResponseEntity<?> avisosPorDia() {
        try {
            Map<String, Object> data = avisoService.getAvisosPorDia();
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al obtener estadísticas: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/estadisticas/avisos-por-tipo")
    public ResponseEntity<?> avisosPorTipo() {
        try {
            Map<String, Object> data = avisoService.getAvisosPorTipo();
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al obtener estadísticas: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/estadisticas/avisos-por-mes-tipo")
    public ResponseEntity<?> avisosPorMesYTipo() {
        try {
            Map<String, Object> data = avisoService.getAvisosPorMesYTipo();
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al obtener estadísticas: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/regiones")
    public ResponseEntity<?> getRegiones() {
        try {
            List<appswebs.tarea4.model.Region> regiones = avisoService.getRegiones();
            
            List<Map<String, Object>> resultado = regiones.stream().map(r -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", r.getId());
                map.put("nombre", r.getNombre());
                return map;
            }).collect(Collectors.toList());
            
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al cargar regiones: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    @GetMapping("/regiones/{regionNombre}/comunas")
    public ResponseEntity<?> getComunas(@PathVariable String regionNombre) {
        try {
            // Buscar región por nombre
            List<appswebs.tarea4.model.Region> regiones = avisoService.getRegiones();
            appswebs.tarea4.model.Region region = regiones.stream()
                .filter(r -> r.getNombre().equals(regionNombre))
                .findFirst()
                .orElse(null);
            
            if (region == null) {
                return ResponseEntity.ok(Collections.emptyList());
            }
            
            List<appswebs.tarea4.model.Comuna> comunas = avisoService.getComunasByRegionId(region.getId());
            
            List<Map<String, Object>> resultado = comunas.stream().map(c -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", c.getId());
                map.put("nombre", c.getNombre());
                return map;
            }).collect(Collectors.toList());
            
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al cargar comunas: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}