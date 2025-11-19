package com.eam.capacitaciones.service;


import com.eam.capacitaciones.dto.request.LoginRequest;
import com.eam.capacitaciones.dto.request.UsuarioCreateRequest;
import com.eam.capacitaciones.dto.response.LoginResponse;
import com.eam.capacitaciones.dto.response.UsuarioDTO;
import com.eam.capacitaciones.domain.entity.Usuario;
import com.eam.capacitaciones.exception.BadRequestException;
import com.eam.capacitaciones.exception.UnauthorizedException;
import com.eam.capacitaciones.mapper.UsuarioMapper;
import com.eam.capacitaciones.repository.UsuarioRepository;
import com.eam.capacitaciones.security.CustomUserDetails;
import com.eam.capacitaciones.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest loginRequest) {
        log.info("Intento de login para usuario: {}", loginRequest.getEmail());

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            String accessToken = jwtTokenProvider.generateToken(authentication);
            String refreshToken = jwtTokenProvider.generateRefreshToken(userDetails.getId());

            log.info("Login exitoso para usuario: {} (ID: {})", userDetails.getEmail(), userDetails.getId());

            Usuario usuario = usuarioRepository.findById(userDetails.getId())
                    .orElseThrow(() -> new UnauthorizedException("Usuario no encontrado"));

            UsuarioDTO usuarioDTO = usuarioMapper.toDTO(usuario);

            return LoginResponse.builder()
                    .token(accessToken)
                    .refreshToken(refreshToken)
                    .user(usuarioDTO)
                    .build();

        } catch (BadCredentialsException ex) {
            log.error("Credenciales inválidas para: {}", loginRequest.getEmail());
            throw new UnauthorizedException("Email o contraseña incorrectos");
        }
    }

    @Transactional
    public LoginResponse register(UsuarioCreateRequest request) {
        log.info("Intento de registro para usuario: {}", request.getEmail());

        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Ya existe un usuario con el email: " + request.getEmail());
        }

        if (request.getPassword().length() < 8) {
            throw new BadRequestException("La contraseña debe tener al menos 8 caracteres");
        }

        Usuario usuario = usuarioMapper.toEntity(request);
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setActivo(true);

        if (request.getRol() == null) {
            usuario.setRol(Usuario.RolEnum.USER);
        }

        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        log.info("Usuario registrado exitosamente: {} (ID: {})", 
                usuarioGuardado.getEmail(), usuarioGuardado.getIdUsuario());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = jwtTokenProvider.generateToken(authentication);
        String refreshToken = jwtTokenProvider.generateRefreshToken(usuarioGuardado.getIdUsuario());

        UsuarioDTO usuarioDTO = usuarioMapper.toDTO(usuarioGuardado);

        return LoginResponse.builder()
                .token(accessToken)
                .refreshToken(refreshToken)
                .user(usuarioDTO)
                .build();
    }

    @Transactional(readOnly = true)
    public String refreshAccessToken(String refreshToken) {
        log.info("Renovando access token");

        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new UnauthorizedException("Refresh token inválido o expirado");
        }

        Long userId = jwtTokenProvider.getUserIdFromToken(refreshToken);

        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException("Usuario no encontrado"));

        if (!usuario.getActivo()) {
            throw new UnauthorizedException("Usuario inactivo");
        }

        CustomUserDetails userDetails = CustomUserDetails.build(usuario);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );

        String newAccessToken = jwtTokenProvider.generateToken(authentication);

        log.info("Access token renovado para usuario ID: {}", userId);

        return newAccessToken;
    }

    public void logout() {
        SecurityContextHolder.clearContext();
        log.info("Usuario desconectado");
    }

    @Transactional(readOnly = true)
    public UsuarioDTO getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("No hay usuario autenticado");
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        
        Usuario usuario = usuarioRepository.findById(userDetails.getId())
                .orElseThrow(() -> new UnauthorizedException("Usuario no encontrado"));

        return usuarioMapper.toDTO(usuario);
    }

    @Transactional
    public void changePassword(String oldPassword, String newPassword) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        Usuario usuario = usuarioRepository.findById(userDetails.getId())
                .orElseThrow(() -> new UnauthorizedException("Usuario no encontrado"));

        if (!passwordEncoder.matches(oldPassword, usuario.getPassword())) {
            throw new BadRequestException("La contraseña actual es incorrecta");
        }

        if (newPassword.length() < 8) {
            throw new BadRequestException("La nueva contraseña debe tener al menos 8 caracteres");
        }
        if (newPassword.length() < 8) {
            throw new BadRequestException("La nueva contraseña debe tener al menos 8 caracteres");
        }

        usuario.setPassword(passwordEncoder.encode(newPassword));
        usuarioRepository.save(usuario);

        log.info("Contraseña cambiada para usuario ID: {}", usuario.getIdUsuario());
    }
}