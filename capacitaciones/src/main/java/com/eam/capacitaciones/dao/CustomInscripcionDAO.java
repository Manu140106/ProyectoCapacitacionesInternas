package com.eam.capacitaciones.dao;

import com.eam.capacitaciones.domain.entity.Inscripcion;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface CustomInscripcionDAO {
    
    List<Map<String, Object>> obtenerReporteInscripcionesPorFecha(
        LocalDate fechaInicio, LocalDate fechaFin);
    
    List<Inscripcion> obtenerInscripcionesPorRangoProgreso(
        Double progresoMin, Double progresoMax);
    
    List<Map<String, Object>> obtenerCursosAbandonadosPorUsuario(Long usuarioId);
    
    Map<Long, Double> calcularTasaCompletacionPorCurso();

    List<Map<String, Object>> obtenerUsuariosCercaDeCompletar(Long cursoId);
}