package appswebs.tarea4.controller;

import appswebs.tarea4.model.*;
import appswebs.tarea4.service.AvisoAdopcionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.*;

@Controller
public class AvisoAdopcionController {

    @Autowired
    private AvisoAdopcionService avisoService;

    @GetMapping("/")
    public String index(@RequestParam(required = false) String mensaje, Model model) {
        List<AvisoAdopcion> avisos = avisoService.getUltimosAvisos(5);
        model.addAttribute("avisos", avisos);
        model.addAttribute("mensaje", mensaje);
        return "index";
    }

    @GetMapping("/agregar")
    public String mostrarFormulario(Model model) {
        model.addAttribute("avisoAdopcion", new AvisoAdopcion());
        model.addAttribute("regiones", avisoService.getRegiones());
        model.addAttribute("errores", new HashMap<>());
        return "form";
    }

    @PostMapping("/agregar")
    public String agregarAviso(
            @RequestParam("select-region") String regionNombre,
            @RequestParam("select-comuna") String comunaNombre,
            @RequestParam(value = "input-sector", required = false) String sector,
            @RequestParam("nombre") String nombre,
            @RequestParam("email") String email,
            @RequestParam("numTel") String celular,
            @RequestParam("select-tipo") String tipo,
            @RequestParam("input-cantidad") Integer cantidad,
            @RequestParam("input-edad") Integer edad,
            @RequestParam("select-medidaEdad") String medida,
            @RequestParam("fecha-disponible-entrega") String fechaEntrega,
            @RequestParam(value = "input-descripcion", required = false) String descripcion,
            @RequestParam(value = "input-foto", required = false) MultipartFile foto1,
            @RequestParam(value = "foto2", required = false) MultipartFile foto2,
            @RequestParam(value = "foto3", required = false) MultipartFile foto3,
            @RequestParam(value = "foto4", required = false) MultipartFile foto4,
            @RequestParam(value = "foto5", required = false) MultipartFile foto5,
            @RequestParam Map<String, String> allParams,
            Model model,
            RedirectAttributes redirectAttributes) {

        Map<String, String> errores = new HashMap<>();

        // Validaciones
        if (regionNombre == null || regionNombre.trim().isEmpty()) {
            errores.put("select-region", "Debe seleccionar una región");
        }

        if (comunaNombre == null || comunaNombre.trim().isEmpty()) {
            errores.put("select-comuna", "Debe seleccionar una comuna");
        }

        if (sector != null && sector.length() > 100) {
            errores.put("input-sector", "Sector demasiado largo");
        }

        if (nombre == null || nombre.length() < 3 || nombre.length() > 200) {
            errores.put("nombre", "Nombre debe tener entre 3 y 200 caracteres");
        }

        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            errores.put("email", "Email inválido");
        }

        if (celular == null || !celular.matches("\\+569\\d{8}")) {
            errores.put("numTel", "Teléfono debe ser formato +569XXXXXXXX");
        }

        if (cantidad == null || cantidad < 1) {
            errores.put("input-cantidad", "Cantidad debe ser mayor a 0");
        }

        if (edad == null || edad < 1) {
            errores.put("input-edad", "Edad debe ser mayor a 0");
        }

        // Validar fecha de entrega (mínimo 3 horas en el futuro)
        try {
            LocalDateTime fechaEntregaDt = LocalDateTime.parse(fechaEntrega);
            if (fechaEntregaDt.isBefore(LocalDateTime.now().plusHours(3))) {
                errores.put("fecha-disponible-entrega", "Fecha debe ser al menos 3 horas en el futuro");
            }
        } catch (Exception e) {
            errores.put("fecha-disponible-entrega", "Fecha inválida");
        }

        if (descripcion != null && descripcion.length() > 1000) {
            errores.put("input-descripcion", "Descripción demasiado larga");
        }

        // Validar contactos (deben tener entre 1 y 5)
        Map<String, String> contactos = new HashMap<>();
        for (String key : allParams.keySet()) {
            if (key.startsWith("contacto-")) {
                String tipo_contacto = key.replace("contacto-", "");
                String valor = allParams.get(key);
                if (valor != null && !valor.trim().isEmpty()) {
                    contactos.put(tipo_contacto, valor);
                }
            }
        }

