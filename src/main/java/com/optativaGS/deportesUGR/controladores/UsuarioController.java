package com.optativaGS.deportesUGR.controladores;

import com.optativaGS.deportesUGR.servicios.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class UsuarioController {
    private final UsuarioService usuarioService;

    @GetMapping("/usuarios")
    public String listar(Model model){
        model.addAttribute("usuarios", usuarioService.findAll());
        return "usuarioslista";
    }
}
