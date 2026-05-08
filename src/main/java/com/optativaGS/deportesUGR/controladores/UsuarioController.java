package com.optativaGS.deportesUGR.controladores;

import com.optativaGS.deportesUGR.dto.UsuarioDTO;
import com.optativaGS.deportesUGR.modelos.ClaseTipo3;
import com.optativaGS.deportesUGR.modelos.Especialidad;
import com.optativaGS.deportesUGR.modelos.RolUsuario;
import com.optativaGS.deportesUGR.modelos.Usuario;
import com.optativaGS.deportesUGR.servicios.ClaseService;
import com.optativaGS.deportesUGR.servicios.UsuarioService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
                case USUARIO -> "indexUsuario";
                case ADMIN -> "redirect:/admin";
                case ENTRENADOR -> "indexEntrenador";
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
        usuarioService.save(dto);
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
}
