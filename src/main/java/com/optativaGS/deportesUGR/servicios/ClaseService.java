package com.optativaGS.deportesUGR.servicios;

import com.optativaGS.deportesUGR.modelos.*;
import com.optativaGS.deportesUGR.respositorios.ClaseRepository;
import com.optativaGS.deportesUGR.respositorios.UsoBonoRepository;
import com.optativaGS.deportesUGR.respositorios.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ClaseService {
    private final ClaseRepository claseRepository;
    private final UsuarioRepository usuarioRepository;
    private final UsoBonoRepository usoBonoRepository;

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

    public Clase create(Map<String, Object> body) {
        String tipo = (String) body.get("tipoClase");
        Clase clase;

        switch (tipo) {

            case "TIPO1" -> {
                ClaseTipo1 c = new ClaseTipo1();
                c.setDiaSemana((String) body.get("diaSemana"));
                c.setHora(LocalTime.parse((String) body.get("hora")));
                clase = c;
            }
            case "TIPO2" -> {
                clase = new ClaseTipo2();
            }
            case "TIPO3" -> {
                ClaseTipo3 c = new ClaseTipo3();
                c.setCupoMax(Integer.parseInt(body.get("cupoMax").toString()));
                clase = c;
            }
            default -> throw new IllegalArgumentException("Tipo de clase inválido");
        }

        clase.setFecha(LocalDateTime.parse((String) body.get("fecha")));
        clase.setDuracionMinutos((Integer) body.get("duracionMinutos"));
        clase.setEspecialidad(Especialidad.valueOf((String) body.get("especialidad")));

        Long entrenadorId = body.get("entrenadorId") != null
                ? Long.valueOf(body.get("entrenadorId").toString())
                : null;

        if (entrenadorId != null) {
            Usuario entrenador = usuarioRepository.findById(entrenadorId)
                    .orElseThrow();
            clase.setEntrenador(entrenador);
        }

        return claseRepository.save(clase);
    }

    @Transactional
    public void generarCalendarioAnual(Usuario entrenador) {
        Especialidad esp = entrenador.getEspecialidad();
        if (esp == null) return;

        LocalDate inicio = LocalDate.of(2026, 1, 1);
        LocalDate fin = LocalDate.of(2026, 12, 31);

        LocalDate dia = inicio;
        while (!dia.isAfter(fin)) {

            if (esFechaLaborable(dia)) {
                DayOfWeek diaSemana = dia.getDayOfWeek();

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

    public List<ClaseTipo3> findAllTipo3() {
        List<ClaseTipo3> lista = claseRepository.findAllTipo3();
        return lista;
    }

    private boolean esFechaLaborable(LocalDate fecha) {
        int mes = fecha.getMonthValue();
        int dia = fecha.getDayOfMonth();

        if (mes == 8) return false;

        if ((mes == 12 && dia >= 22) || (mes == 1 && dia <= 7)) return false;

        if ((mes == 3 && dia >= 29) || (mes == 4 && dia <= 5)) return false;

        return true;
    }

    public List<UsoBono> findSolicitudesPendientes() {
        return usoBonoRepository.findAll().stream()
                .filter(u -> u.getEstadoSolicitud() == EstadoSolicitud.PENDIENTE)
                .toList();
    }
}
