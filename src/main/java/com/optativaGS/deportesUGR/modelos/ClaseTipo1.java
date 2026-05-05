package com.optativaGS.deportesUGR.modelos;

import jakarta.persistence.Entity;
import lombok.*;

import java.time.LocalTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ClaseTipo1 extends Clase{
    private String diaSemana;
    private LocalTime hora;
}
