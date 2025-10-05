package com.example.plataforma_capacitacion.controller;

import com.example.plataforma_capacitacion.model.Modulo;
import com.example.plataforma_capacitacion.repository.ModuloRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/modulos")
public class ModuloController {

    private final ModuloRepository moduloRepository;

    public ModuloController(ModuloRepository moduloRepository) {
        this.moduloRepository = moduloRepository;
    }

    // GET todos
    @GetMapping
    public List<Modulo> getAllModulos() {
        return moduloRepository.findAll();
    }

    // GET por id
    @GetMapping("/{id}")
    public Modulo getModuloById(@PathVariable Long id) {
        return moduloRepository.findById(id).orElse(null);
    }

    // POST (crear)
    @PostMapping
    public Modulo createModulo(@RequestBody Modulo modulo) {
        return moduloRepository.save(modulo);
    }

    // PUT (actualizar)
    @PutMapping("/{id}")
    public Modulo updateModulo(@PathVariable Long id, @RequestBody Modulo modulo) {
        modulo.setId(id);
        return moduloRepository.save(modulo);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public void deleteModulo(@PathVariable Long id) {
        moduloRepository.deleteById(id);
    }
}

