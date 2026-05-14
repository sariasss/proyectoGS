package com.optativaGS.deportesUGR.servicios;

import com.optativaGS.deportesUGR.dto.UsuarioDTO;
import com.optativaGS.deportesUGR.modelos.*;
import com.optativaGS.deportesUGR.respositorios.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final BonoRepository bonoRepository;
    private final UsoBonoRepository usoBonoRepository;
    private final ClaseRepository claseRepository;
    private final ClaseTipo3UsuarioRepository claseTipo3UsuarioRepository;

    public List<UsuarioDTO> findAll(){
        return usuarioRepository.findAll().stream()
                .map(this::mapToDTO)
                .toList();
    }

    public UsuarioDTO findById(Long id){
        return usuarioRepository.findById(id)
                .map(this::mapToDTO)
                .orElse(null);
    }

    public UsuarioDTO login(String email, String password){
        Usuario usuario = usuarioRepository.findByEmail(email);

        if(usuario != null && usuario.getPassword().equals(password)){
            return mapToDTO(usuario);
        }
        return null;
    }

    public void delete(Long id){
        usuarioRepository.deleteById(id);
    }

    public Usuario save(UsuarioDTO dto){
        Usuario usuario = new Usuario();

        if (dto.id() != null) {
            usuario = usuarioRepository.findById(dto.id())
                    .orElse(new Usuario());
        } else {
            usuario = new Usuario();
        }

        usuario.setNombre(dto.nombre());
        usuario.setEmail(dto.email());
        usuario.setTelefono(dto.telefono());
        usuario.setRol(dto.rol());

        if (dto.password() != null && !dto.password().isEmpty()) {
            usuario.setPassword(dto.password());
        }

        if (dto.rol() == RolUsuario.ENTRENADOR) {
            usuario.setEspecialidad(dto.especialidad());
        } else {
            usuario.setEspecialidad(null);
        }

        usuarioRepository.save(usuario);
        return usuario;
    }

    public List<Usuario> findEntrenadores() {
        return usuarioRepository.findByRol(RolUsuario.ENTRENADOR);
    }

    //Convierte el usuario de la base de datos a DTO
    private UsuarioDTO mapToDTO(Usuario usuario) {
        return new UsuarioDTO(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getTelefono(),
                null,
                usuario.getRol(),
                usuario.getEspecialidad()
        );
    }

    public Usuario findEntityById(Long id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    public void comprarBono(Long usuarioId, TipoBono tipo) {
        Usuario u = usuarioRepository.findById(usuarioId).orElse(null);
        if (u != null) {
            Bono nuevoBono = new Bono();
            nuevoBono.setUsuario(u);
            nuevoBono.setTipo(tipo);

            if (tipo == TipoBono.TIPO1) {
                nuevoBono.setMax_usos(10);
            } else {
                nuevoBono.setMax_usos(5);
            }

            bonoRepository.save(nuevoBono);
        }
    }

    public void inscribirUsuarioEnClase(Long usuarioId, Long claseId) {
        Usuario usuario = usuarioRepository.findById(usuarioId).orElseThrow();
        Clase clase = claseRepository.findById(claseId).orElseThrow();

        Bono bonoValido = usuario.getBonos().stream()
                .filter(b -> b.getEspecialidad().equals(clase.getEspecialidad()))
                .filter(b -> b.getUsos().size() < b.getMax_usos())
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No tienes bonos con sesiones disponibles para esta especialidad"));

        UsoBono uso = new UsoBono();
        uso.setFecha(clase.getFecha().toLocalDate());
        uso.setBono(bonoValido);

        usoBonoRepository.save(uso);
    }

    public void comprarBonoConEspecialidad(Long usuarioId, TipoBono tipo, Especialidad especialidad) {
        Usuario u = usuarioRepository.findById(usuarioId).orElse(null);
        if (u != null) {
            List<LocalDate> fechasOcupadas = u.getBonos().stream()
                    .filter(b -> b.getEspecialidad().equals(especialidad))
                    .flatMap(b -> b.getUsos().stream())
                    .map(UsoBono::getFecha)
                    .toList();

            LocalDateTime fechaUltimaSesion = fechasOcupadas.stream()
                    .map(LocalDate::atStartOfDay)
                    .max(Comparator.naturalOrder())
                    .orElse(LocalDateTime.now());

            Bono nuevoBono = new Bono();
            nuevoBono.setUsuario(u);
            nuevoBono.setTipo(tipo);
            nuevoBono.setEspecialidad(especialidad);
            int maxUsos = (tipo == TipoBono.TIPO1) ? 10 : 5;
            nuevoBono.setMax_usos(maxUsos);
            Bono bonoGuardado = bonoRepository.save(nuevoBono);

            List<Clase> clasesAsignables = claseRepository.findAll().stream()
                    .filter(c -> c.getEspecialidad().equals(especialidad))
                    .filter(c -> c.getFecha().isAfter(fechaUltimaSesion))
                    .filter(c -> !fechasOcupadas.contains(c.getFecha().toLocalDate()))
                    .sorted(Comparator.comparing(Clase::getFecha))
                    .limit(maxUsos)
                    .toList();

            for (Clase clase : clasesAsignables) {
                UsoBono usoAutomatico = new UsoBono();
                usoAutomatico.setBono(bonoGuardado);
                usoAutomatico.setFecha(clase.getFecha().toLocalDate());
                usoBonoRepository.save(usoAutomatico);
            }
        }
    }

    //Borra el uso actual y busca la siguiente clase disponible
    @Transactional
    public void cancelarYReasignar(Long usoId) {
        UsoBono usoACancelar = usoBonoRepository.findById(usoId).orElse(null);
        if (usoACancelar == null) return;

        Bono bono = usoACancelar.getBono();
        Usuario usuario = bono.getUsuario();
        Especialidad especialidad = bono.getEspecialidad();

        List<LocalDate> fechasOcupadasActualmente = usuario.getBonos().stream()
                .filter(b -> b.getEspecialidad().equals(especialidad))
                .flatMap(b -> b.getUsos().stream())
                .map(UsoBono::getFecha)
                .collect(Collectors.toList());

        LocalDate fechaUltima = fechasOcupadasActualmente.stream()
                .max(Comparator.naturalOrder())
                .orElse(LocalDate.now());

        usoBonoRepository.delete(usoACancelar);

        bono.getUsos().remove(usoACancelar);

        Clase siguienteClase = claseRepository.findAll().stream()
                .filter(c -> c.getEspecialidad().equals(especialidad))
                .filter(c -> !(c instanceof ClaseTipo3))
                .filter(c -> c.getFecha().toLocalDate().isAfter(fechaUltima))
                .filter(c -> !fechasOcupadasActualmente.contains(c.getFecha().toLocalDate()))
                .sorted(Comparator.comparing(Clase::getFecha))
                .findFirst()
                .orElse(null);

        if (siguienteClase != null) {
            UsoBono nuevoUso = new UsoBono();
            nuevoUso.setBono(bono);
            nuevoUso.setFecha(siguienteClase.getFecha().toLocalDate());
            usoBonoRepository.save(nuevoUso);
        }
    }

    //Inscripción en clases tipo 3
    @Transactional
    public void inscribirEnClaseEspecial(Long usuarioId, Long claseId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Clase claseBase = claseRepository.findById(claseId)
                .orElseThrow(() -> new RuntimeException("Clase no encontrada"));

        if (usuario.getBonos() == null || usuario.getBonos().isEmpty()) {
            throw new RuntimeException("Se requiere un bono activo para acceder.");
        }

        ClaseTipo3Usuario inscripcion = new ClaseTipo3Usuario();
        inscripcion.setUsuario(usuario);
        inscripcion.setClase((ClaseTipo3) claseBase);

        claseTipo3UsuarioRepository.save(inscripcion);
    }

    //Solicita cambiar una clase del tipo 2 al entrenador que la imparta
    public void solicitarCambio(Long usoId) {
        UsoBono uso = usoBonoRepository.findById(usoId).orElseThrow();
    }
}
