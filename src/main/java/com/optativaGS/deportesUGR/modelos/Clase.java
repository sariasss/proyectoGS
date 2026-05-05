package com.optativaGS.deportesUGR.modelos;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Clase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate fecha;

    @ManyToOne
    @JoinColumn(name = "entrenador_id")
    private Entrenador entrenador;

    @OneToMany(mappedBy = "clase", cascade = CascadeType.ALL)
    private List<Reserva> reservas;

}
