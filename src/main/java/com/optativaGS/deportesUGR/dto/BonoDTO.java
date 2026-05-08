package com.optativaGS.deportesUGR.dto;

import com.optativaGS.deportesUGR.modelos.TipoBono;

public record BonoDTO(
        Long id,
        int maxUsos,
        TipoBono tipo,
        Long usuarioId,
        int usosConsumidos
) {}