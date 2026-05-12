package com.optativaGS.deportesUGR.servicios;

import com.optativaGS.deportesUGR.modelos.Clase;
import com.optativaGS.deportesUGR.modelos.ClaseTipo1;
import com.optativaGS.deportesUGR.modelos.Especialidad;
import com.optativaGS.deportesUGR.modelos.Usuario;
import com.optativaGS.deportesUGR.respositorios.ClaseRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
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

    public List<Clase> findByEntrenadorId(Long id) {
        return claseRepository.findByEntrenadorId(id);
    }

    @Transactional
    public void generarCalendarioAnual(Usuario entrenador) {
        Especialidad esp = entrenador.getEspecialidad();
        if (esp == null) return;

        LocalDate inicio = LocalDate.of(2026, 1, 1);
        LocalDate fin = LocalDate.of(2026, 12, 31);

        LocalDate dia = inicio;
        while (!dia.isAfter(fin)) {
            DayOfWeek diaSemana = dia.getDayOfWeek();

            //Lógica de Sábados de Partido
            LocalDate primerSabado = dia.with(TemporalAdjusters.firstInMonth(DayOfWeek.SATURDAY));
            LocalDate segundoSabado = primerSabado.plusWeeks(1);

            boolean esHoyPartido = (esp == Especialidad.FUTBOL && dia.equals(primerSabado)) ||
                    (esp == Especialidad.BALONCESTO && dia.equals(segundoSabado));

            if (esDiaDeClase(esp, diaSemana) || esHoyPartido) {
                ClaseTipo1 c1 = new ClaseTipo1();
                c1.setFecha(dia.atTime(obtenerHoraEspecialidad(esp)));
                c1.setEspecialidad(esp);
                c1.setEntrenador(entrenador);

                c1.setDuracionMinutos(obtenerDuracion(esp, esHoyPartido));

                claseRepository.save(c1);
            }
            dia = dia.plusDays(1);
        }
    }

    //Lógica para decidir qué días hay cada cosa
    private boolean esDiaDeClase(Especialidad esp, DayOfWeek dia) {
        return switch (esp) {
            case FUTBOL -> (dia == DayOfWeek.MONDAY || dia == DayOfWeek.WEDNESDAY);
            case BALONCESTO -> (dia == DayOfWeek.TUESDAY || dia == DayOfWeek.THURSDAY);
            case TENIS -> (dia == DayOfWeek.MONDAY || dia == DayOfWeek.THURSDAY);
            case VOLEY -> (dia == DayOfWeek.TUESDAY || dia == DayOfWeek.FRIDAY);
            case YOGA -> (dia == DayOfWeek.MONDAY || dia == DayOfWeek.FRIDAY);
            case FLAMENCO -> (dia == DayOfWeek.TUESDAY || dia == DayOfWeek.WEDNESDAY);
            case BALLET -> (dia == DayOfWeek.WEDNESDAY || dia == DayOfWeek.FRIDAY);
            case BADMINTON -> (dia == DayOfWeek.WEDNESDAY || dia == DayOfWeek.THURSDAY);
            default -> false;
        };
    }

    //Lógica para decidir a que hora es cada clase
    private LocalTime obtenerHoraEspecialidad(Especialidad esp) {
        return switch (esp) {
            case FUTBOL -> LocalTime.of(17, 0);
            case BALONCESTO -> LocalTime.of(18, 0);
            case TENIS -> LocalTime.of(19, 15);
            case VOLEY -> LocalTime.of(17, 20);
            case YOGA -> LocalTime.of(16, 30);
            case FLAMENCO -> LocalTime.of(19, 30);
            case BALLET -> LocalTime.of(18, 15);
            case BADMINTON -> LocalTime.of(20, 15);
            default -> null;
        };
    }

    private Integer obtenerDuracion(Especialidad esp, boolean esPartido) {
        if (esPartido) {
            return 120;
        }

        return switch (esp) {
            case TENIS, BADMINTON -> 45;
            default -> 60;
        };
    }
}
