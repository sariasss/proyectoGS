package com.optativaGS.deportesUGR.config;

import com.optativaGS.deportesUGR.modelos.RolUsuario;
import com.optativaGS.deportesUGR.modelos.Usuario;
import com.optativaGS.deportesUGR.respositorios.ClaseRepository;
import com.optativaGS.deportesUGR.respositorios.UsuarioRepository;
import com.optativaGS.deportesUGR.servicios.ClaseService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {
    @Autowired private ClaseRepository claseRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private ClaseService claseService;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (claseRepository.count() == 0) {
            List<Usuario> entrenadores = usuarioRepository.findByRol(RolUsuario.ENTRENADOR);
            for (Usuario e : entrenadores) {
                claseService.generarCalendarioAnual(e);
            }
        }
    }
}
