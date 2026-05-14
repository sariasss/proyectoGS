package com.optativaGS.deportesUGR.controladores;

import com.optativaGS.deportesUGR.dto.UsuarioDTO;
import com.optativaGS.deportesUGR.modelos.*;
import com.optativaGS.deportesUGR.servicios.ClaseService;
import com.optativaGS.deportesUGR.servicios.UsuarioService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class UsuarioController {
    private final UsuarioService usuarioService;
    private final ClaseService claseService;

    //Página de login
    @GetMapping("")
    public String mostrarLogin(){
        return "login";
    }

    //Recoge los datos del login y devuelve una página según quién ha iniciado sesión
    @PostMapping("/login")
    public String procesarLogin(@RequestParam String email, @RequestParam String password, Model model){
        UsuarioDTO usuario = usuarioService.login(email, password);

        if (usuario != null) {
            model.addAttribute("usuario", usuario);

            return switch (usuario.rol()) {
                case USUARIO -> "redirect:/indexUsuario/" + usuario.id();
                case ADMIN -> "redirect:/admin";
                case ENTRENADOR -> "redirect:/clasesEntrenador/" + usuario.id();
            };
        }
        model.addAttribute("error", "Email o contraseña incorrectos");
        return "login";
    }

    //Devuelve una tabla con todos los usuarios para el admin
    @GetMapping("/admin")
    public String list(Model model){
        model.addAttribute("usuarios", usuarioService.findAll());
        return "indexAdmin";
    }

    //Página de logout
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        //Spring se encarga de borrar lo que haya en la sesión para cerrarla
        session.invalidate();
        return "redirect:/";
    }

    //Formulario para crear un nuevo usuario (solo para admin)
    @GetMapping("/newUsuario")
    public String newUserForm(Model model){

        Usuario usuario = new Usuario();
        usuario.setRol(RolUsuario.USUARIO);

        model.addAttribute("usuario", usuario);
        return "formularioAltaUsuario";
    }

    //Formulario para crear un nuevo entrenador (solo para admin)
    @GetMapping("/newEntrenador")
    public String newEntrenadorForm(Model model){

        Usuario usuario = new Usuario();
        usuario.setRol(RolUsuario.ENTRENADOR);

        model.addAttribute("usuario", usuario);
        model.addAttribute("especialidades", Especialidad.values());

        return "formularioAltaEntrenador";
    }

    //Recoge la información del save usuario y la guarda en la base de datos
    @PostMapping("/saveUsuario")
    public String newUser(@ModelAttribute Usuario usuario){
        UsuarioDTO dto = new UsuarioDTO(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getTelefono(),
                usuario.getPassword(),
                usuario.getRol(),
                usuario.getEspecialidad()
        );
        Usuario usuarioGuardado= usuarioService.save(dto);
        if(usuarioGuardado.getRol()== RolUsuario.ENTRENADOR){
            claseService.generarCalendarioAnual(usuarioGuardado);
        }
        return "redirect:/admin";
    }

    //Formulario para editar un usuario dependiendo de su rol (solo para admin)
    @GetMapping("/editUsuario/{id}")
    public String editarUsuario(@PathVariable Long id, Model model){
        UsuarioDTO usuario = usuarioService.findById(id);
        model.addAttribute("usuario", usuario);

        if(usuario.rol() == RolUsuario.ENTRENADOR){
            model.addAttribute("especialidades", Especialidad.values());
            return "formularioAltaEntrenador";
        }
        return "formularioAltaUsuario";
    }

    //Formulario para crear una nueva clase de tipo 3 (solo para admin)
    @GetMapping("/newClaseTipo3")
    public String nuevaClaseTipo3Form(Model model) {
        model.addAttribute("clase3", new ClaseTipo3());
        model.addAttribute("entrenadores", usuarioService.findEntrenadores());
        model.addAttribute("especialidades", Especialidad.values());
        return "formularioAltaClase3";
    }

    //Recoge la información del save clase tipo 3 y la guarda en la base de datos
    @PostMapping("/saveClaseTipo3")
    public String newClass3(@ModelAttribute("clase3") ClaseTipo3 claseTipo3){
        claseService.save(claseTipo3);
        return "redirect:/admin";
    }

    //Elimina un usuario
    @GetMapping("/delUsuario/{id}")
    public String eliminarUsuario(@PathVariable Long id){
        usuarioService.delete(id);
        return "redirect:/admin";
    }

    //Devuelve una lista de clases
    @GetMapping("/clases")
    public String listarClases3(Model model){
        model.addAttribute("clases", claseService.findAll());
        return "listaClases3";
    }

    //Muestra la pantalla del entrenador
    @GetMapping("/clasesEntrenador/{id}")
    public String mostrarPanelEntrenador(@PathVariable Long id, Model model) {
        UsuarioDTO entrenador = usuarioService.findById(id);

        if (entrenador == null || entrenador.rol() != RolUsuario.ENTRENADOR) {
            return "redirect:/login";
        }

        model.addAttribute("entrenador", entrenador);
        model.addAttribute("clases", claseService.findByEntrenadorId(id));

        return "indexEntrenador";
    }

    //Apuntarse a una clase
    @PostMapping("/inscribir")
    public String inscribirEnClase(@RequestParam Long usuarioId, @RequestParam Long claseId) {
        usuarioService.inscribirUsuarioEnClase(usuarioId, claseId);

        return "redirect:/indexUsuario/" + usuarioId;
    }

    //Muestra la pantalla del entrenador
    @GetMapping("/indexUsuario/{id}")
    public String indexUsuario(@PathVariable Long id, Model model) {
        UsuarioDTO usuarioDTO = usuarioService.findById(id);
        Usuario usuarioReal = usuarioService.findEntityById(id);

        List<Bono> misBonos = new ArrayList<>();
        if (usuarioReal != null && usuarioReal.getBonos() != null) {
            misBonos = usuarioReal.getBonos();
            misBonos.forEach(bono -> {
                if (bono.getUsos() != null) {
                    bono.getUsos().sort(Comparator.comparing(UsoBono::getFecha));
                }
            });
        }

        List<Especialidad> misEspecialidades = misBonos.stream()
                .map(Bono::getEspecialidad)
                .toList();

        List<Clase> todasLasClases = claseService.findAll();

        List<Clase> clasesBonos = todasLasClases.stream()
                .filter(c -> !(c instanceof ClaseTipo3))
                .filter(c -> misEspecialidades.contains(c.getEspecialidad()))
                .toList();

        List<Clase> clasesEspeciales = todasLasClases.stream()
                .filter(c -> c instanceof ClaseTipo3)
                .toList();

        model.addAttribute("usuario", usuarioDTO);
        model.addAttribute("misBonos", misBonos);
        model.addAttribute("clases", clasesBonos);
        model.addAttribute("clasesEspeciales", clasesEspeciales);
        model.addAttribute("tieneBonos", !misBonos.isEmpty());

        return "indexUsuario";
    }

    @PostMapping("/comprarBono/{id}")
    public String comprarBono(@PathVariable Long id, @RequestParam TipoBono tipo) {
        usuarioService.comprarBono(id, tipo);

        return "redirect:/indexUsuario/" + id;
    }

    //Pantalla intermedia para elegir especialidad del bono
    @GetMapping("/elegirEspecialidad/{usuarioId}/{tipo}")
    public String elegirEspecialidad(@PathVariable Long usuarioId, @PathVariable TipoBono tipo, Model model) {
        model.addAttribute("usuarioId", usuarioId);
        model.addAttribute("tipoBono", tipo);
        model.addAttribute("especialidades", Especialidad.values());
        return "elegirEspecialidad";
    }

    @PostMapping("/confirmarCompraBono")
    public String confirmarCompraBono(
            @RequestParam Long usuarioId,
            @RequestParam TipoBono tipo,
            @RequestParam Especialidad especialidad) {

        usuarioService.comprarBonoConEspecialidad(usuarioId, tipo, especialidad);

        return "redirect:/indexUsuario/" + usuarioId;
    }

    @PostMapping("/uso/cancelar/{id}")
    public String cancelarUso(@PathVariable Long id, @RequestParam Long usuarioId) {
        usuarioService.cancelarYReasignar(id);
        return "redirect:/indexUsuario/" + usuarioId;
    }

    @PostMapping("/uso/solicitar-cambio/{id}")
    public String solicitarCambio(@PathVariable Long id, @RequestParam Long usuarioId) {
        usuarioService.solicitarCambio(id);
        return "redirect:/indexUsuario/" + usuarioId;
    }

    @PostMapping("/clase/inscribir-especial/{claseId}")
    public String inscribirEspecial(@PathVariable Long claseId, @RequestParam Long usuarioId) {
        try {
            usuarioService.inscribirEnClaseEspecial(usuarioId, claseId);
        } catch (Exception e) {
        }
        return "redirect:/indexUsuario/" + usuarioId;
    }
}
