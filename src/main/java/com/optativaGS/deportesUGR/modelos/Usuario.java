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
    private Especialidad especialidad;

    @Enumerated(value = EnumType.STRING)
    private RolUsuario rol;

    @OneToMany(mappedBy = "usuario")
    private List<Bono> bonos;

    @OneToMany(mappedBy = "usuario")
    private List<ClaseTipo3Usuario> clasesTipo3;

    public Usuario(Long id,
                   String nombre,
                   String email,
                   String telefono,
                   String password,
                   RolUsuario rol,
                   List<Bono> bonos,
                   List<ClaseTipo3Usuario> clasesTipo3) {

        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
        this.password = password;
        this.rol = rol;
        this.bonos = bonos;
        this.clasesTipo3 = clasesTipo3;
    }
}
