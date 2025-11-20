package com.eam.capacitaciones.dao;

import com.eam.capacitaciones.domain.entity.Curso;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class CustomCursoDAOImpl implements CustomCursoDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Curso> buscarCursosConFiltros(String titulo, String nivel, 
                                               Long instructorId, Boolean activo, 
                                               Integer minInscritos) {
        log.debug("Buscando cursos con filtros avanzados");

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Curso> cq = cb.createQuery(Curso.class);
        Root<Curso> curso = cq.from(Curso.class);

        List<Predicate> predicates = new ArrayList<>();

        if (titulo != null && !titulo.isEmpty()) {
            predicates.add(cb.like(cb.lower(curso.get("titulo")), 
                                   "%" + titulo.toLowerCase() + "%"));
        }

        if (nivel != null && !nivel.isEmpty()) {
            predicates.add(cb.equal(curso.get("nivel"), Curso.NivelEnum.valueOf(nivel)));
        }

        if (instructorId != null) {
            predicates.add(cb.equal(curso.get("instructorId"), instructorId));
        }

        if (activo != null) {
            predicates.add(cb.equal(curso.get("activo"), activo));
        }

        if (minInscritos != null && minInscritos > 0) {
            Subquery<Long> subquery = cq.subquery(Long.class);
            Root<Curso> subCurso = subquery.correlate(curso);
            subquery.select(cb.count(subCurso.join("inscripciones")));
            
            predicates.add(cb.greaterThanOrEqualTo(subquery, minInscritos.longValue()));
        }

        if (!predicates.isEmpty()) {
            cq.where(cb.and(predicates.toArray(new Predicate[0])));
        }

        cq.orderBy(cb.desc(curso.get("fechaCreacion")));

        return entityManager.createQuery(cq).getResultList();
    }

    @Override
    public List<Map<String, Object>> obtenerCursosMasPopulares(int limit) {
        log.debug("Obteniendo top {} cursos más populares", limit);

        String jpql = "SELECT c.idCurso, c.titulo, c.nivel, COUNT(i.idInscripcion) as totalInscritos " +
                      "FROM Curso c " +
                      "LEFT JOIN c.inscripciones i " +
                      "WHERE c.activo = true " +
                      "GROUP BY c.idCurso, c.titulo, c.nivel " +
                      "ORDER BY COUNT(i.idInscripcion) DESC";

        List<Object[]> results = entityManager.createQuery(jpql, Object[].class)
                                             .setMaxResults(limit)
                                             .getResultList();

        List<Map<String, Object>> cursos = new ArrayList<>();
        for (Object[] row : results) {
            Map<String, Object> curso = new HashMap<>();
            curso.put("idCurso", row[0]);
            curso.put("titulo", row[1]);
            curso.put("nivel", row[2]);
            curso.put("totalInscritos", row[3]);
            cursos.add(curso);
        }

        return cursos;
    }

    @Override
    public List<Curso> obtenerCursosRecomendados(Long usuarioId, int limit) {
        log.debug("Obteniendo cursos recomendados para usuario: {}", usuarioId);

        String jpql = "SELECT DISTINCT c FROM Curso c " +
                      "WHERE c.activo = true " +
                      "AND c.idCurso NOT IN (" +
                      "    SELECT i.cursoId FROM Inscripcion i " +
                      "    WHERE i.usuarioId = :usuarioId" +
                      ") " +
                      "ORDER BY c.fechaCreacion DESC";

        return entityManager.createQuery(jpql, Curso.class)
                           .setParameter("usuarioId", usuarioId)
                           .setMaxResults(limit)
                           .getResultList();
    }

    @Override
    public Map<String, Long> obtenerEstadisticasPorNivel() {
        log.debug("Obteniendo estadísticas de cursos por nivel");

        String jpql = "SELECT c.nivel, COUNT(c) FROM Curso c " +
                      "WHERE c.activo = true " +
                      "GROUP BY c.nivel " +
                      "ORDER BY COUNT(c) DESC";

        List<Object[]> results = entityManager.createQuery(jpql, Object[].class)
                                             .getResultList();

        return results.stream()
                     .collect(Collectors.toMap(
                         row -> row[0].toString(),
                         row -> (Long) row[1]
                     ));
    }

    @Override
    public List<Map<String, Object>> obtenerCursosConBajaCompletacion(double umbralPorcentaje) {
        log.debug("Obteniendo cursos con tasa de completación menor a {}%", umbralPorcentaje);

        String sql = "SELECT c.idCurso, c.titulo, " +
                     "COUNT(DISTINCT i1.idInscripcion) as totalInscritos, " +
                     "COUNT(DISTINCT i2.idInscripcion) as totalCompletados, " +
                     "(COUNT(DISTINCT i2.idInscripcion) * 100.0 / COUNT(DISTINCT i1.idInscripcion)) as tasaCompletacion " +
                     "FROM Curso c " +
                     "LEFT JOIN Inscripcion i1 ON c.idCurso = i1.cursoId " +
                     "LEFT JOIN Inscripcion i2 ON c.idCurso = i2.cursoId AND i2.estado = 'COMPLETADO' " +
                     "WHERE c.activo = true " +
                     "GROUP BY c.idCurso, c.titulo " +
                     "HAVING COUNT(DISTINCT i1.idInscripcion) > 0 " +
                     "AND (COUNT(DISTINCT i2.idInscripcion) * 100.0 / COUNT(DISTINCT i1.idInscripcion)) < :umbral " +
                     "ORDER BY tasaCompletacion ASC";

        @SuppressWarnings("unchecked")
        List<Object[]> results = (List<Object[]>) entityManager.createNativeQuery(sql)
                                             .setParameter("umbral", umbralPorcentaje)
                                             .getResultList();

        List<Map<String, Object>> cursos = new ArrayList<>();
        for (Object[] row : results) {
            Map<String, Object> curso = new HashMap<>();
            curso.put("idCurso", row[0]);
            curso.put("titulo", row[1]);
            curso.put("totalInscritos", row[2]);
            curso.put("totalCompletados", row[3]);
            curso.put("tasaCompletacion", row[4]);
            cursos.add(curso);
        }

        return cursos;
    }
}