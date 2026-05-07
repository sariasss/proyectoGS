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
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String email;
    private String telefono;
    private String password;

    @Enumerated(value = EnumType.STRING)
    private String rol;

    @OneToMany(mappedBy = "usuario")
    private List<Bono> bonos;

    @OneToMany(mappedBy = "usuario")
    private List<ClaseTipo3Usuario> clasesTipo3;
}
