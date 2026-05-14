package com.optativaGS.deportesUGR.controladores;

import com.optativaGS.deportesUGR.dto.BonoDTO;
import com.optativaGS.deportesUGR.modelos.Bono;
import com.optativaGS.deportesUGR.servicios.BonoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bonos")
@RequiredArgsConstructor
public class BonoRestController {

    private final BonoService bonoService;

    @GetMapping
    public List<BonoDTO> findAll() {
        return bonoService.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @GetMapping("/{id}")
    public BonoDTO findById(@PathVariable Long id) {
        Bono bono = bonoService.findById(id);

        if (bono == null) {
            return null;
        }

        return toDTO(bono);
    }

    @PostMapping
    public BonoDTO create(@RequestBody Bono bono) {
        bonoService.save(bono);
        return toDTO(bono);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        bonoService.delete(id);
    }

    private BonoDTO toDTO(Bono bono) {
        int usosConsumidos = 0;

        if (bono.getUsos() != null) {
            usosConsumidos = bono.getUsos().size();
        }

        Long usuarioId = null;

        if (bono.getUsuario() != null) {
            usuarioId = bono.getUsuario().getId();
        }

        return new BonoDTO(
                bono.getId(),
                bono.getMax_usos(),
                bono.getTipo(),
                usuarioId,
                usosConsumidos
        );
    }
}