package com.eam.capacitaciones.controller;

import com.eam.capacitaciones.dto.request.InscripcionCreateRequest;
import com.eam.capacitaciones.dto.response.ApiResponse;
import com.eam.capacitaciones.dto.response.InscripcionDTO;
import com.eam.capacitaciones.security.CustomUserDetails;
import com.eam.capacitaciones.service.InscripcionService;
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
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/inscripciones")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Inscripciones", description = "API para gestión de inscripciones a cursos")
@SecurityRequirement(name = "Bearer Authentication")
public class InscripcionController {

    private final InscripcionService inscripcionService;

    @GetMapping("/usuario/{usuarioId}")
    @Operation(summary = "Listar inscripciones por usuario", description = "Obtiene todas las inscripciones de un usuario específico")
    public ResponseEntity<ApiResponse<List<InscripcionDTO>>> getInscripcionesByUsuario(
            @Parameter(description = "ID del usuario", required = true)
            @PathVariable Long usuarioId) {
        
        log.info("GET /inscripciones/usuario/{} - Obteniendo inscripciones", usuarioId);
        List<InscripcionDTO> inscripciones = inscripcionService.getInscripcionesByUsuario(usuarioId);
        return ResponseEntity.ok(ApiResponse.success(inscripciones));
    }

    @GetMapping("/curso/{cursoId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
    @Operation(summary = "Listar inscripciones por curso", description = "Obtiene todas las inscripciones de un curso específico")
    public ResponseEntity<ApiResponse<List<InscripcionDTO>>> getInscripcionesByCurso(
            @Parameter(description = "ID del curso", required = true)
            @PathVariable Long cursoId) {
        
        log.info("GET /inscripciones/curso/{} - Obteniendo inscripciones", cursoId);
        List<InscripcionDTO> inscripciones = inscripcionService.getInscripcionesByCurso(cursoId);
        return ResponseEntity.ok(ApiResponse.success(inscripciones));
    }

    @PostMapping
    @Operation(summary = "Crear nueva inscripción", description = "Inscribe al usuario autenticado en un curso")
    public ResponseEntity<ApiResponse<InscripcionDTO>> createInscripcion(
            @Valid @RequestBody InscripcionCreateRequest request,
            Authentication authentication) {
        
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        log.info("POST /inscripciones - Inscribiendo usuario {} en curso {}", 
                userDetails.getId(), request.getCursoId());
        
        InscripcionDTO nuevaInscripcion = inscripcionService.inscribirUsuarioEnCurso(
                userDetails.getId(), request);
        
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Inscripción creada exitosamente", nuevaInscripcion));
    }

    @PatchMapping("/{id}/progreso")
    @Operation(summary = "Actualizar progreso", description = "Actualiza el porcentaje de avance del curso (0-100)")
    public ResponseEntity<ApiResponse<Void>> actualizarProgreso(
            @Parameter(description = "ID de la inscripción", required = true)
            @PathVariable Long id,
            @Parameter(description = "Nuevo progreso (0-100)", required = true)
            @RequestParam BigDecimal progreso) {
        
        log.info("PATCH /inscripciones/{}/progreso - Actualizando a {}%", id, progreso);
        inscripcionService.actualizarProgreso(id, progreso);
        return ResponseEntity.ok(ApiResponse.success("Progreso actualizado exitosamente", null));
    }
}