package com.optativaGS.deportesUGR.respositorios;

import com.optativaGS.deportesUGR.modelos.RolUsuario;
import com.optativaGS.deportesUGR.modelos.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    //SELECT * FROM usuario WHERE email=email_x
    Usuario findByEmail(String email);

    //SELECT * FROM usuario WHERE rol= rol_x
    List<Usuario> findByRol(RolUsuario rol);
}
