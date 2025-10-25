package com.example.plataforma_capacitacion.controller;

import com.example.plataforma_capacitacion.model.Respuesta;
import com.example.plataforma_capacitacion.repository.RespuestaRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/respuestas")
public class RespuestaController {

    private final RespuestaRepository respuestaRepository;

    public RespuestaController(RespuestaRepository respuestaRepository) {
        this.respuestaRepository = respuestaRepository;
    }

    @GetMapping
    public List<Respuesta> getAll() {
        return respuestaRepository.findAll();
    }

    @GetMapping("/{id}")
    public Respuesta getById(@PathVariable Long id) {
        return respuestaRepository.findById(id).orElse(null);
    }

    @PostMapping
    public Respuesta create(@RequestBody Respuesta respuesta) {
        return respuestaRepository.save(respuesta);
    }

    @PutMapping("/{id}")
    public Respuesta update(@PathVariable Long id, @RequestBody Respuesta respuesta) {
        respuesta.setId(id);
        return respuestaRepository.save(respuesta);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        respuestaRepository.deleteById(id);
    }
}
