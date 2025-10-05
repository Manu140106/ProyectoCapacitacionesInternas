package com.example.plataforma_capacitacion.repository;

import com.example.plataforma_capacitacion.model.Modulo;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface ModuloRepository extends JpaRepository<Modulo, Long> {
    List<Modulo> findByCursoId(Long cursoId);
}

