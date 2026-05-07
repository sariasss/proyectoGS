package com.optativaGS.deportesUGR.controladores;

import com.optativaGS.deportesUGR.modelos.ClaseTipo3;
import com.optativaGS.deportesUGR.modelos.Especialidad;
import com.optativaGS.deportesUGR.modelos.RolUsuario;
import com.optativaGS.deportesUGR.modelos.Usuario;
import com.optativaGS.deportesUGR.servicios.ClaseService;
import com.optativaGS.deportesUGR.servicios.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class UsuarioController {
    private final UsuarioService usuarioService;
    private final ClaseService claseService;

    @GetMapping("")
    public String mostrarLogin(){
        return "login";
    }

    @PostMapping("/login")
    public String procesarLogin(@RequestParam String email, @RequestParam String password, Model model){
        Usuario usuario = usuarioService.login(email, password);

        if (usuario != null) {
            model.addAttribute("usuario", usuario);

            return switch (usuario.getRol()) {
                case USUARIO -> "indexUsuario";
                case ADMIN -> "redirect:/admin";
                case ENTRENADOR -> "indexEntrenador";
            };
        }
        model.addAttribute("error", "Email o contraseña incorrectos");
        return "login";
    }

    @GetMapping("/admin")
    public String list(Model model){
        model.addAttribute("usuarios", usuarioService.findAll());
        return "indexAdmin";
    }

    @GetMapping("/logout")
    public String logout() {
        return "redirect:/login";
    }

    @GetMapping("/newUsuario")
    public String newUserForm(Model model){

        Usuario usuario = new Usuario();
        usuario.setRol(RolUsuario.USUARIO);

        model.addAttribute("usuario", usuario);

        return "formularioAltaUsuario";
    }

    @GetMapping("/newEntrenador")
    public String newEntrenadorForm(Model model){

        Usuario usuario = new Usuario();
        usuario.setRol(RolUsuario.ENTRENADOR);

        model.addAttribute("usuario", usuario);
        model.addAttribute("especialidades", Especialidad.values());

        return "formularioAltaEntrenador";
    }

    @PostMapping("/saveUsuario")
    public String newUser(@ModelAttribute Usuario usuario){
        usuarioService.save(usuario);
        return "redirect:/admin";
    }

    @GetMapping("/editUsuario/{id}")
    public String editarUsuario(@PathVariable Long id, Model model){
        Usuario usuario = usuarioService.findById(id);
        model.addAttribute("usuario", usuario);

        if(usuario.getRol() == RolUsuario.ENTRENADOR){
            model.addAttribute("especialidades", Especialidad.values());
            return "formularioAltaEntrenador";
        }
        return "formularioAltaUsuario";
    }


    @PostMapping("/newClaseTipo3")
    public String newClass3(@ModelAttribute ClaseTipo3 claseTipo3){
        claseService.save(claseTipo3);
        return "formularioAltaClase3";
    }

    @GetMapping("/delUsuario/{id}")
    public String eliminarUsuario(@PathVariable Long id){
        usuarioService.delete(id);
        return "redirect:/admin";
    }

    @GetMapping("/clases")
    public String listarClases(Model model){
        model.addAttribute("clases", claseService.findAll());
        return "clases";
    }
}
