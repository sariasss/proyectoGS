package com.optativaGS.deportesUGR.modelos;


import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Table(name = "clase_tipo3_usuario")
public class ClaseTipo3Usuario implements Serializable {
    @Id
    @ManyToOne
    @JoinColumn(name = "clase_id")
    private ClaseTipo3 clase;

    @Id
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
}
