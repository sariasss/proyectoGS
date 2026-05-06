package com.optativaGS.deportesUGR.servicios;

import com.optativaGS.deportesUGR.modelos.Clase;
import com.optativaGS.deportesUGR.respositorios.ClaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClaseService {
    private final ClaseRepository claseRepository;

    public List<Clase> findAll(){
        return claseRepository.findAll();
    }

    public Clase findById(Long id){
        return claseRepository.findById(id).orElse(null);
    }

    public void delete(Long id){
        claseRepository.deleteById(id);
    }

    public void save(Clase clase){
        claseRepository.save(clase);
    }
}
