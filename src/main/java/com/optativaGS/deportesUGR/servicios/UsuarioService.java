package com.optativaGS.deportesUGR.servicios;

import com.optativaGS.deportesUGR.dto.UsuarioDTO;
import com.optativaGS.deportesUGR.modelos.RolUsuario;
import com.optativaGS.deportesUGR.modelos.Usuario;
import com.optativaGS.deportesUGR.respositorios.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;

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

}
