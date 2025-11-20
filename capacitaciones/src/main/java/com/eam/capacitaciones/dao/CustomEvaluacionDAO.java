package com.eam.capacitaciones.dao;

import java.util.List;
import java.util.Map;

public interface CustomEvaluacionDAO {
    
    Map<Long, Double> obtenerPromedioCalificacionesPorEvaluacion();

    List<Map<String, Object>> obtenerEvaluacionesConRespuestasPendientes(Long instructorId);

    List<Map<String, Object>> obtenerEstadisticasAprobacion(Long cursoId, Integer puntajeMinimo);
    
    List<Map<String, Object>> obtenerEvaluacionesMasDificiles(int limit);
}