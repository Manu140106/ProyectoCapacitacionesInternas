package com.eam.capacitaciones.repository;

import com.eam.capacitaciones.domain.entity.Evaluacion;
import com.eam.capacitaciones.domain.entity.Evaluacion.TipoEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvaluacionRepository extends JpaRepository<Evaluacion, Long> {
    
    List<Evaluacion> findByModuloId(Long moduloId);
    
    List<Evaluacion> findByTipo(TipoEnum tipo);
    
    @Query("SELECT e FROM Evaluacion e WHERE e.moduloId IN " +
           "(SELECT m.idModulo FROM Modulo m WHERE m.cursoId = :cursoId)")
    List<Evaluacion> findByCursoId(@Param("cursoId") Long cursoId);
    
    @Query("SELECT COUNT(e) FROM Evaluacion e WHERE e.moduloId = :moduloId")
    Long countByModulo(@Param("moduloId") Long moduloId);
}