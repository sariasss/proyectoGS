package com.optativaGS.deportesUGR.controladores;

import com.optativaGS.deportesUGR.servicios.ClaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class ClaseController {

    private final ClaseService claseService;

    @GetMapping("/clases3")
    public String listarTipo3(Model model) {
        model.addAttribute("clases", claseService.findAllTipo3());
        return "listaClases3";
    }
}