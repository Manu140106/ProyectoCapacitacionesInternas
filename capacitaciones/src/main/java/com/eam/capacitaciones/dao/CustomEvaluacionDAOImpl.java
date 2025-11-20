package com.eam.capacitaciones.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class CustomEvaluacionDAOImpl implements CustomEvaluacionDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Map<Long, Double> obtenerPromedioCalificacionesPorEvaluacion() {
        log.debug("Calculando promedio de calificaciones por evaluación");

        String jpql = """
            SELECT r.evaluacionId, AVG(r.puntuacion)
            FROM Respuesta r
            WHERE r.calificada = true
            GROUP BY r.evaluacionId
        """;

        List<Object[]> results = entityManager.createQuery(jpql, Object[].class)
                .getResultList();

        return results.stream()
                .collect(Collectors.toMap(
                    row -> (Long) row[0],
                    row -> ((Number) row[1]).doubleValue()
                ));
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> obtenerEvaluacionesConRespuestasPendientes(Long instructorId) {
        log.debug("Obteniendo evaluaciones con respuestas pendientes del instructor: {}", instructorId);

        String sql = """
            SELECT e.idEvaluacion, e.titulo, m.titulo as moduloTitulo,
                   c.titulo as cursoTitulo, COUNT(r.idRespuesta) as pendientes
            FROM Evaluacion e
            JOIN Modulo m ON e.moduloId = m.idModulo
            JOIN Curso c ON m.cursoId = c.idCurso
            JOIN Respuesta r ON e.idEvaluacion = r.evaluacionId
            WHERE c.instructorId = :instructorId
            AND r.calificada = false
            AND e.tipo = 'ABIERTA'
            GROUP BY e.idEvaluacion, e.titulo, m.titulo, c.titulo
            ORDER BY COUNT(r.idRespuesta) DESC
        """;

        List<Object[]> results = entityManager.createNativeQuery(sql)
                .setParameter("instructorId", instructorId)
                .getResultList();

        List<Map<String, Object>> evaluaciones = new ArrayList<>();
        for (Object[] row : results) {
            Map<String, Object> eval = new HashMap<>();
            eval.put("idEvaluacion", row[0]);
            eval.put("tituloEvaluacion", row[1]);
            eval.put("tituloModulo", row[2]);
            eval.put("tituloCurso", row[3]);
            eval.put("respuestasPendientes", row[4]);
            evaluaciones.add(eval);
        }

        return evaluaciones;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> obtenerEstadisticasAprobacion(Long cursoId, Integer puntajeMinimo) {
        log.debug("Obteniendo estadísticas de aprobación para curso: {}", cursoId);

        String sql = """
            SELECT 
                e.idEvaluacion,
                e.titulo,
                COUNT(DISTINCT r.usuarioId) as totalEstudiantes,
                SUM(CASE WHEN r.puntuacion >= :puntajeMin THEN 1 ELSE 0 END) as aprobados,
                (SUM(CASE WHEN r.puntuacion >= :puntajeMin THEN 1 ELSE 0 END) * 100.0 / 
                 COUNT(DISTINCT r.usuarioId)) as tasaAprobacion,
                AVG(r.puntuacion) as promedioCalificacion
            FROM Evaluacion e
            INNER JOIN Modulo m ON e.moduloId = m.idModulo
            INNER JOIN Respuesta r ON e.idEvaluacion = r.evaluacionId
            WHERE m.cursoId = :cursoId
            AND r.calificada = true
            GROUP BY e.idEvaluacion, e.titulo
        """;

        List<Object[]> results = entityManager.createNativeQuery(sql)
                .setParameter("cursoId", cursoId)
                .setParameter("puntajeMin", puntajeMinimo)
                .getResultList();

        List<Map<String, Object>> estadisticas = new ArrayList<>();
        for (Object[] row : results) {
            Map<String, Object> stat = new HashMap<>();
            stat.put("idEvaluacion", row[0]);
            stat.put("titulo", row[1]);
            stat.put("totalEstudiantes", row[2]);
            stat.put("aprobados", row[3]);
            stat.put("tasaAprobacion", row[4]);
            stat.put("promedioCalificacion", row[5]);
            estadisticas.add(stat);
        }

        return estadisticas;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> obtenerEvaluacionesMasDificiles(int limit) {
        log.debug("Obteniendo {} evaluaciones más difíciles", limit);

        String sql = """
            SELECT 
                e.idEvaluacion,
                e.titulo,
                c.titulo as cursoTitulo,
                AVG(r.puntuacion) as promedioCalificacion,
                (SUM(CASE WHEN r.puntuacion < e.puntajeMax * 0.6 THEN 1 ELSE 0 END) * 100.0 / COUNT(*)) as tasaReprobacion
            FROM Evaluacion e
            INNER JOIN Modulo m ON e.moduloId = m.idModulo
            INNER JOIN Curso c ON m.cursoId = c.idCurso
            INNER JOIN Respuesta r ON e.idEvaluacion = r.evaluacionId
            WHERE r.calificada = true
            GROUP BY e.idEvaluacion, e.titulo, c.titulo, e.puntajeMax
            HAVING COUNT(*) >= 5
            ORDER BY AVG(r.puntuacion) ASC
            LIMIT :limit
        """;

        List<Object[]> results = entityManager.createNativeQuery(sql)
                .setParameter("limit", limit)
                .getResultList();

        List<Map<String, Object>> evaluaciones = new ArrayList<>();
        for (Object[] row : results) {
            Map<String, Object> eval = new HashMap<>();
            eval.put("idEvaluacion", row[0]);
            eval.put("tituloEvaluacion", row[1]);
            eval.put("tituloCurso", row[2]);
            eval.put("promedioCalificacion", row[3]);
            eval.put("tasaReprobacion", row[4]);
            evaluaciones.add(eval);
        }

        return evaluaciones;
    }
}