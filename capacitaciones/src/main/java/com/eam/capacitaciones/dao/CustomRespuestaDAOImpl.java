package com.eam.capacitaciones.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
public class CustomRespuestaDAOImpl implements CustomRespuestaDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Map<String, Object>> obtenerRespuestasDeUsuarioEnCurso(Integer usuarioId, Integer cursoId) {
        String sql = """
            SELECT r.idRespuesta, r.puntuacion, r.fecha,
                   e.titulo, m.titulo AS moduloTitulo
            FROM Respuesta r
            JOIN Evaluacion e ON r.evaluacionId = e.idEvaluacion
            JOIN Modulo m ON e.moduloId = m.idModulo
            WHERE r.usuarioId = :usuario
            AND m.cursoId = :curso
        """;

        @SuppressWarnings("unchecked")
        List<Object[]> results = (List<Object[]>) entityManager
                .createNativeQuery(sql)
                .setParameter("usuario", usuarioId)
                .setParameter("curso", cursoId)
                .getResultList();

        List<Map<String, Object>> respuestas = new ArrayList<>();

        for (Object[] row : results) {
            Map<String, Object> map = new HashMap<>();
            map.put("idRespuesta", row[0]);
            map.put("puntuacion", row[1]);
            map.put("fecha", row[2]);
            map.put("evaluacionTitulo", row[3]);
            map.put("moduloTitulo", row[4]);
            respuestas.add(map);
        }

        return respuestas;
    }

    @Override
    public Double obtenerPromedioPorCurso(Integer usuarioId, Integer cursoId) {
        String sql = """
            SELECT AVG(r.puntuacion)
            FROM Respuesta r
            JOIN Evaluacion e ON r.evaluacionId = e.idEvaluacion
            JOIN Modulo m ON e.moduloId = m.idModulo
            WHERE r.usuarioId = :usuario
            AND m.cursoId = :curso
        """;

        Number res = (Number) entityManager
                .createNativeQuery(sql)
                .setParameter("usuario", usuarioId)
                .setParameter("curso", cursoId)
                .getSingleResult();

        return res == null ? null : res.doubleValue();
    }

    @Override
    public boolean usuarioHaPresentadoEvaluacion(Integer usuarioId, Integer evaluacionId) {
        String sql = """
            SELECT COUNT(*)
            FROM Respuesta
            WHERE usuarioId = :usuario
            AND evaluacionId = :evaluacion
        """;

        Number result = (Number) entityManager
                .createNativeQuery(sql)
                .setParameter("usuario", usuarioId)
                .setParameter("evaluacion", evaluacionId)
                .getSingleResult();

        Long count = result.longValue();

        return count > 0;
    }
}