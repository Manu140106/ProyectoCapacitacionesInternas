package com.eam.capacitaciones.repository;

import com.eam.capacitaciones.domain.entity.Modulo;
import com.eam.capacitaciones.domain.entity.Modulo.TipoEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModuloRepository extends JpaRepository<Modulo, Long> {
    
    List<Modulo> findByCursoIdOrderByOrdenAsc(Long cursoId);
    
    List<Modulo> findByCursoIdAndTipo(Long cursoId, TipoEnum tipo);
    
    @Query("SELECT COUNT(m) FROM Modulo m WHERE m.cursoId = :cursoId")
    Long countByCurso(@Param("cursoId") Long cursoId);
    
    @Query("SELECT m FROM Modulo m WHERE m.cursoId = :cursoId ORDER BY m.orden ASC")
    List<Modulo> findModulosByCursoOrdenados(@Param("cursoId") Long cursoId);
    
    @Query("SELECT MAX(m.orden) FROM Modulo m WHERE m.cursoId = :cursoId")
    Integer findMaxOrdenByCurso(@Param("cursoId") Long cursoId);
}