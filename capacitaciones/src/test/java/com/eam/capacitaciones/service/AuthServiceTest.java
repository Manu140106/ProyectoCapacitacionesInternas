package com.eam.capacitaciones.service;

import com.eam.capacitaciones.domain.entity.Usuario;
import com.eam.capacitaciones.domain.entity.Usuario.RolEnum;
import com.eam.capacitaciones.dto.request.LoginRequest;
import com.eam.capacitaciones.dto.request.UsuarioCreateRequest;
import com.eam.capacitaciones.dto.response.LoginResponse;
import com.eam.capacitaciones.dto.response.UsuarioDTO;
import com.eam.capacitaciones.exception.BadRequestException;
import com.eam.capacitaciones.exception.UnauthorizedException;
import com.eam.capacitaciones.mapper.UsuarioMapper;
import com.eam.capacitaciones.repository.UsuarioRepository;
import com.eam.capacitaciones.security.CustomUserDetails;
import com.eam.capacitaciones.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private UsuarioMapper usuarioMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private AuthService authService;

    private Usuario usuario;
    private UsuarioDTO usuarioDTO;
    private LoginRequest loginRequest;
    private UsuarioCreateRequest createRequest;
    private CustomUserDetails userDetails;
    private Authentication authentication;

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

        loginRequest = LoginRequest.builder()
                .email("test@example.com")
                .password("password123")
                .build();

        createRequest = UsuarioCreateRequest.builder()
                .email("test@example.com")
                .password("password123")
                .nombre("Test User")
                .rol(RolEnum.USER)
                .departamento("IT")
                .build();

        userDetails = CustomUserDetails.build(usuario);
        authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    @Test
    void login_ShouldReturnLoginResponse_WhenCredentialsValid() {

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtTokenProvider.generateToken(authentication)).thenReturn("accessToken");
        when(jwtTokenProvider.generateRefreshToken(1L)).thenReturn("refreshToken");
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioMapper.toDTO(usuario)).thenReturn(usuarioDTO);

        LoginResponse result = authService.login(loginRequest);

        assertThat(result.getToken()).isEqualTo("accessToken");
        assertThat(result.getRefreshToken()).isEqualTo("refreshToken");
        assertThat(result.getUser().getEmail()).isEqualTo("test@example.com");
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtTokenProvider).generateToken(authentication);
        verify(jwtTokenProvider).generateRefreshToken(1L);
    }

    @Test
    void login_ShouldThrowUnauthorizedException_WhenCredentialsInvalid() {

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Email o contraseña incorrectos");
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void register_ShouldReturnLoginResponse_WhenValid() {

        when(usuarioRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(usuarioMapper.toEntity(createRequest)).thenReturn(usuario);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtTokenProvider.generateToken(authentication)).thenReturn("accessToken");
        when(jwtTokenProvider.generateRefreshToken(1L)).thenReturn("refreshToken");
        when(usuarioMapper.toDTO(any(Usuario.class))).thenReturn(usuarioDTO);

        LoginResponse result = authService.register(createRequest);

        assertThat(result.getToken()).isEqualTo("accessToken");
        assertThat(result.getRefreshToken()).isEqualTo("refreshToken");
        assertThat(result.getUser().getEmail()).isEqualTo("test@example.com");
        verify(usuarioRepository).existsByEmail("test@example.com");
        verify(passwordEncoder).encode("password123");
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void register_ShouldThrowBadRequestException_WhenEmailExists() {

        when(usuarioRepository.existsByEmail("test@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(createRequest))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Ya existe un usuario con el email: test@example.com");
        verify(usuarioRepository).existsByEmail("test@example.com");
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void register_ShouldThrowBadRequestException_WhenPasswordTooShort() {

        createRequest.setPassword("short");
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);

        assertThatThrownBy(() -> authService.register(createRequest))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("La contraseña debe tener al menos 8 caracteres");
        verify(usuarioRepository).existsByEmail(anyString());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void refreshAccessToken_ShouldReturnNewToken_WhenValidRefreshToken() {

        when(jwtTokenProvider.validateToken("refreshToken")).thenReturn(true);
        when(jwtTokenProvider.getUserIdFromToken("refreshToken")).thenReturn(1L);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(jwtTokenProvider.generateToken(any(Authentication.class))).thenReturn("newAccessToken");

        String result = authService.refreshAccessToken("refreshToken");

        assertThat(result).isEqualTo("newAccessToken");
        verify(jwtTokenProvider).validateToken("refreshToken");
        verify(jwtTokenProvider).getUserIdFromToken("refreshToken");
        verify(jwtTokenProvider).generateToken(any(Authentication.class));
    }

    @Test
    void refreshAccessToken_ShouldThrowUnauthorizedException_WhenInvalidRefreshToken() {

        when(jwtTokenProvider.validateToken("invalidToken")).thenReturn(false);

        assertThatThrownBy(() -> authService.refreshAccessToken("invalidToken"))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Refresh token inválido o expirado");
        verify(jwtTokenProvider).validateToken("invalidToken");
    }

    @Test
    void refreshAccessToken_ShouldThrowUnauthorizedException_WhenUserInactive() {

        usuario.setActivo(false);
        when(jwtTokenProvider.validateToken("refreshToken")).thenReturn(true);
        when(jwtTokenProvider.getUserIdFromToken("refreshToken")).thenReturn(1L);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));


        assertThatThrownBy(() -> authService.refreshAccessToken("refreshToken"))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Usuario inactivo");
        verify(jwtTokenProvider).validateToken("refreshToken");
        verify(jwtTokenProvider).getUserIdFromToken("refreshToken");
    }

    @Test
    void logout_ShouldClearSecurityContext() {

        authService.logout();

        verify(securityContext, never()).setAuthentication(null); // SecurityContextHolder.clearContext() is called
    }

    @Test
    void getCurrentUser_ShouldReturnUsuarioDTO_WhenAuthenticated() {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioMapper.toDTO(usuario)).thenReturn(usuarioDTO);

        UsuarioDTO result = authService.getCurrentUser();

        assertThat(result.getEmail()).isEqualTo("test@example.com");
        verify(securityContext).getAuthentication();
        verify(usuarioRepository).findById(1L);
        verify(usuarioMapper).toDTO(usuario);
    }

    @Test
    void getCurrentUser_ShouldThrowUnauthorizedException_WhenNotAuthenticated() {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(null);

        assertThatThrownBy(() -> authService.getCurrentUser())
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("No hay usuario autenticado");
        verify(securityContext).getAuthentication();
    }

    @Test
    void changePassword_ShouldChangePassword_WhenValid() {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("oldPassword", "encodedPassword")).thenReturn(true);
        when(passwordEncoder.encode("newPassword123")).thenReturn("newEncodedPassword");

        authService.changePassword("oldPassword", "newPassword123");

        verify(passwordEncoder).matches("oldPassword", "encodedPassword");
        verify(passwordEncoder).encode("newPassword123");
        verify(usuarioRepository).save(usuario);
        assertThat(usuario.getPassword()).isEqualTo("newEncodedPassword");
    }

    @Test
    void changePassword_ShouldThrowBadRequestException_WhenOldPasswordIncorrect() {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("wrongOldPassword", "encodedPassword")).thenReturn(false);

        assertThatThrownBy(() -> authService.changePassword("wrongOldPassword", "newPassword123"))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("La contraseña actual es incorrecta");
        verify(passwordEncoder).matches("wrongOldPassword", "encodedPassword");
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void changePassword_ShouldThrowBadRequestException_WhenNewPasswordTooShort() {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("oldPassword", "encodedPassword")).thenReturn(true);

        assertThatThrownBy(() -> authService.changePassword("oldPassword", "short"))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("La nueva contraseña debe tener al menos 8 caracteres");
        verify(passwordEncoder).matches("oldPassword", "encodedPassword");
        verify(passwordEncoder, never()).encode(anyString());
    }
}