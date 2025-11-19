package com.eam.capacitaciones.repository;

import com.eam.capacitaciones.domain.entity.Inscripcion;
import com.eam.capacitaciones.domain.entity.Inscripcion.EstadoEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface InscripcionRepository extends JpaRepository<Inscripcion, Long> {
    
    List<Inscripcion> findByUsuarioId(Long usuarioId);
    
    List<Inscripcion> findByCursoId(Long cursoId);
    
    List<Inscripcion> findByUsuarioIdAndEstado(Long usuarioId, EstadoEnum estado);
    
    Optional<Inscripcion> findByUsuarioIdAndCursoId(Long usuarioId, Long cursoId);
    
    boolean existsByUsuarioIdAndCursoId(Long usuarioId, Long cursoId);
    
    @Query("SELECT i FROM Inscripcion i WHERE i.usuarioId = :usuarioId AND i.estado = 'COMPLETADO'")
    List<Inscripcion> findCompletadasByUsuario(@Param("usuarioId") Long usuarioId);
    
    @Query("SELECT COUNT(i) FROM Inscripcion i WHERE i.cursoId = :cursoId AND i.estado = 'COMPLETADO'")
    Long countCompletadasByCurso(@Param("cursoId") Long cursoId);
    
    @Query("SELECT i FROM Inscripcion i WHERE i.fechaInscripcion BETWEEN :fechaInicio AND :fechaFin")
    List<Inscripcion> findByFechaInscripcionBetween(@Param("fechaInicio") LocalDate fechaInicio, @Param("fechaFin") LocalDate fechaFin);
}