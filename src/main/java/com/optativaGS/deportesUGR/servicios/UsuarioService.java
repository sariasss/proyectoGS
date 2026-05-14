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

    public void delete(Long id) {
        List<Clase> clasesAsociadas = claseRepository.findByEntrenadorId(id);
        claseRepository.deleteAll(clasesAsociadas);
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

    public List<Usuario> findByRolAndEspecialidad(RolUsuario rol, Especialidad especialidad) {
        return usuarioRepository.findByRolAndEspecialidad(rol, especialidad);
    }
}
