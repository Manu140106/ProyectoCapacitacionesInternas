package com.eam.capacitaciones.dao;

import java.util.List;
import java.util.Map;

public interface CustomModuloDAO {

    List<Map<String, Object>> obtenerModulosPorCurso(Integer cursoId);

    Map<String, Object> obtenerSiguienteModulo(Integer cursoId, Integer ordenActual);

    Map<String, Object> obtenerUltimoModuloCompletado(Integer usuarioId, Integer cursoId);
}