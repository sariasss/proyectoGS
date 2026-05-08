package com.optativaGS.deportesUGR.dto;

public record ClaseResumenDTO(
        Long id,
        java.time.LocalDate fecha,
        String nombreEntrenador,
        String tipoClase
) {}
