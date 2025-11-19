package com.eam.capacitaciones.repository;

import com.eam.capacitaciones.domain.entity.Curso;
import com.eam.capacitaciones.domain.entity.Curso.NivelEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CursoRepository extends JpaRepository<Curso, Long> {
    
    List<Curso> findByActivoTrue();
    
    List<Curso> findByInstructorId(Long instructorId);
    
    List<Curso> findByNivel(NivelEnum nivel);
    
    List<Curso> findByNivelAndActivoTrue(NivelEnum nivel);
    
    @Query("SELECT c FROM Curso c WHERE c.activo = true AND LOWER(c.titulo) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<Curso> searchByTitulo(@Param("search") String search);
    
    @Query("SELECT c FROM Curso c WHERE c.instructorId = :instructorId AND c.activo = true")
    List<Curso> findActiveCursosByInstructor(@Param("instructorId") Long instructorId);
    
    @Query("SELECT COUNT(c) FROM Curso c WHERE c.instructorId = :instructorId")
    Long countByInstructor(@Param("instructorId") Long instructorId);
}