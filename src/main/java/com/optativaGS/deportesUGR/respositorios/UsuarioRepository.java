package com.optativaGS.deportesUGR.respositorios;

import com.optativaGS.deportesUGR.modelos.Especialidad;
import com.optativaGS.deportesUGR.modelos.RolUsuario;
import com.optativaGS.deportesUGR.modelos.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    //SELECT * FROM usuario WHERE email=email_x
    Usuario findByEmail(String email);

    //SELECT * FROM usuario WHERE rol= rol_x
    List<Usuario> findByRol(RolUsuario rol);

    // Spring genera: SELECT * FROM usuario WHERE rol = ? AND especialidad = ?
    List<Usuario> findByRolAndEspecialidad(RolUsuario rol, Especialidad especialidad);
}
