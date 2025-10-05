package com.example.plataforma_capacitacion.repository;

import com.example.plataforma_capacitacion.model.Inscripcion;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface InscripcionRepository extends JpaRepository<Inscripcion, Long> {
    List<Inscripcion> findByUsuarioId(Long usuarioId);
    List<Inscripcion> findByCursoId(Long cursId);
}
