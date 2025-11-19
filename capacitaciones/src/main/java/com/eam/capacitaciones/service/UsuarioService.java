package com.eam.capacitaciones.service;

import com.eam.capacitaciones.dto.request.UsuarioCreateRequest;
import com.eam.capacitaciones.dto.request.UsuarioUpdateRequest;
import com.eam.capacitaciones.dto.response.UsuarioDTO;
import com.eam.capacitaciones.domain.entity.Usuario;
import com.eam.capacitaciones.domain.entity.Usuario.RolEnum;
import com.eam.capacitaciones.exception.BadRequestException;
import com.eam.capacitaciones.mapper.UsuarioMapper;
import com.eam.capacitaciones.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio para gestión de usuarios
 * Implementa la lógica de negocio relacionada con usuarios
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public Page<UsuarioDTO> getAllUsuarios(Pageable pageable) {
        log.debug("Obteniendo todos los usuarios - Página: {}", pageable.getPageNumber());
        return usuarioRepository.findAll(pageable)
                .map(usuarioMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public UsuarioDTO getUsuarioById(Long id) {
        log.debug("Obteniendo usuario por ID: {}", id);
        log.debug("Obteniendo usuario por ID: {}", id);
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        return usuarioMapper.toDTO(usuario);
    }

    @Transactional(readOnly = true)
    public UsuarioDTO getUsuarioByEmail(String email) {
        log.debug("Obteniendo usuario por email: {}", email);
        log.debug("Obteniendo usuario por email: {}", email);
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con email: " + email));
        return usuarioMapper.toDTO(usuario);
    }

    @Transactional(readOnly = true)
    public List<UsuarioDTO> getUsuariosByRol(RolEnum rol) {
        log.debug("Obteniendo usuarios por rol: {}", rol);
        List<Usuario> usuarios = usuarioRepository.findByRol(rol);
        return usuarioMapper.toDTOList(usuarios);
    }

    @Transactional(readOnly = true)
    public List<UsuarioDTO> getUsuariosByDepartamento(String departamento) {
        log.debug("Obteniendo usuarios por departamento: {}", departamento);
        List<Usuario> usuarios = usuarioRepository.findByDepartamento(departamento);
        return usuarioMapper.toDTOList(usuarios);
    }

    @Transactional(readOnly = true)
    public List<UsuarioDTO> getUsuariosActivos() {
        log.debug("Obteniendo usuarios activos");
        List<Usuario> usuarios = usuarioRepository.findByActivoTrue();
        return usuarioMapper.toDTOList(usuarios);
    }

    /**
     * Crea un nuevo usuario
     * Reglas de negocio:
     * - El email debe ser único
     * - La contraseña se encripta con BCrypt
     * - Por defecto el usuario está activo
     */
    public UsuarioDTO createUsuario(UsuarioCreateRequest request) {
        log.info("Creando nuevo usuario con email: {}", request.getEmail());

        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Ya existe un usuario con el email: " + request.getEmail());
        }

        Usuario usuario = usuarioMapper.toEntity(request);

        String passwordEncriptada = passwordEncoder.encode(request.getPassword());
        usuario.setPassword(passwordEncriptada);

        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        log.info("Usuario creado exitosamente con ID: {}", usuarioGuardado.getIdUsuario());

        return usuarioMapper.toDTO(usuarioGuardado);
    }

    /**
     * Actualiza un usuario existente
     * Solo actualiza campos no nulos
     */
    public UsuarioDTO updateUsuario(Long id, UsuarioUpdateRequest request) {
        log.info("Actualizando usuario con ID: {}", id);
        log.info("Actualizando usuario con ID: {}", id);

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        if (request.getEmail() != null && !request.getEmail().equals(usuario.getEmail())) {
            if (usuarioRepository.existsByEmail(request.getEmail())) {
                throw new BadRequestException("Ya existe un usuario con el email: " + request.getEmail());
            }
        }

        usuarioMapper.updateEntityFromRequest(request, usuario);

        Usuario usuarioActualizado = usuarioRepository.save(usuario);
        log.info("Usuario actualizado exitosamente con ID: {}", id);

        return usuarioMapper.toDTO(usuarioActualizado);
    }

    public void cambiarPassword(Long id, String nuevaPassword) {
        log.info("Cambiando contraseña del usuario con ID: {}", id);
        log.info("Cambiando contraseña del usuario con ID: {}", id);

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        if (nuevaPassword.length() < 8) {
            throw new BadRequestException("La contraseña debe tener al menos 8 caracteres");
        }

        usuario.setPassword(passwordEncoder.encode(nuevaPassword));
        usuarioRepository.save(usuario);

        log.info("Contraseña cambiada exitosamente para usuario ID: {}", id);
    }

    public void desactivarUsuario(Long id) {
        log.info("Desactivando usuario con ID: {}", id);
        log.info("Desactivando usuario con ID: {}", id);

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        usuario.setActivo(false);
        usuarioRepository.save(usuario);

        log.info("Usuario desactivado exitosamente con ID: {}", id);
    }

    public void activarUsuario(Long id) {
        log.info("Activando usuario con ID: {}", id);
        log.info("Activando usuario con ID: {}", id);

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        usuario.setActivo(true);
        usuarioRepository.save(usuario);

        log.info("Usuario activado exitosamente con ID: {}", id);
    }

    public void deleteUsuario(Long id) {
        log.warn("ELIMINANDO FÍSICAMENTE usuario con ID: {} - OPERACIÓN IRREVERSIBLE", id);

        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado con ID: " + id);
        }
        usuarioRepository.deleteById(id);
        log.warn("Usuario ELIMINADO permanentemente con ID: {}", id);
    }

    @Transactional(readOnly = true)
    public boolean existeEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    @Transactional(readOnly = true)
    public Long contarUsuariosPorRol(RolEnum rol) {
        return usuarioRepository.countByRol(rol);
    }

    @Transactional(readOnly = true)
    public EstadisticasUsuarios getEstadisticas() {
        log.debug("Obteniendo estadísticas de usuarios");

        long totalUsuarios = usuarioRepository.count();
        long totalActivos = usuarioRepository.findByActivoTrue().size();
        long totalAdmins = usuarioRepository.countByRol(RolEnum.ADMIN);
        long totalInstructores = usuarioRepository.countByRol(RolEnum.INSTRUCTOR);
        long totalUsers = usuarioRepository.countByRol(RolEnum.USER);

        return EstadisticasUsuarios.builder()
                .totalUsuarios(totalUsuarios)
                .totalActivos(totalActivos)
                .totalAdministradores(totalAdmins)
                .totalInstructores(totalInstructores)
                .totalUsuariosRegulares(totalUsers)
                .build();
    }

    @lombok.Data
    @lombok.Builder
    public static class EstadisticasUsuarios {
        private Long totalUsuarios;
        private Long totalActivos;
        private Long totalAdministradores;
        private Long totalInstructores;
        private Long totalUsuariosRegulares;
    }
}