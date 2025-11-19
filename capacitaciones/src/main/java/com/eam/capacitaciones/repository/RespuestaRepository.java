package com.eam.capacitaciones.repository;

import com.eam.capacitaciones.domain.entity.Respuesta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RespuestaRepository extends JpaRepository<Respuesta, Long> {
    
    List<Respuesta> findByEvaluacionId(Long evaluacionId);
    
    List<Respuesta> findByUsuarioId(Long usuarioId);
    
    List<Respuesta> findByEvaluacionIdAndUsuarioId(Long evaluacionId, Long usuarioId);
    
    @Query("SELECT r FROM Respuesta r WHERE r.evaluacionId = :evaluacionId AND r.calificada = false")
    List<Respuesta> findPendientesCalificacion(@Param("evaluacionId") Long evaluacionId);
    
    @Query("SELECT COUNT(r) FROM Respuesta r WHERE r.evaluacionId = :evaluacionId AND r.usuarioId = :usuarioId")
    Long countIntentosByUsuario(@Param("evaluacionId") Long evaluacionId, @Param("usuarioId") Long usuarioId);
    
    @Query("SELECT r FROM Respuesta r WHERE r.usuarioId = :usuarioId " +
           "AND r.evaluacionId IN (SELECT e.idEvaluacion FROM Evaluacion e WHERE e.moduloId IN " +
           "(SELECT m.idModulo FROM Modulo m WHERE m.cursoId = :cursoId))")
    List<Respuesta> findRespuestasByUsuarioAndCurso(@Param("usuarioId") Long usuarioId, @Param("cursoId") Long cursoId);
}