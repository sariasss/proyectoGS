package com.optativaGS.deportesUGR.modelos;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ClaseTipo3UsuarioId implements Serializable {
    private Long clase;
    private Long usuario;
}
