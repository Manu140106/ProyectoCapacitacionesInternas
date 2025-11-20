package com.eam.capacitaciones.service;

import com.eam.capacitaciones.dto.request.BadgeCreateRequest;
import com.eam.capacitaciones.dto.response.BadgeDTO;
import com.eam.capacitaciones.domain.entity.Badge;
import com.eam.capacitaciones.domain.entity.Usuario;
import com.eam.capacitaciones.exception.BadRequestException;
import com.eam.capacitaciones.exception.ResourceNotFoundException;
import com.eam.capacitaciones.mapper.BadgeMapper;
import com.eam.capacitaciones.repository.BadgeRepository;
import com.eam.capacitaciones.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class BadgeService {

    private final BadgeRepository badgeRepository;
    private final UsuarioRepository usuarioRepository;
    private final BadgeMapper badgeMapper;

    @Transactional(readOnly = true)
    public List<BadgeDTO> getAllBadges() {
        log.debug("Obteniendo todos los badges");
        List<Badge> badges = badgeRepository.findAll();
        return badgeMapper.toDTOList(badges);
    }

    @Transactional(readOnly = true)
    public BadgeDTO getBadgeById(Long id) {
        log.debug("Obteniendo badge por ID: {}", id);
        Badge badge = badgeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Badge no encontrado con ID: " + id));
        return badgeMapper.toDTO(badge);
    }

    @Transactional(readOnly = true)
    public List<BadgeDTO> getBadgesByUsuario(Long usuarioId) {
        log.debug("Obteniendo badges del usuario: {}", usuarioId);
        List<Badge> badges = badgeRepository.findByUsuarioId(usuarioId);
        return badgeMapper.toDTOList(badges);
    }

    public BadgeDTO createBadge(BadgeCreateRequest request) {
        log.info("Creando nuevo badge: {}", request.getNombre());

        if (badgeRepository.existsByNombre(request.getNombre())) {
            throw new BadRequestException("Ya existe un badge con el nombre: " + request.getNombre());
        }

        Badge badge = badgeMapper.toEntity(request);
        Badge badgeGuardado = badgeRepository.save(badge);

        log.info("Badge creado exitosamente con ID: {}", badgeGuardado.getIdBadge());
        return badgeMapper.toDTO(badgeGuardado);
    }

    public void asignarBadgeAUsuario(Long usuarioId, Long badgeId) {
        log.info("Asignando badge {} a usuario {}", badgeId, usuarioId);

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Badge badge = badgeRepository.findById(badgeId)
                .orElseThrow(() -> new ResourceNotFoundException("Badge no encontrado"));

        if (usuario.getBadges().contains(badge)) {
            throw new BadRequestException("El usuario ya tiene este badge asignado");
        }

        usuario.addBadge(badge);
        usuarioRepository.save(usuario);

        log.info("Badge asignado exitosamente");
    }

    public void deleteBadge(Long id) {
        log.warn("Eliminando badge ID: {}", id);

        if (!badgeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Badge no encontrado");
        }

        badgeRepository.deleteById(id);
        log.info("Badge eliminado ID: {}", id);
    }
}