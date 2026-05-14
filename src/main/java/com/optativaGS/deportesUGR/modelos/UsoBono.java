package com.optativaGS.deportesUGR.modelos;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class UsoBono {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate fecha;

    private LocalDate fechaPropuesta;

    @Enumerated(EnumType.STRING)
    private EstadoSolicitud estadoSolicitud = EstadoSolicitud.SIN_SOLICITUD;

    @ManyToOne
    @JoinColumn(name = "bono_id")
    private Bono bono;
}
