package com.optativaGS.deportesUGR.controladores;

import com.optativaGS.deportesUGR.dto.ClaseResumenDTO;
import com.optativaGS.deportesUGR.modelos.Clase;
import com.optativaGS.deportesUGR.modelos.ClaseTipo1;
import com.optativaGS.deportesUGR.modelos.ClaseTipo2;
import com.optativaGS.deportesUGR.modelos.ClaseTipo3;
import com.optativaGS.deportesUGR.servicios.ClaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/clases")
@RequiredArgsConstructor
public class ClaseRestController {

    private final ClaseService claseService;

    @GetMapping
    public List<ClaseResumenDTO> findAll() {
        return claseService.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @GetMapping("/{id}")
    public ClaseResumenDTO findById(@PathVariable Long id) {
        Clase clase = claseService.findById(id);

        if (clase == null) {
            return null;
        }

        return toDTO(clase);
    }

    @PostMapping
    public ClaseResumenDTO create(@RequestBody Map<String, Object> body) {
        Clase clase = claseService.create(body);
        return toDTO(clase);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        claseService.delete(id);
    }

    private ClaseResumenDTO toDTO(Clase clase) {
        String nombreEntrenador = null;

        if (clase.getEntrenador() != null) {
            nombreEntrenador = clase.getEntrenador().getNombre();
        }

        String tipoClase;

        if (clase instanceof ClaseTipo1) {
            tipoClase = "ClaseTipo1";
        } else if (clase instanceof ClaseTipo2) {
            tipoClase = "ClaseTipo2";
        } else if (clase instanceof ClaseTipo3) {
            tipoClase = "ClaseTipo3";
        } else {
            tipoClase = "Clase";
        }

        return new ClaseResumenDTO(
                clase.getId(),
                clase.getFecha(),
                nombreEntrenador,
                clase.getEspecialidad(),
                tipoClase,
                clase.getDuracionMinutos()
        );
    }
}