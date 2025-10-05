package com.example.plataforma_capacitacion.repository;

import com.example.plataforma_capacitacion.model.Badge;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface BadgeRepository extends JpaRepository<Badge, Long> {
    List<Badge> findByUsuarioId(Long usuarioId);
}