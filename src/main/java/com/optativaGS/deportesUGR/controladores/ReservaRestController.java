package com.optativaGS.deportesUGR.controladores;

import com.optativaGS.deportesUGR.dto.ReservaDTO;
import com.optativaGS.deportesUGR.modelos.Reserva;
import com.optativaGS.deportesUGR.servicios.ReservaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservas")
@RequiredArgsConstructor
public class ReservaRestController {

    private final ReservaService reservaService;

    @GetMapping
    public List<ReservaDTO> findAll() {
        return reservaService.findAll();
    }

    @GetMapping("/{id}")
    public ReservaDTO findById(@PathVariable Long id) {
        Reserva reserva = reservaService.findById(id);

        if (reserva == null) {
            return null;
        }

        return toDTO(reserva);
    }

    @PostMapping
    public ReservaDTO create(@RequestBody Reserva reserva) {
        reservaService.save(reserva);
        return toDTO(reserva);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        reservaService.delete(id);
    }

    private ReservaDTO toDTO(Reserva reserva) {
        return new ReservaDTO(
                reserva.getId(),
                reserva.getEstado().name(),
                reserva.getUsuario().getNombre(),
                reserva.getClase().getId(),
                reserva.getFecha()
        );
    }
}