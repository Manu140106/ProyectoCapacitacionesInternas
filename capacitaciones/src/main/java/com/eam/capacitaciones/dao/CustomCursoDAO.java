package com.eam.capacitaciones.dao;

import com.eam.capacitaciones.domain.entity.Curso;
import java.util.List;
import java.util.Map;

public interface CustomCursoDAO {
    
    List<Curso> buscarCursosConFiltros(String titulo, String nivel, 
                                        Long instructorId, Boolean activo, 
                                        Integer minInscritos);
    
    List<Map<String, Object>> obtenerCursosMasPopulares(int limit);

    List<Curso> obtenerCursosRecomendados(Long usuarioId, int limit);

    Map<String, Long> obtenerEstadisticasPorNivel();

    List<Map<String, Object>> obtenerCursosConBajaCompletacion(double umbralPorcentaje);
}