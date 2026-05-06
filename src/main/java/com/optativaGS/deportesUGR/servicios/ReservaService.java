package com.optativaGS.deportesUGR.servicios;

import com.optativaGS.deportesUGR.modelos.Reserva;
import com.optativaGS.deportesUGR.respositorios.ReservaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservaService {
    private final ReservaRepository reservaRepository;

    public List<Reserva> findAll(){
        return reservaRepository.findAll();
    }

    public Reserva findById(Long id){
        return reservaRepository.findById(id).orElse(null);
    }

    public void delete(Long id){
        reservaRepository.deleteById(id);
    }

    public void save(Reserva reserva){
        reservaRepository.save(reserva);
    }
}
