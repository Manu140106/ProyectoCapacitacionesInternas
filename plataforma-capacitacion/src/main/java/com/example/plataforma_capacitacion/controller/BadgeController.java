package com.example.plataforma_capacitacion.controller;

import com.example.plataforma_capacitacion.model.Badge;
import com.example.plataforma_capacitacion.repository.BadgeRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/badges")
public class BadgeController {

    private final BadgeRepository badgeRepository;

    public BadgeController(BadgeRepository badgeRepository) {
        this.badgeRepository = badgeRepository;
    }

    @GetMapping
    public List<Badge> getAll() {
        return badgeRepository.findAll();
    }

    @GetMapping("/{id}")
    public Badge getById(@PathVariable Long id) {
        return badgeRepository.findById(id).orElse(null);
    }

    @PostMapping
    public Badge create(@RequestBody Badge badge) {
        return badgeRepository.save(badge);
    }

    @PutMapping("/{id}")
    public Badge update(@PathVariable Long id, @RequestBody Badge badge) {
        badge.setId(id);
        return badgeRepository.save(badge);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        badgeRepository.deleteById(id);
    }
}
