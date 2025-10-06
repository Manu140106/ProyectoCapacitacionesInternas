package com.example.plataforma_capacitacion.repository;

import com.example.plataforma_capacitacion.model.Evaluacion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EvaluacionRepository extends JpaRepository<Evaluacion, Long> {
    List<Evaluacion> findByCursoId(Long cursoId);
}