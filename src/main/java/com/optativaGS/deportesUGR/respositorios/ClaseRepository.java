package com.optativaGS.deportesUGR.respositorios;

import com.optativaGS.deportesUGR.modelos.Clase;
import com.optativaGS.deportesUGR.modelos.ClaseTipo1;
import com.optativaGS.deportesUGR.modelos.ClaseTipo2;
import com.optativaGS.deportesUGR.modelos.ClaseTipo3;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ClaseRepository extends JpaRepository<Clase, Long> {
    //SELECT * FROM clase WHERE id= id_x
    List<Clase> findByEntrenadorId(Long id);

    @Query("SELECT c FROM Clase c WHERE c.entrenador.id = :id AND c.fecha >= CURRENT_TIMESTAMP ORDER BY c.fecha ASC")
    List<Clase> findProximasByEntrenadorId(Long id);

    @Query("SELECT c FROM ClaseTipo3 c")
    List<ClaseTipo3> findAllTipo3();

    //filtrar solo clases de Tipo 2 para un Entrenador
    @Query("SELECT TREAT(c AS ClaseTipo1) FROM Clase c WHERE c.entrenador.id = :id")
    List<ClaseTipo1> findTipo1ByEntrenadorId( Long id);

    //filtrar solo clases de Tipo 2 para un Entrenador
    @Query("SELECT TREAT(c AS ClaseTipo2) FROM Clase c WHERE c.entrenador.id = :id")
    List<ClaseTipo2> findTipo2ByEntrenadorId(Long id);

    // filtrar solo clases de Tipo 3 para un Entrenador
    @Query("SELECT TREAT(c AS ClaseTipo3) FROM Clase c WHERE c.entrenador.id = :id")
    List<ClaseTipo3> findTipo3ByEntrenadorId(Long id);
}
