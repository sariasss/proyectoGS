package com.optativaGS.deportesUGR.respositorios;

import com.optativaGS.deportesUGR.modelos.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    //SELECT * FROM usuario WHERE email=email_x
    Usuario findByEmail(String email);
}
