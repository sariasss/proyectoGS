package com.optativaGS.deportesUGR.modelos;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@ToString
public class ClaseTipo3 extends Clase{
    private int cupoMax;

    @OneToMany(mappedBy = "clase")
    private List<ClaseTipo3Usuario> inscripciones;

    public ClaseTipo3() {
        super();
        this.setDuracionMinutos(60);
    }

    public ClaseTipo3(int cupoMax, List<ClaseTipo3Usuario> inscripciones) {
        super();
        this.setDuracionMinutos(60);
        this.cupoMax = cupoMax;
        this.inscripciones = inscripciones;
    }
}
