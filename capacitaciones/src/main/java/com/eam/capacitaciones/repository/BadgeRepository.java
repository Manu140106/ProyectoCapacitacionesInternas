package com.eam.capacitaciones.repository;

import com.eam.capacitaciones.domain.entity.Badge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BadgeRepository extends JpaRepository<Badge, Long> {
    
    Optional<Badge> findByNombre(String nombre);
    
    boolean existsByNombre(String nombre);
    
    @Query("SELECT b FROM Badge b WHERE b.puntosRequeridos <= :puntos")
    List<Badge> findByPuntosRequeridosLessThanEqual(@Param("puntos") Integer puntos);
    
    @Query("SELECT b FROM Badge b JOIN b.usuarios u WHERE u.idUsuario = :usuarioId")
    List<Badge> findByUsuarioId(@Param("usuarioId") Long usuarioId);
    
    @Query("SELECT COUNT(b) FROM Badge b JOIN b.usuarios u WHERE u.idUsuario = :usuarioId")
    Long countByUsuarioId(@Param("usuarioId") Long usuarioId);
}