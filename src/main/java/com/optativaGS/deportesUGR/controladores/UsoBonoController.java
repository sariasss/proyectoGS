package com.optativaGS.deportesUGR.controladores;

import com.optativaGS.deportesUGR.servicios.UsoBonoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
@RequiredArgsConstructor
public class UsoBonoController {
    private final UsoBonoService usoBonoService;

    //Solicitar cambio de clase tipo 2
    @PostMapping("/uso/solicitar-cambio/{id}")
    public String solicitarCambio(@PathVariable Long id, @RequestParam Long usuarioId, @RequestParam String nuevaFecha) {
        LocalDate fecha = LocalDate.parse(nuevaFecha);
        usoBonoService.solicitarCambio(id, fecha);
        return "redirect:/indexUsuario/" + usuarioId;
    }

    //Aceptar la solicitud del usuario
    @PostMapping("/solicitud/aceptar/{id}")
    public String aceptarSolicitud(@PathVariable Long id, @RequestParam Long entrenadorId) {
        usoBonoService.aceptarCambioFecha(id);
        return "redirect:/clasesEntrenador/" + entrenadorId;
    }

    //Rechazar la solicitud del usuario
    @PostMapping("/solicitud/rechazar/{id}")
    public String rechazarSolicitud(@PathVariable Long id, @RequestParam Long entrenadorId) {
        usoBonoService.rechazarCambioFecha(id);
        return "redirect:/clasesEntrenador/" + entrenadorId;
    }
}
