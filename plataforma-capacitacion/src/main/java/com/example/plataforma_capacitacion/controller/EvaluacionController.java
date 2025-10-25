package com.example.plataforma_capacitacion.controller;

import com.example.plataforma_capacitacion.model.Evaluacion;
import com.example.plataforma_capacitacion.repository.EvaluacionRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/evaluaciones")
public class EvaluacionController {

    private final EvaluacionRepository evaluacionRepository;

    public EvaluacionController(EvaluacionRepository evaluacionRepository) {
        this.evaluacionRepository = evaluacionRepository;
    }

    @GetMapping
    public List<Evaluacion> getAll() {
        return evaluacionRepository.findAll();
    }

    @GetMapping("/{id}")
    public Evaluacion getById(@PathVariable Long id) {
        return evaluacionRepository.findById(id).orElse(null);
    }

    @PostMapping
    public Evaluacion create(@RequestBody Evaluacion evaluacion) {
        return evaluacionRepository.save(evaluacion);
    }

    @PutMapping("/{id}")
    public Evaluacion update(@PathVariable Long id, @RequestBody Evaluacion evaluacion) {
        evaluacion.setId(id);
        return evaluacionRepository.save(evaluacion);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        evaluacionRepository.deleteById(id);
    }
}
