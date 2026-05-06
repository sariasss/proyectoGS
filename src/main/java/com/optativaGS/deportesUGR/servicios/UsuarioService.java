package com.optativaGS.deportesUGR.servicios;

import com.optativaGS.deportesUGR.modelos.Usuario;
import com.optativaGS.deportesUGR.respositorios.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;

    public List<Usuario> findAll(){
        return usuarioRepository.findAll();
    }

    public Usuario findById(Long id){
        return usuarioRepository.findById(id).orElse(null);
    }

    public void delete(Long id){
        usuarioRepository.deleteById(id);
    }

    public void save(Usuario usuario){
        usuarioRepository.save(usuario);
    }
}
