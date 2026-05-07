package com.optativaGS.deportesUGR.servicios;

import com.optativaGS.deportesUGR.modelos.Bono;
import com.optativaGS.deportesUGR.respositorios.BonoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BonoService {
    private final BonoRepository bonoRepository;

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
}
