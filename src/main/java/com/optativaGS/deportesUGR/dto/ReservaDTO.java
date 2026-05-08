package com.optativaGS.deportesUGR.dto;

import java.time.LocalDate;

public record ReservaDTO(
        Long id,
        String estado,
        String nombreUsuario,
        Long claseId,
        LocalDate fecha
) {}
