package com.example.plataforma_capacitacion.repository;

import com.example.plataforma_capacitacion.model.Respuesta;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface RespuestaRepository extends JpaRepository<Respuesta, Long> {
    List<Respuesta> findByEvaluacionId(Long evaluacionId);
}