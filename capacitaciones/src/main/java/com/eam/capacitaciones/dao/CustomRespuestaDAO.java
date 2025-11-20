package com.eam.capacitaciones.dao;

import java.util.List;
import java.util.Map;

public interface CustomRespuestaDAO {

    List<Map<String, Object>> obtenerRespuestasDeUsuarioEnCurso(Integer usuarioId, Integer cursoId);

    Double obtenerPromedioPorCurso(Integer usuarioId, Integer cursoId);

    boolean usuarioHaPresentadoEvaluacion(Integer usuarioId, Integer evaluacionId);
}