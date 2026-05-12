package com.optativaGS.deportesUGR.dto;

import com.optativaGS.deportesUGR.modelos.Especialidad;

import java.time.LocalDateTime;


public record ClaseResumenDTO(
        Long id,
        LocalDateTime fecha,
        String nombreEntrenador,
        Especialidad especialidad,
        String tipoClase,
        Integer duracionEnMinutos
) {}
