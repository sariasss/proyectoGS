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
@EqualsAndHashCode
public class Bono {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int max_usos;

    @Enumerated(value = EnumType.STRING)
    private TipoBono tipo;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @OneToMany(mappedBy = "bono", cascade = CascadeType.ALL)
    private List<UsoBono> usos;
}
