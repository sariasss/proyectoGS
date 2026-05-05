package com.optativaGS.deportesUGR.modelos;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Table(name = "clase_tipo3_usuario")
public class ClaseTipo3Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "clase_id")
    private ClaseTipo3 clase;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
}
