package com.eam.capacitaciones.controller;

import com.eam.capacitaciones.dto.request.UsuarioCreateRequest;
import com.eam.capacitaciones.dto.request.UsuarioUpdateRequest;
import com.eam.capacitaciones.dto.response.ApiResponse;
import com.eam.capacitaciones.dto.response.UsuarioDTO;
import com.eam.capacitaciones.domain.entity.Usuario.RolEnum;
import com.eam.capacitaciones.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Usuarios", description = "API para gestión de usuarios del sistema")
@SecurityRequirement(name = "Bearer Authentication")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Listar todos los usuarios",
        description = "Obtiene lista paginada de usuarios. Solo accesible por ADMIN."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Sin permisos de administrador"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<Page<UsuarioDTO>> getAllUsuarios(
            @PageableDefault(size = 20, sort = "nombre") Pageable pageable) {
        
        log.info("GET /usuarios - Obteniendo lista de usuarios (página: {})", pageable.getPageNumber());
        Page<UsuarioDTO> usuarios = usuarioService.getAllUsuarios(pageable);
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    @Operation(
        summary = "Obtener usuario por ID",
        description = "Obtiene los datos de un usuario específico. ADMIN puede ver cualquier usuario, usuarios regulares solo su propio perfil."
    )
    public ResponseEntity<ApiResponse<UsuarioDTO>> getUsuarioById(
            @Parameter(description = "ID del usuario", required = true)
            @PathVariable Long id) {
        
        log.info("GET /usuarios/{} - Obteniendo usuario", id);
        UsuarioDTO usuario = usuarioService.getUsuarioById(id);
        return ResponseEntity.ok(ApiResponse.success(usuario));
    }

    @GetMapping("/rol/{rol}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Obtener usuarios por rol", description = "Filtra usuarios por rol (ADMIN, INSTRUCTOR, USER)")
    public ResponseEntity<ApiResponse<List<UsuarioDTO>>> getUsuariosByRol(
            @Parameter(description = "Rol a filtrar", required = true)
            @PathVariable RolEnum rol) {
        
        log.info("GET /usuarios/rol/{} - Obteniendo usuarios por rol", rol);
        List<UsuarioDTO> usuarios = usuarioService.getUsuariosByRol(rol);
        return ResponseEntity.ok(ApiResponse.success(usuarios));
    }

    @GetMapping("/departamento/{departamento}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('INSTRUCTOR')")
    @Operation(summary = "Obtener usuarios por departamento")
    public ResponseEntity<ApiResponse<List<UsuarioDTO>>> getUsuariosByDepartamento(
            @PathVariable String departamento) {
        
        log.info("GET /usuarios/departamento/{} - Obteniendo usuarios", departamento);
        List<UsuarioDTO> usuarios = usuarioService.getUsuariosByDepartamento(departamento);
        return ResponseEntity.ok(ApiResponse.success(usuarios));
    }

    @GetMapping("/activos")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Obtener usuarios activos", description = "Lista todos los usuarios con estado activo=true")
    public ResponseEntity<ApiResponse<List<UsuarioDTO>>> getUsuariosActivos() {
        log.info("GET /usuarios/activos - Obteniendo usuarios activos");
        List<UsuarioDTO> usuarios = usuarioService.getUsuariosActivos();
        return ResponseEntity.ok(ApiResponse.success(usuarios));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Crear nuevo usuario",
        description = "Registra un nuevo usuario en el sistema. La contraseña se encripta automáticamente."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Usuario creado exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos inválidos o email duplicado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Sin permisos de administrador")
    })
    public ResponseEntity<ApiResponse<UsuarioDTO>> createUsuario(
            @Valid @RequestBody UsuarioCreateRequest request) {
        
        log.info("POST /usuarios - Creando nuevo usuario: {}", request.getEmail());
        UsuarioDTO nuevoUsuario = usuarioService.createUsuario(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Usuario creado exitosamente", nuevoUsuario));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    @Operation(
        summary = "Actualizar usuario",
        description = "Actualiza los datos de un usuario. Solo se actualizan los campos proporcionados (no nulos)."
    )
    public ResponseEntity<ApiResponse<UsuarioDTO>> updateUsuario(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioUpdateRequest request) {
        
        log.info("PUT /usuarios/{} - Actualizando usuario", id);
        UsuarioDTO usuarioActualizado = usuarioService.updateUsuario(id, request);
        return ResponseEntity.ok(ApiResponse.success("Usuario actualizado exitosamente", usuarioActualizado));
    }

    @PatchMapping("/{id}/cambiar-password")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    @Operation(summary = "Cambiar contraseña", description = "Actualiza la contraseña de un usuario")
    public ResponseEntity<ApiResponse<Void>> cambiarPassword(
            @PathVariable Long id,
            @RequestBody ChangePasswordRequest request) {
        
        log.info("PATCH /usuarios/{}/cambiar-password - Cambiando contraseña", id);
        usuarioService.cambiarPassword(id, request.getNuevaPassword());
        return ResponseEntity.ok(ApiResponse.success("Contraseña cambiada exitosamente", null));
    }

    @PatchMapping("/{id}/desactivar")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Desactivar usuario", description = "Marca un usuario como inactivo sin eliminarlo de la BD")
    public ResponseEntity<ApiResponse<Void>> desactivarUsuario(@PathVariable Long id) {
        log.info("PATCH /usuarios/{}/desactivar - Desactivando usuario", id);
        usuarioService.desactivarUsuario(id);
        return ResponseEntity.ok(ApiResponse.success("Usuario desactivado exitosamente", null));
    }

    @PatchMapping("/{id}/activar")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Activar usuario", description = "Marca un usuario como activo")
    public ResponseEntity<ApiResponse<Void>> activarUsuario(@PathVariable Long id) {
        log.info("PATCH /usuarios/{}/activar - Activando usuario", id);
        usuarioService.activarUsuario(id);
        return ResponseEntity.ok(ApiResponse.success("Usuario activado exitosamente", null));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Eliminar usuario permanentemente",
        description = "PRECAUCIÓN: Elimina físicamente el usuario de la BD. Operación irreversible."
    )
    public ResponseEntity<ApiResponse<Void>> deleteUsuario(@PathVariable Long id) {
        log.warn("DELETE /usuarios/{} - Eliminando usuario PERMANENTEMENTE", id);
        usuarioService.deleteUsuario(id);
        return ResponseEntity.ok(ApiResponse.success("Usuario eliminado permanentemente", null));
    }

    @GetMapping("/existe-email")
    @Operation(summary = "Verificar si email existe", description = "Verifica si un email ya está registrado")
    public ResponseEntity<ApiResponse<Boolean>> existeEmail(@RequestParam String email) {
        log.debug("GET /usuarios/existe-email?email={}", email);
        boolean existe = usuarioService.existeEmail(email);
        return ResponseEntity.ok(ApiResponse.success(existe));
    }

    @GetMapping("/estadisticas")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Obtener estadísticas de usuarios", description = "Devuelve conteo de usuarios por rol y estado")
    public ResponseEntity<ApiResponse<UsuarioService.EstadisticasUsuarios>> getEstadisticas() {
        log.info("GET /usuarios/estadisticas - Obteniendo estadísticas");
        UsuarioService.EstadisticasUsuarios stats = usuarioService.getEstadisticas();
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    @lombok.Data
    private static class ChangePasswordRequest {
        @jakarta.validation.constraints.NotBlank
        @jakarta.validation.constraints.Size(min = 8)
        private String nuevaPassword;
    }
}