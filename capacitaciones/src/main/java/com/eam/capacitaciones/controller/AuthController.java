package com.eam.capacitaciones.controller;


import com.eam.capacitaciones.dto.request.LoginRequest;
import com.eam.capacitaciones.dto.request.UsuarioCreateRequest;
import com.eam.capacitaciones.dto.response.ApiResponse;
import com.eam.capacitaciones.dto.response.LoginResponse;
import com.eam.capacitaciones.dto.response.UsuarioDTO;
import com.eam.capacitaciones.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Autenticación", description = "Endpoints para login, registro y gestión de tokens JWT")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(
        summary = "Iniciar sesión",
        description = "Autentica un usuario y devuelve un token JWT válido por 15 minutos y un refresh token válido por 7 días"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Login exitoso"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Credenciales inválidas")
    })
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("POST /auth/login - Usuario: {}", loginRequest.getEmail());
        
        LoginResponse loginResponse = authService.login(loginRequest);
        
        return ResponseEntity.ok(ApiResponse.success("Login exitoso", loginResponse));
    }

    @PostMapping("/register")
    @Operation(
        summary = "Registrar nuevo usuario",
        description = "Registra un nuevo usuario en el sistema. Por defecto se asigna rol USER. " +
                     "Después del registro, el usuario queda automáticamente autenticado."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Registro exitoso"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Email duplicado o datos inválidos")
    })
    public ResponseEntity<ApiResponse<LoginResponse>> register(
            @Valid @RequestBody UsuarioCreateRequest request) {
        log.info("POST /auth/register - Email: {}", request.getEmail());
        
        LoginResponse loginResponse = authService.register(request);
        
        return ResponseEntity.ok(ApiResponse.success("Usuario registrado exitosamente", loginResponse));
    }

    @PostMapping("/refresh")
    @Operation(
        summary = "Renovar access token",
        description = "Genera un nuevo access token usando un refresh token válido. " +
                     "El refresh token no se renueva, solo el access token."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Token renovado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Refresh token inválido o expirado")
    })
    public ResponseEntity<ApiResponse<TokenRefreshResponse>> refreshToken(
            @RequestBody RefreshTokenRequest request) {
        log.info("POST /auth/refresh");
        
        String newAccessToken = authService.refreshAccessToken(request.getRefreshToken());
        
        TokenRefreshResponse response = new TokenRefreshResponse(newAccessToken);
        
        return ResponseEntity.ok(ApiResponse.success("Token renovado exitosamente", response));
    }

    @PostMapping("/logout")
    @Operation(
        summary = "Cerrar sesión",
        description = "Cierra la sesión del usuario actual. " +
                     "Nota: Con JWT stateless, esto solo limpia el contexto del servidor. " +
                     "El token seguirá siendo válido hasta su expiración."
    )
    public ResponseEntity<ApiResponse<Void>> logout() {
        log.info("POST /auth/logout");
        
        authService.logout();
        
        return ResponseEntity.ok(ApiResponse.success("Sesión cerrada exitosamente", null));
    }

    @GetMapping("/me")
    @Operation(
        summary = "Obtener usuario actual",
        description = "Devuelve los datos del usuario autenticado basándose en el token JWT"
    )
    public ResponseEntity<ApiResponse<UsuarioDTO>> getCurrentUser() {
        log.info("GET /auth/me");
        
        UsuarioDTO usuario = authService.getCurrentUser();
        
        return ResponseEntity.ok(ApiResponse.success(usuario));
    }

    @PostMapping("/change-password")
    @Operation(
        summary = "Cambiar contraseña",
        description = "Cambia la contraseña del usuario autenticado. Requiere proporcionar la contraseña actual."
    )
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request) {
        log.info("POST /auth/change-password");
        
        authService.changePassword(request.getOldPassword(), request.getNewPassword());
        
        return ResponseEntity.ok(ApiResponse.success("Contraseña cambiada exitosamente", null));
    }

    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    private static class RefreshTokenRequest {
        @jakarta.validation.constraints.NotBlank(message = "El refresh token es obligatorio")
        private String refreshToken;
    }

    @lombok.Data
    @lombok.AllArgsConstructor
    private static class TokenRefreshResponse {
        private String accessToken;
    }

    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    private static class ChangePasswordRequest {
        @jakarta.validation.constraints.NotBlank(message = "La contraseña actual es obligatoria")
        private String oldPassword;

        @jakarta.validation.constraints.NotBlank(message = "La nueva contraseña es obligatoria")
        @jakarta.validation.constraints.Size(min = 8, message = "La nueva contraseña debe tener al menos 8 caracteres")
        private String newPassword;
    }
}