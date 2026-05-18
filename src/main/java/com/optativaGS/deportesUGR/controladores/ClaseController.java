package com.optativaGS.deportesUGR.controladores;

import com.optativaGS.deportesUGR.modelos.Clase;
import com.optativaGS.deportesUGR.modelos.ClaseTipo3;
import com.optativaGS.deportesUGR.modelos.Especialidad;
import com.optativaGS.deportesUGR.modelos.RolUsuario;
import com.optativaGS.deportesUGR.servicios.ClaseService;
import com.optativaGS.deportesUGR.servicios.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class ClaseController {
    private final ClaseService claseService;
    private final UsuarioService usuarioService;

    //Devuelve una lista de clases
    @GetMapping("/clases")
    public String listarClases3(Model model){
        model.addAttribute("clases", claseService.findAll());
        return "listaClases3";
    }

    //Formulario para editar las clases de tipo 3
    @GetMapping("/editClaseTipo3/{id}")
    public String editClaseTipo3Form(@PathVariable Long id, Model model) {
        Clase clase = claseService.findById(id);

        if (clase instanceof ClaseTipo3) {
            model.addAttribute("clase3", clase);
            model.addAttribute("entrenadores", usuarioService.findEntrenadores());
            model.addAttribute("especialidades", Especialidad.values());
            return "formularioAltaClase3";
        }
        return "redirect:/clases";
    }

    //Eliminar clases del tipo 3
    @GetMapping("/delClaseTipo3/{id}")
    public String eliminarClaseTipo3(@PathVariable Long id) {
        claseService.delete(id);
        return "redirect:/clases";
    }

    //Formulario para crear una nueva clase de tipo 3 (solo para admin)
    @GetMapping("/newClaseTipo3")
    public String nuevaClaseTipo3Form(Model model) {
        model.addAttribute("clase3", new ClaseTipo3());
        model.addAttribute("especialidades", Especialidad.values());
        return "formularioAltaClase3";
    }

    @PostMapping("/filtrarClaseTipo3")
    public String filtrar(@ModelAttribute("clase3") ClaseTipo3 claseTipo3, Model model) {

        model.addAttribute("clase3", claseTipo3);
        model.addAttribute("especialidades", Especialidad.values());
        model.addAttribute("entrenadores",
                usuarioService.findByRolAndEspecialidad(
                        RolUsuario.ENTRENADOR,
                        claseTipo3.getEspecialidad()
                )
        );

        return "formularioAltaClase3";
    }

    @PostMapping("/saveClaseTipo3")
    public String save(@ModelAttribute("clase3") ClaseTipo3 claseTipo3) {
        claseService.save(claseTipo3);
        return "redirect:/admin";
    }

    @GetMapping("/clasesEntrenador/{id}/tipo1")
    public String listarTipo1(@PathVariable Long id, Model model) {
        model.addAttribute("entrenador", usuarioService.findById(id));
        model.addAttribute("clases", claseService.findTipo1ByEntrenadorId(id));
        model.addAttribute("mostrandoHistorial", false);
        model.addAttribute("mostrandoTipo", "tipo1");
        return "indexEntrenador";
    }

    @GetMapping("/clasesEntrenador/{id}/tipo2")
    public String listarTipo2(@PathVariable Long id, Model model) {
        model.addAttribute("entrenador", usuarioService.findById(id));
        model.addAttribute("clases", claseService.findTipo2ByEntrenadorId(id));
        model.addAttribute("mostrandoHistorial", false);
        model.addAttribute("mostrandoTipo", "tipo2");
        return "indexEntrenador";
    }

    @GetMapping("/clasesEntrenador/{id}/tipo3")
    public String listarTipo3(@PathVariable Long id, Model model) {
        model.addAttribute("entrenador", usuarioService.findById(id));
        model.addAttribute("clases", claseService.findTipo3ByEntrenadorId(id));
        model.addAttribute("mostrandoHistorial", false);
        model.addAttribute("mostrandoTipo", "tipo3");
        return "indexEntrenador";
    }
}