        if (contactos.isEmpty() || contactos.size() > 5) {
            errores.put("contactar_por", "Debe seleccionar entre 1 y 5 formas de contacto");
        }

        // Validar fotos
        List<MultipartFile> fotos = new ArrayList<>();
        if (foto1 != null && !foto1.isEmpty()) fotos.add(foto1);
        if (foto2 != null && !foto2.isEmpty()) fotos.add(foto2);
        if (foto3 != null && !foto3.isEmpty()) fotos.add(foto3);
        if (foto4 != null && !foto4.isEmpty()) fotos.add(foto4);
        if (foto5 != null && !foto5.isEmpty()) fotos.add(foto5);

        if (fotos.isEmpty()) {
            errores.put("input-foto", "Debe agregar al menos una foto");
        }

        if (fotos.size() > 5) {
            errores.put("input-foto", "Máximo 5 fotos permitidas");
        }

        // Validar tamaño y tipo de fotos
        for (MultipartFile foto : fotos) {
            if (foto.getSize() > 5 * 1024 * 1024) { // 5MB
                errores.put("input-foto", "Las fotos no deben superar 5MB");
                break;
            }
            String contentType = foto.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                errores.put("input-foto", "Solo se permiten imágenes");
                break;
            }
        }

        if (!errores.isEmpty()) {
            model.addAttribute("errores", errores);
            model.addAttribute("regiones", avisoService.getRegiones());
            return "form";
        }

        // Crear aviso
        try {
            Comuna comuna = avisoService.getComunaByNombre(comunaNombre);
            if (comuna == null) {
                errores.put("select-comuna", "Comuna inválida");
                model.addAttribute("errores", errores);
                model.addAttribute("regiones", avisoService.getRegiones());
                return "form";
            }

            AvisoAdopcion aviso = new AvisoAdopcion();
            aviso.setComuna(comuna);
            aviso.setSector(sector);
            aviso.setNombre(nombre);
            aviso.setEmail(email);
            aviso.setCelular(celular);
            aviso.setTipo(AvisoAdopcion.TipoAnimal.valueOf(tipo));
            aviso.setCantidad(cantidad);
            aviso.setEdad(edad);
            aviso.setUnidadMedida(AvisoAdopcion.UnidadMedida.valueOf(medida));
            aviso.setFechaEntrega(LocalDateTime.parse(fechaEntrega));
            aviso.setDescripcion(descripcion);

            avisoService.createAviso(aviso, fotos, contactos);

            redirectAttributes.addAttribute("mensaje", "Aviso agregado exitosamente");
            return "redirect:/";

        } catch (Exception e) {
            e.printStackTrace();
            errores.put("general", "Error al guardar el aviso");
            model.addAttribute("errores", errores);
            model.addAttribute("regiones", avisoService.getRegiones());
            return "form";
        }
    }

    @GetMapping("/listado")
    public String listadoAvisos(@RequestParam(defaultValue = "1") int page, Model model) {
        int pageSize = 5;
        Page<AvisoAdopcion> avisoPage = avisoService.getAvisosPaginados(page, pageSize);

        model.addAttribute("avisos", avisoPage.getContent());
        model.addAttribute("page", page);
        model.addAttribute("totalPages", avisoPage.getTotalPages());
        model.addAttribute("hasPrev", page > 1);
        model.addAttribute("hasNext", page < avisoPage.getTotalPages());

        return "listado";
    }

    @GetMapping("/aviso/{id}")
    public String detalleAviso(@PathVariable Long id, Model model) {
        AvisoAdopcion aviso = avisoService.getAvisoById(id);
        if (aviso == null) {
            return "redirect:/listado";
        }
        model.addAttribute("aviso", aviso);
        return "informacion_actividad";
    }

    @GetMapping("/estadisticas")
    public String estadisticas() {
        return "estadisticas";
    }
}