package com.optativaGS.deportesUGR.respositorios;

import com.optativaGS.deportesUGR.modelos.Clase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ClaseRepository extends JpaRepository<Clase, Long> {
    //SELECT * FROM clase WHERE id= id_x
    List<Clase> findByEntrenadorId(Long id);

    @Query("SELECT c FROM Clase c WHERE c.entrenador.id = :id AND c.fecha >= CURRENT_TIMESTAMP ORDER BY c.fecha ASC")
    List<Clase> findProximasByEntrenadorId(Long id);
}
