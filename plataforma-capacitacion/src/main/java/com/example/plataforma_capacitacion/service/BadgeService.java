package com.example.plataforma_capacitacion.service;

import com.example.plataforma_capacitacion.model.Badge;
import com.example.plataforma_capacitacion.repository.BadgeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class BadgeService {

    @Autowired
    private BadgeRepository badgeRepository;

    public List<Badge> listarBadges() {
        return badgeRepository.findAll();
    }

    public Badge guardarBadge(Badge badge) {
        return badgeRepository.save(badge);
    }

    public Badge obtenerPorId(Long id) {
        return badgeRepository.findById(id).orElse(null);
    }

    public void eliminarBadge(Long id) {
        badgeRepository.deleteById(id);
    }
    public List<Badge> listarPorUsuario(Long usuarioId) {
        return badgeRepository.findByUsuarioId(usuarioId);
    }
}
