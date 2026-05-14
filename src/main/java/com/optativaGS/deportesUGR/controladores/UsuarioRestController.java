package com.optativaGS.deportesUGR.controladores;

import com.optativaGS.deportesUGR.dto.UsuarioDTO;
import com.optativaGS.deportesUGR.modelos.Usuario;
import com.optativaGS.deportesUGR.servicios.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioRestController {

    private final UsuarioService usuarioService;

    @GetMapping
    public List<UsuarioDTO> findAll() {
        return usuarioService.findAll();
    }

    @GetMapping("/{id}")
    public UsuarioDTO findById(@PathVariable Long id) {
        return usuarioService.findById(id);
    }

    @PostMapping
    public UsuarioDTO create(@RequestBody UsuarioDTO dto) {
        Usuario usuario = usuarioService.save(dto);
        return usuarioService.findById(usuario.getId());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        usuarioService.delete(id);
    }
}