package com.optativaGS.deportesUGR.modelos;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ClaseTipo3 extends Clase{
    private int cupoMax;

    @OneToMany(mappedBy = "clase")
    private List<ClaseTipo3Usuario> inscripciones;

    private List<Usuario> usuarios;
}
