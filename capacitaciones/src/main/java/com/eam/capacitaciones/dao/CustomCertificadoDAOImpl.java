package com.eam.capacitaciones.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@Slf4j
public class CustomCertificadoDAOImpl implements CustomCertificadoDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Map<String, Object>> obtenerEstadisticasCertificadosPorPeriodo(int year, int month) {
        log.debug("Obteniendo estadísticas de certificados para {}/{}", month, year);

        String sql = "SELECT " +
                     "DATE(cert.fechaEmision) as fecha, " +
                     "COUNT(*) as totalCertificados, " +
                     "COUNT(DISTINCT cert.usuarioId) as usuariosUnicos, " +
                     "COUNT(DISTINCT cert.cursoId) as cursosUnicos " +
                     "FROM Certificado cert " +
                     "WHERE YEAR(cert.fechaEmision) = :year " +
                     "AND MONTH(cert.fechaEmision) = :month " +
                     "AND cert.revocado = false " +
                     "GROUP BY DATE(cert.fechaEmision) " +
                     "ORDER BY DATE(cert.fechaEmision)";

        List<?> results = entityManager.createNativeQuery(sql)
                .setParameter("year", year)
                .setParameter("month", month)
                .getResultList();

        List<Map<String, Object>> estadisticas = new ArrayList<>();
        for (Object rowObj : results) {
            Object[] row = (Object[]) rowObj;
            Map<String, Object> stat = new HashMap<>();
            stat.put("fecha", row[0]);
            stat.put("totalCertificados", row[1]);
            stat.put("usuariosUnicos", row[2]);
            stat.put("cursosUnicos", row[3]);
            estadisticas.add(stat);
        }

        return estadisticas;
    }

    @Override
    public List<Map<String, Object>> obtenerUsuariosConMasCertificados(int limit) {
        log.debug("Obteniendo top {} usuarios con más certificados", limit);

        String jpql = "SELECT u.idUsuario, u.nombre, u.email, u.departamento, COUNT(cert) as totalCertificados " +
                      "FROM Certificado cert " +
                      "JOIN Usuario u ON cert.usuarioId = u.idUsuario " +
                      "WHERE cert.revocado = false " +
                      "GROUP BY u.idUsuario, u.nombre, u.email, u.departamento " +
                      "ORDER BY COUNT(cert) DESC";

        List<Object[]> results = entityManager.createQuery(jpql, Object[].class)
                .setMaxResults(limit)
                .getResultList();

        List<Map<String, Object>> usuarios = new ArrayList<>();
        for (Object[] row : results) {
            Map<String, Object> usuario = new HashMap<>();
            usuario.put("idUsuario", row[0]);
            usuario.put("nombre", row[1]);
            usuario.put("email", row[2]);
            usuario.put("departamento", row[3]);
            usuario.put("totalCertificados", row[4]);
            usuarios.add(usuario);
        }

        return usuarios;
    }

    @Override
    public List<Map<String, Object>> obtenerCursosConMasCertificados(int limit) {
        log.debug("Obteniendo top {} cursos con más certificados", limit);

        String jpql = "SELECT c.idCurso, c.titulo, c.nivel, COUNT(cert) as totalCertificados " +
                      "FROM Certificado cert " +
                      "JOIN Curso c ON cert.cursoId = c.idCurso " +
                      "WHERE cert.revocado = false " +
                      "GROUP BY c.idCurso, c.titulo, c.nivel " +
                      "ORDER BY COUNT(cert) DESC";

        List<Object[]> results = entityManager.createQuery(jpql, Object[].class)
                .setMaxResults(limit)
                .getResultList();

        List<Map<String, Object>> cursos = new ArrayList<>();
        for (Object[] row : results) {
            Map<String, Object> curso = new HashMap<>();
            curso.put("idCurso", row[0]);
            curso.put("titulo", row[1]);
            curso.put("nivel", row[2]);
            curso.put("totalCertificados", row[3]);
            cursos.add(curso);
        }

        return cursos;
    }

    @Override
    public Map<String, Object> verificarCertificadoPorHash(String hash) {
        log.debug("Verificando certificado con hash: {}", hash);

        String jpql = "SELECT cert.idCertificado, cert.fechaEmision, cert.revocado, " +
                      "u.nombre, u.email, c.titulo, c.nivel " +
                      "FROM Certificado cert " +
                      "JOIN Usuario u ON cert.usuarioId = u.idUsuario " +
                      "JOIN Curso c ON cert.cursoId = c.idCurso " +
                      "WHERE cert.hash = :hash";

        try {
            Object[] result = entityManager.createQuery(jpql, Object[].class)
                    .setParameter("hash", hash)
                    .getSingleResult();

            Map<String, Object> certificado = new HashMap<>();
            certificado.put("existe", true);
            certificado.put("idCertificado", result[0]);
            certificado.put("fechaEmision", result[1]);
            certificado.put("revocado", result[2]);
            certificado.put("nombreUsuario", result[3]);
            certificado.put("emailUsuario", result[4]);
            certificado.put("tituloCurso", result[5]);
            certificado.put("nivelCurso", result[6]);

            return certificado;
        } catch (NoResultException e) {
            Map<String, Object> certificado = new HashMap<>();
            certificado.put("existe", false);
            return certificado;
        }
    }
}