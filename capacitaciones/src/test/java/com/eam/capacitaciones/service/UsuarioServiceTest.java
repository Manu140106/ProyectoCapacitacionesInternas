package com.eam.capacitaciones.service;

import com.eam.capacitaciones.domain.entity.Usuario;
import com.eam.capacitaciones.domain.entity.Usuario.RolEnum;
import com.eam.capacitaciones.dto.request.UsuarioCreateRequest;
import com.eam.capacitaciones.dto.request.UsuarioUpdateRequest;
import com.eam.capacitaciones.dto.response.UsuarioDTO;
import com.eam.capacitaciones.exception.BadRequestException;
import com.eam.capacitaciones.mapper.UsuarioMapper;
import com.eam.capacitaciones.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private UsuarioMapper usuarioMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuario;
    private UsuarioDTO usuarioDTO;
    private UsuarioCreateRequest createRequest;
    private UsuarioUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        usuario = Usuario.builder()
                .idUsuario(1L)
                .email("test@example.com")
                .password("encodedPassword")
                .nombre("Test User")
                .rol(RolEnum.USER)
                .departamento("IT")
                .activo(true)
                .build();

        usuarioDTO = UsuarioDTO.builder()
                .idUsuario(1L)
                .email("test@example.com")
                .nombre("Test User")
                .rol(RolEnum.USER)
                .departamento("IT")
                .activo(true)
                .build();

        createRequest = UsuarioCreateRequest.builder()
                .email("test@example.com")
                .password("password123")
                .nombre("Test User")
                .rol(RolEnum.USER)
                .departamento("IT")
                .build();

        updateRequest = UsuarioUpdateRequest.builder()
                .email("updated@example.com")
                .nombre("Updated User")
                .departamento("HR")
                .build();
    }

    @Test
    void getAllUsuarios_ShouldReturnPagedUsuarios() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Usuario> usuarioPage = new PageImpl<>(List.of(usuario));
        when(usuarioRepository.findAll(pageable)).thenReturn(usuarioPage);
        when(usuarioMapper.toDTO(any(Usuario.class))).thenReturn(usuarioDTO);

        // Act
        Page<UsuarioDTO> result = usuarioService.getAllUsuarios(pageable);

        // Assert
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getEmail()).isEqualTo("test@example.com");
        verify(usuarioRepository).findAll(pageable);
        verify(usuarioMapper).toDTO(usuario);
    }

    @Test
    void getUsuarioById_ShouldReturnUsuario_WhenExists() {
        // Arrange
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioMapper.toDTO(usuario)).thenReturn(usuarioDTO);

        // Act
        UsuarioDTO result = usuarioService.getUsuarioById(1L);

        // Assert
        assertThat(result.getIdUsuario()).isEqualTo(1L);
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        verify(usuarioRepository).findById(1L);
        verify(usuarioMapper).toDTO(usuario);
    }

    @Test
    void getUsuarioById_ShouldThrowException_WhenNotExists() {
        // Arrange
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> usuarioService.getUsuarioById(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Usuario no encontrado con ID: 1");
        verify(usuarioRepository).findById(1L);
    }

    @Test
    void createUsuario_ShouldCreateAndReturnUsuario_WhenValid() {
        // Arrange
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);
        when(usuarioMapper.toEntity(createRequest)).thenReturn(usuario);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);
        when(usuarioMapper.toDTO(any(Usuario.class))).thenReturn(usuarioDTO);

        // Act
        UsuarioDTO result = usuarioService.createUsuario(createRequest);

        // Assert
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        assertThat(result.getNombre()).isEqualTo("Test User");
        verify(usuarioRepository).existsByEmail("test@example.com");
        verify(passwordEncoder).encode("password123");
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void createUsuario_ShouldThrowException_WhenEmailExists() {
        // Arrange
        when(usuarioRepository.existsByEmail("test@example.com")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> usuarioService.createUsuario(createRequest))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Ya existe un usuario con el email: test@example.com");
        verify(usuarioRepository).existsByEmail("test@example.com");
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void updateUsuario_ShouldUpdateAndReturnUsuario_WhenValid() {
        // Arrange
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.existsByEmail("updated@example.com")).thenReturn(false);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);
        when(usuarioMapper.toDTO(any(Usuario.class))).thenReturn(usuarioDTO);

        // Act
        UsuarioDTO result = usuarioService.updateUsuario(1L, updateRequest);

        // Assert
        assertThat(result.getIdUsuario()).isEqualTo(1L);
        verify(usuarioRepository).findById(1L);
        verify(usuarioRepository).existsByEmail("updated@example.com");
        verify(usuarioMapper).updateEntityFromRequest(updateRequest, usuario);
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void cambiarPassword_ShouldChangePassword_WhenValid() {
        // Arrange
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.encode("newPassword123")).thenReturn("newEncodedPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        // Act
        usuarioService.cambiarPassword(1L, "newPassword123");

        // Assert
        verify(passwordEncoder).encode("newPassword123");
        verify(usuarioRepository).save(usuario);
        assertThat(usuario.getPassword()).isEqualTo("newEncodedPassword");
    }

    @Test
    void cambiarPassword_ShouldThrowException_WhenPasswordTooShort() {
        // Arrange
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        // Act & Assert
        assertThatThrownBy(() -> usuarioService.cambiarPassword(1L, "short"))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("La contraseña debe tener al menos 8 caracteres");
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void desactivarUsuario_ShouldDeactivateUsuario() {
        // Arrange
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        // Act
        usuarioService.desactivarUsuario(1L);

        // Assert
        assertThat(usuario.getActivo()).isFalse();
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void activarUsuario_ShouldActivateUsuario() {
        // Arrange
        usuario.setActivo(false);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        // Act
        usuarioService.activarUsuario(1L);

        // Assert
        assertThat(usuario.getActivo()).isTrue();
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void deleteUsuario_ShouldDeleteUsuario_WhenExists() {
        // Arrange
        when(usuarioRepository.existsById(1L)).thenReturn(true);

        // Act
        usuarioService.deleteUsuario(1L);

        // Assert
        verify(usuarioRepository).deleteById(1L);
    }

    @Test
    void deleteUsuario_ShouldThrowException_WhenNotExists() {
        // Arrange
        when(usuarioRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> usuarioService.deleteUsuario(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Usuario no encontrado con ID: 1");
        verify(usuarioRepository, never()).deleteById(anyLong());
    }

    @Test
    void existeEmail_ShouldReturnTrue_WhenExists() {
        // Arrange
        when(usuarioRepository.existsByEmail("test@example.com")).thenReturn(true);

        // Act
        boolean result = usuarioService.existeEmail("test@example.com");

        // Assert
        assertThat(result).isTrue();
        verify(usuarioRepository).existsByEmail("test@example.com");
    }

    @Test
    void contarUsuariosPorRol_ShouldReturnCount() {
        // Arrange
        when(usuarioRepository.countByRol(RolEnum.USER)).thenReturn(5L);

        // Act
        Long result = usuarioService.contarUsuariosPorRol(RolEnum.USER);

        // Assert
        assertThat(result).isEqualTo(5L);
        verify(usuarioRepository).countByRol(RolEnum.USER);
    }

    @Test
    void getEstadisticas_ShouldReturnStatistics() {
        // Arrange
        when(usuarioRepository.count()).thenReturn(100L);
        when(usuarioRepository.findByActivoTrue()).thenReturn(List.of(usuario, usuario));
        when(usuarioRepository.countByRol(RolEnum.ADMIN)).thenReturn(10L);
        when(usuarioRepository.countByRol(RolEnum.INSTRUCTOR)).thenReturn(20L);
        when(usuarioRepository.countByRol(RolEnum.USER)).thenReturn(70L);

        // Act
        UsuarioService.EstadisticasUsuarios result = usuarioService.getEstadisticas();

        // Assert
        assertThat(result.getTotalUsuarios()).isEqualTo(100L);
        assertThat(result.getTotalActivos()).isEqualTo(2L);
        assertThat(result.getTotalAdministradores()).isEqualTo(10L);
        assertThat(result.getTotalInstructores()).isEqualTo(20L);
        assertThat(result.getTotalUsuariosRegulares()).isEqualTo(70L);
    }
}
