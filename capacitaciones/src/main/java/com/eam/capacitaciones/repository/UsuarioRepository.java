package com.eam.capacitaciones.repository;

import com.eam.capacitaciones.domain.entity.Usuario;
import com.eam.capacitaciones.domain.entity.Usuario.RolEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    Optional<Usuario> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    List<Usuario> findByRol(RolEnum rol);
    
    List<Usuario> findByDepartamento(String departamento);
    
    List<Usuario> findByActivoTrue();
    
    @Query("SELECT u FROM Usuario u WHERE u.activo = true AND u.rol = :rol")
    List<Usuario> findActiveByRol(@Param("rol") RolEnum rol);
    
    @Query("SELECT COUNT(u) FROM Usuario u WHERE u.rol = :rol")
    Long countByRol(@Param("rol") RolEnum rol);
}
