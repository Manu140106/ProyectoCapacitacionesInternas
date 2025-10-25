package com.example.plataforma_capacitacion.controller;

import com.example.plataforma_capacitacion.model.Inscripcion;
import com.example.plataforma_capacitacion.repository.InscripcionRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inscripciones")
public class InscripcionController {

    private final InscripcionRepository inscripcionRepository;

    public InscripcionController(InscripcionRepository inscripcionRepository) {
        this.inscripcionRepository = inscripcionRepository;
    }

    @GetMapping
    public List<Inscripcion> getAll() {
        return inscripcionRepository.findAll();
    }

    @GetMapping("/{id}")
    public Inscripcion getById(@PathVariable Long id) {
        return inscripcionRepository.findById(id).orElse(null);
    }

    @PostMapping
    public Inscripcion create(@RequestBody Inscripcion inscripcion) {
        return inscripcionRepository.save(inscripcion);
    }

    @PutMapping("/{id}")
    public Inscripcion update(@PathVariable Long id, @RequestBody Inscripcion inscripcion) {
        inscripcion.setId(id);
        return inscripcionRepository.save(inscripcion);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        inscripcionRepository.deleteById(id);
    }
}
