package com.optativaGS.deportesUGR.servicios;

import com.optativaGS.deportesUGR.modelos.EstadoSolicitud;
import com.optativaGS.deportesUGR.modelos.UsoBono;
import com.optativaGS.deportesUGR.respositorios.UsoBonoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class UsoBonoService {
    private final UsoBonoRepository usoBonoRepository;

    //Solicita cambiar una clase del tipo 2 al entrenador que la imparta
    public void solicitarCambio(Long usoId, LocalDate fecha) {
        UsoBono uso = usoBonoRepository.findById(usoId)
                .orElseThrow(() -> new RuntimeException("No se encontró el uso de bono con ID: " + usoId));

        uso.setFechaPropuesta(fecha);
        uso.setEstadoSolicitud(EstadoSolicitud.PENDIENTE);

        usoBonoRepository.save(uso);
    }

    public void gestionarRespuestaCambio(Long usoId, boolean aceptado) {
        UsoBono uso = usoBonoRepository.findById(usoId).orElseThrow();
        if (aceptado && uso.getFechaPropuesta() != null) {
            uso.setFecha(uso.getFechaPropuesta());
            uso.setEstadoSolicitud(EstadoSolicitud.ACEPTADA);
        } else {
            uso.setEstadoSolicitud(EstadoSolicitud.RECHAZADA);
        }
        uso.setFechaPropuesta(null);
        usoBonoRepository.save(uso);
    }

    public void aceptarCambioFecha(Long usoId) {
        UsoBono uso = usoBonoRepository.findById(usoId)
                .orElseThrow(() -> new RuntimeException("Uso de bono no encontrado"));

        if (uso.getFechaPropuesta() != null) {
            // La fecha oficial de la clase ahora es la propuesta por el usuario
            uso.setFecha(uso.getFechaPropuesta());
            // Limpiamos la propuesta y marcamos como aceptada
            uso.setFechaPropuesta(null);
            uso.setEstadoSolicitud(EstadoSolicitud.ACEPTADA);

            usoBonoRepository.save(uso);
        }
    }

    public void rechazarCambioFecha(Long usoId) {
        UsoBono uso = usoBonoRepository.findById(usoId)
                .orElseThrow(() -> new RuntimeException("Uso de bono no encontrado"));

        // Eliminamos la propuesta sin cambiar la fecha original
        uso.setFechaPropuesta(null);
        uso.setEstadoSolicitud(EstadoSolicitud.RECHAZADA);

        usoBonoRepository.save(uso);
    }
}
