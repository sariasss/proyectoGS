package com.optativaGS.deportesUGR.config;

import com.optativaGS.deportesUGR.modelos.*;
import com.optativaGS.deportesUGR.respositorios.*;
import com.optativaGS.deportesUGR.servicios.ClaseService;
import com.optativaGS.deportesUGR.servicios.UsuarioService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class DataInitializer implements CommandLineRunner {
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private ClaseRepository claseRepository;
    @Autowired private BonoRepository bonoRepository;
    @Autowired private ClaseService claseService;
    @Autowired private UsuarioService usuarioService;

    @Override
    @Transactional
    public void run(String... args) throws Exception {

        if (usuarioRepository.findByEmail("admin@ugr.es") == null) {
            Usuario admin = new Usuario();
            admin.setNombre("Administrador");
            admin.setEmail("admin@ugr.es");
            admin.setPassword("1234");
            admin.setRol(RolUsuario.ADMIN);
            admin.setTelefono("958000000");
            admin.setBonos(new ArrayList<>());
            admin.setClasesTipo3(new ArrayList<>());
            usuarioRepository.save(admin);
        }

        String[] nombresE = {"Ricardo Molina", "Beatriz Ruiz", "Fernando Ortega", "Patricia Castillo", "Hugo Cano", "Irene Marín", "Alberto Sanz", "Marta Nieto"};
        Random random = new Random();

        int indexNombre = 0;
        for (Especialidad esp : Especialidad.values()) {
            String email = "profe." + esp.name().toLowerCase() + "@ugr.es";
            if (usuarioRepository.findByEmail(email) == null) {
                Usuario profe = new Usuario();
                profe.setNombre(nombresE[indexNombre % nombresE.length]);
                profe.setEmail(email);
                profe.setPassword("1234");
                profe.setRol(RolUsuario.ENTRENADOR);
                profe.setEspecialidad(esp);
                profe.setTelefono("600" + (100000 + random.nextInt(900000)));
                profe.setBonos(new ArrayList<>());
                profe.setClasesTipo3(new ArrayList<>());
                usuarioRepository.save(profe);

                claseService.generarCalendarioAnual(profe);
                indexNombre++;
            }
        }

        String[] nombresU = {"Pepe Garcia", "Maria Lopez", "Juan Rodriguez"};

        for (int i = 0; i < nombresU.length; i++) {
            String email = "user" + (i + 1) + "@correo.com";
            Usuario user = usuarioRepository.findByEmail(email);

            if (user == null) {
                user = new Usuario();
                user.setNombre(nombresU[i]);
                user.setEmail(email);
                user.setPassword("1234");
                user.setRol(RolUsuario.USUARIO);
                user.setTelefono("61122334" + i);
                user.setBonos(new ArrayList<>());
                user.setClasesTipo3(new ArrayList<>());
                usuarioRepository.save(user);

                try {
                    List<Clase> todas = claseRepository.findAll();
                    if (!todas.isEmpty()) {
                        Clase claseDestino = todas.get(random.nextInt(todas.size()));

                        Bono bonoInaugural = new Bono();
                        bonoInaugural.setUsuario(user);
                        bonoInaugural.setEspecialidad(claseDestino.getEspecialidad());
                        bonoInaugural.setMax_usos(10);
                        bonoInaugural.setTipo(TipoBono.TIPO1);
                        bonoInaugural.setUsos(new ArrayList<>());
                        bonoRepository.save(bonoInaugural);

                        user.getBonos().add(bonoInaugural);

                        usuarioService.inscribirUsuarioEnClase(user.getId(), claseDestino.getId());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}