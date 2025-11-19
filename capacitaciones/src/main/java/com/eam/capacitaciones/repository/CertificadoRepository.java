package com.eam.capacitaciones.repository;

import com.eam.capacitaciones.domain.entity.Certificado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CertificadoRepository extends JpaRepository<Certificado, Long> {
    
    List<Certificado> findByUsuarioId(Long usuarioId);
    
    List<Certificado> findByCursoId(Long cursoId);
    
    Optional<Certificado> findByHash(String hash);
    
    Optional<Certificado> findByUsuarioIdAndCursoId(Long usuarioId, Long cursoId);
    
    boolean existsByUsuarioIdAndCursoId(Long usuarioId, Long cursoId);
    
    @Query("SELECT c FROM Certificado c WHERE c.usuarioId = :usuarioId AND c.revocado = false")
    List<Certificado> findValidosByUsuario(@Param("usuarioId") Long usuarioId);
    
    @Query("SELECT COUNT(c) FROM Certificado c WHERE c.cursoId = :cursoId")
    Long countByCurso(@Param("cursoId") Long cursoId);
}