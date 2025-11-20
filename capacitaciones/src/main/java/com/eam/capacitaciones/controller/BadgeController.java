package com.eam.capacitaciones.controller;

import com.eam.capacitaciones.dto.request.BadgeCreateRequest;
import com.eam.capacitaciones.dto.response.ApiResponse;
import com.eam.capacitaciones.dto.response.BadgeDTO;
import com.eam.capacitaciones.service.BadgeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/badges")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Badges", description = "Sistema de gamificación y recompensas")
@SecurityRequirement(name = "Bearer Authentication")
public class BadgeController {

    private final BadgeService badgeService;

    @GetMapping
    @Operation(summary = "Listar todos los badges", description = "Obtiene todos los badges disponibles en el sistema")
    public ResponseEntity<ApiResponse<List<BadgeDTO>>> getAllBadges() {
        log.info("GET /badges - Obteniendo todos los badges");
        List<BadgeDTO> badges = badgeService.getAllBadges();
        return ResponseEntity.ok(ApiResponse.success(badges));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener badge por ID")
    public ResponseEntity<ApiResponse<BadgeDTO>> getBadgeById(
            @Parameter(description = "ID del badge", required = true)
            @PathVariable Long id) {
        
        log.info("GET /badges/{} - Obteniendo badge", id);
        BadgeDTO badge = badgeService.getBadgeById(id);
        return ResponseEntity.ok(ApiResponse.success(badge));
    }

    @GetMapping("/usuario/{usuarioId}")
    @Operation(summary = "Listar badges de un usuario", description = "Obtiene todos los badges obtenidos por un usuario específico")
    public ResponseEntity<ApiResponse<List<BadgeDTO>>> getBadgesByUsuario(
            @Parameter(description = "ID del usuario", required = true)
            @PathVariable Long usuarioId) {
        
        log.info("GET /badges/usuario/{} - Obteniendo badges del usuario", usuarioId);
        List<BadgeDTO> badges = badgeService.getBadgesByUsuario(usuarioId);
        return ResponseEntity.ok(ApiResponse.success(badges));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear nuevo badge", description = "Crea un nuevo badge en el sistema")
    public ResponseEntity<ApiResponse<BadgeDTO>> createBadge(
            @Valid @RequestBody BadgeCreateRequest request) {
        
        log.info("POST /badges - Creando nuevo badge: {}", request.getNombre());
        BadgeDTO nuevoBadge = badgeService.createBadge(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Badge creado exitosamente", nuevoBadge));
    }

    @PostMapping("/asignar")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Asignar badge a usuario", description = "Otorga un badge específico a un usuario")
    public ResponseEntity<ApiResponse<Void>> asignarBadge(
            @Parameter(description = "ID del usuario", required = true)
            @RequestParam Long usuarioId,
            @Parameter(description = "ID del badge", required = true)
            @RequestParam Long badgeId) {
        
        log.info("POST /badges/asignar - Asignando badge {} a usuario {}", badgeId, usuarioId);
        badgeService.asignarBadgeAUsuario(usuarioId, badgeId);
        return ResponseEntity.ok(ApiResponse.success("Badge asignado exitosamente", null));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar badge", description = "Elimina un badge del sistema")
    public ResponseEntity<ApiResponse<Void>> deleteBadge(
            @Parameter(description = "ID del badge", required = true)
            @PathVariable Long id) {
        
        log.warn("DELETE /badges/{} - Eliminando badge", id);
        badgeService.deleteBadge(id);
        return ResponseEntity.ok(ApiResponse.success("Badge eliminado exitosamente", null));
    }
}