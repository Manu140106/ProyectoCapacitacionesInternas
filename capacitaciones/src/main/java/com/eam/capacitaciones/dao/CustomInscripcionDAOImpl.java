package com.eam.capacitaciones.dao;

import com.eam.capacitaciones.domain.entity.Inscripcion;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class CustomInscripcionDAOImpl implements CustomInscripcionDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> obtenerReporteInscripcionesPorFecha(
            LocalDate fechaInicio, LocalDate fechaFin) {
        log.debug("Generando reporte de inscripciones entre {} y {}", fechaInicio, fechaFin);

        String sql = """
            SELECT 
                DATE(i.fechaInscripcion) as fecha,
                COUNT(*) as totalInscripciones,
                SUM(CASE WHEN i.estado = 'COMPLETADO' THEN 1 ELSE 0 END) as completadas,
                SUM(CASE WHEN i.estado = 'EN_PROGRESO' THEN 1 ELSE 0 END) as enProgreso,
                SUM(CASE WHEN i.estado = 'ABANDONADO' THEN 1 ELSE 0 END) as abandonadas
            FROM Inscripcion i
            WHERE i.fechaInscripcion BETWEEN :inicio AND :fin
            GROUP BY DATE(i.fechaInscripcion)
            ORDER BY DATE(i.fechaInscripcion)
        """;

        List<Object[]> results = entityManager.createNativeQuery(sql)
                .setParameter("inicio", fechaInicio)
                .setParameter("fin", fechaFin)
                .getResultList();

        List<Map<String, Object>> reporte = new ArrayList<>();
        for (Object[] row : results) {
            Map<String, Object> dia = new HashMap<>();
            dia.put("fecha", row[0]);
            dia.put("totalInscripciones", row[1]);
            dia.put("completadas", row[2]);
            dia.put("enProgreso", row[3]);
            dia.put("abandonadas", row[4]);
            reporte.add(dia);
        }

        return reporte;
    }

    @Override
    public List<Inscripcion> obtenerInscripcionesPorRangoProgreso(
            Double progresoMin, Double progresoMax) {
        log.debug("Buscando inscripciones con progreso entre {} y {}", progresoMin, progresoMax);

        String jpql = """
            SELECT i FROM Inscripcion i
            WHERE i.progreso >= :min AND i.progreso <= :max
            ORDER BY i.progreso DESC
        """;

        return entityManager.createQuery(jpql, Inscripcion.class)
                .setParameter("min", progresoMin)
                .setParameter("max", progresoMax)
                .getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> obtenerCursosAbandonadosPorUsuario(Long usuarioId) {
        log.debug("Obteniendo cursos abandonados del usuario: {}", usuarioId);

        String sql = """
            SELECT c.idCurso, c.titulo, i.progreso, i.fechaInscripcion
            FROM Inscripcion i
            JOIN Curso c ON i.cursoId = c.idCurso
            WHERE i.usuarioId = :usuarioId
            AND i.estado = 'ABANDONADO'
            ORDER BY i.fechaInscripcion DESC
        """;

        List<Object[]> results = entityManager.createNativeQuery(sql)
                .setParameter("usuarioId", usuarioId)
                .getResultList();

        List<Map<String, Object>> cursos = new ArrayList<>();
        for (Object[] row : results) {
            Map<String, Object> curso = new HashMap<>();
            curso.put("idCurso", row[0]);
            curso.put("titulo", row[1]);
            curso.put("progreso", row[2]);
            curso.put("fechaInscripcion", row[3]);
            cursos.add(curso);
        }

        return cursos;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<Long, Double> calcularTasaCompletacionPorCurso() {
        log.debug("Calculando tasa de completación por curso");

        String sql = """
            SELECT 
                c.idCurso,
                (SUM(CASE WHEN i.estado = 'COMPLETADO' THEN 1 ELSE 0 END) * 100.0 / COUNT(*)) as tasa
            FROM Curso c
            INNER JOIN Inscripcion i ON c.idCurso = i.cursoId
            GROUP BY c.idCurso
        """;

        List<Object[]> results = entityManager.createNativeQuery(sql).getResultList();

        return results.stream()
                .collect(Collectors.toMap(
                    row -> ((Number) row[0]).longValue(),
                    row -> ((Number) row[1]).doubleValue()
                ));
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> obtenerUsuariosCercaDeCompletar(Long cursoId) {
        log.debug("Obteniendo usuarios cerca de completar curso: {}", cursoId);

        String sql = """
            SELECT u.idUsuario, u.nombre, u.email, i.progreso
            FROM Inscripcion i
            JOIN Usuario u ON i.usuarioId = u.idUsuario
            WHERE i.cursoId = :cursoId
            AND i.progreso >= 80
            AND i.estado = 'EN_PROGRESO'
            ORDER BY i.progreso DESC
        """;

        List<Object[]> results = entityManager.createNativeQuery(sql)
                .setParameter("cursoId", cursoId)
                .getResultList();

        List<Map<String, Object>> usuarios = new ArrayList<>();
        for (Object[] row : results) {
            Map<String, Object> usuario = new HashMap<>();
            usuario.put("idUsuario", row[0]);
            usuario.put("nombre", row[1]);
            usuario.put("email", row[2]);
            usuario.put("progreso", row[3]);
            usuarios.add(usuario);
        }

        return usuarios;
    }
}