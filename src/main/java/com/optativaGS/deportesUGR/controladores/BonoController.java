package com.optativaGS.deportesUGR.controladores;

import com.optativaGS.deportesUGR.modelos.Especialidad;
import com.optativaGS.deportesUGR.modelos.TipoBono;
import com.optativaGS.deportesUGR.servicios.BonoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class BonoController {
    private final BonoService bonoService;

    //Compra el bono para el usuario
    @PostMapping("/comprarBono/{id}")
    public String comprarBono(@PathVariable Long id, @RequestParam TipoBono tipo) {
        bonoService.comprarBono(id, tipo);
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

    //Confirmar la compra del bono
    @PostMapping("/confirmarCompraBono")
    public String confirmarCompraBono(@RequestParam Long usuarioId, @RequestParam TipoBono tipo, @RequestParam Especialidad especialidad) {
        bonoService.comprarBonoConEspecialidad(usuarioId, tipo, especialidad);
        return "redirect:/indexUsuario/" + usuarioId;
    }

    //Cancelar uso del bono
    @PostMapping("/uso/cancelar/{id}")
    public String cancelarUso(@PathVariable Long id, @RequestParam Long usuarioId) {
        bonoService.cancelarYReasignar(id);
        return "redirect:/indexUsuario/" + usuarioId;
    }

}
