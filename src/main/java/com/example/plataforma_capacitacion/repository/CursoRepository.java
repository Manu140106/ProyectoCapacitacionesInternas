package com.example.plataforma_capacitacion.repository;

import com.example.plataforma_capacitacion.model.Curso;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface CursoRepository extends JpaRepository<Curso, Long> {
    Curso findByNombre(String nombre);
    boolean existByNombre(String nombre);
}
