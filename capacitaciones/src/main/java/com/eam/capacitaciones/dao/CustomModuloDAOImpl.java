package com.eam.capacitaciones.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@Slf4j
public class CustomModuloDAOImpl implements CustomModuloDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> obtenerModulosPorCurso(Integer cursoId) {
        String sql = """
            SELECT idModulo, titulo, tipo, orden
            FROM Modulo
            WHERE cursoId = :curso
            ORDER BY orden
        """;

        List<Object[]> results = entityManager
                .createNativeQuery(sql)
                .setParameter("curso", cursoId)
                .getResultList();

        List<Map<String, Object>> modulos = new ArrayList<>();

        for (Object[] row : results) {
            Map<String, Object> map = new HashMap<>();
            map.put("idModulo", row[0]);
            map.put("titulo", row[1]);
            map.put("tipo", row[2]);
            map.put("orden", row[3]);
            modulos.add(map);
        }

        return modulos;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> obtenerSiguienteModulo(Integer cursoId, Integer ordenActual) {
        String sql = """
            SELECT idModulo, titulo, tipo, orden
            FROM Modulo
            WHERE cursoId = :curso
            AND orden > :orden
            ORDER BY orden
            LIMIT 1
        """;

        List<Object[]> result = entityManager
                .createNativeQuery(sql)
                .setParameter("curso", cursoId)
                .setParameter("orden", ordenActual)
                .getResultList();

        if (result.isEmpty()) return null;

        Object[] row = result.get(0);
        Map<String, Object> map = new HashMap<>();
        map.put("idModulo", row[0]);
        map.put("titulo", row[1]);
        map.put("tipo", row[2]);
        map.put("orden", row[3]);

        return map;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> obtenerUltimoModuloCompletado(Integer usuarioId, Integer cursoId) {
        String sql = """
            SELECT m.idModulo, m.titulo, m.orden, r.puntuacion
            FROM Respuesta r
            JOIN Evaluacion e ON r.evaluacionId = e.idEvaluacion
            JOIN Modulo m ON e.moduloId = m.idModulo
            WHERE r.usuarioId = :usuario
            AND m.cursoId = :curso
            ORDER BY m.orden DESC
            LIMIT 1
        """;

        List<Object[]> result = entityManager
                .createNativeQuery(sql)
                .setParameter("usuario", usuarioId)
                .setParameter("curso", cursoId)
                .getResultList();

        if (result.isEmpty()) return null;

        Object[] row = result.get(0);
        Map<String, Object> map = new HashMap<>();
        map.put("idModulo", row[0]);
        map.put("titulo", row[1]);
        map.put("orden", row[2]);
        map.put("puntuacion", row[3]);

        return map;
    }
}