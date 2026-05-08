package com.optativaGS.deportesUGR.servicios;

import com.optativaGS.deportesUGR.dto.ReservaDTO;
import com.optativaGS.deportesUGR.modelos.Reserva;
import com.optativaGS.deportesUGR.respositorios.ReservaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservaService {
    private final ReservaRepository reservaRepository;

    public List<ReservaDTO> findAll(){
        return reservaRepository.findAll().stream()
                .map(this::mapToDTO)
                .toList();
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

    //Convierte la reserva de la base de datos a DTO
    private ReservaDTO mapToDTO(Reserva reserva) {
        return new ReservaDTO(
                reserva.getId(),
                reserva.getEstado().name(),
                reserva.getUsuario().getNombre(),
                reserva.getClase().getId(),
                reserva.getFecha()
        );
    }
}
