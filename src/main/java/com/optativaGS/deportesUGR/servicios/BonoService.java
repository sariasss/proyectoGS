package com.optativaGS.deportesUGR.servicios;

import com.optativaGS.deportesUGR.modelos.*;
import com.optativaGS.deportesUGR.respositorios.BonoRepository;
import com.optativaGS.deportesUGR.respositorios.ClaseRepository;
import com.optativaGS.deportesUGR.respositorios.UsoBonoRepository;
import com.optativaGS.deportesUGR.respositorios.UsuarioRepository;
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
public class BonoService {
    private final BonoRepository bonoRepository;
    private final UsoBonoRepository usoBonoRepository;
    private final ClaseRepository claseRepository;
    private final UsuarioRepository usuarioRepository;

    public List<Bono> findAll(){
        return bonoRepository.findAll();
    }

    public Bono findById(Long id){
        return bonoRepository.findById(id).orElse(null);
    }

    public void delete(Long id){
        bonoRepository.deleteById(id);
    }

    public void save(Bono bono){
        bonoRepository.save(bono);
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
}
