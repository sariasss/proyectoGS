package com.optativaGS.deportesUGR.dto;

import com.optativaGS.deportesUGR.modelos.Especialidad;
import com.optativaGS.deportesUGR.modelos.RolUsuario;

public record UsuarioDTO(
        Long id,
        String nombre,
        String email,
        String telefono,
        String password,
        RolUsuario rol,
        Especialidad especialidad
) {}
