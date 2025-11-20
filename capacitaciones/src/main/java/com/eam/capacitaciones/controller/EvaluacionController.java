package com.eam.capacitaciones.controller;

import com.eam.capacitaciones.dto.request.EvaluacionCreateRequest;
import com.eam.capacitaciones.dto.response.ApiResponse;
import com.eam.capacitaciones.dto.response.EvaluacionDTO;
import com.eam.capacitaciones.service.EvaluacionService;
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
@RequestMapping("/evaluaciones")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Evaluaciones", description = "API para gestión de evaluaciones y exámenes")
@SecurityRequirement(name = "Bearer Authentication")
public class EvaluacionController {

    private final EvaluacionService evaluacionService;

    @GetMapping("/modulo/{moduloId}")
    @Operation(summary = "Listar evaluaciones por módulo", description = "Obtiene todas las evaluaciones asociadas a un módulo")
    public ResponseEntity<ApiResponse<List<EvaluacionDTO>>> getEvaluacionesByModulo(
            @Parameter(description = "ID del módulo", required = true)
            @PathVariable Long moduloId) {
        
        log.info("GET /evaluaciones/modulo/{} - Obteniendo evaluaciones", moduloId);
        List<EvaluacionDTO> evaluaciones = evaluacionService.getEvaluacionesByModulo(moduloId);
        return ResponseEntity.ok(ApiResponse.success(evaluaciones));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener evaluación por ID", description = "Obtiene los detalles de una evaluación específica")
    public ResponseEntity<ApiResponse<EvaluacionDTO>> getEvaluacionById(
            @Parameter(description = "ID de la evaluación", required = true)
            @PathVariable Long id) {
        
        log.info("GET /evaluaciones/{} - Obteniendo evaluación", id);
        EvaluacionDTO evaluacion = evaluacionService.getEvaluacionById(id);
        return ResponseEntity.ok(ApiResponse.success(evaluacion));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
    @Operation(summary = "Crear nueva evaluación", description = "Crea una evaluación para un módulo específico")
    public ResponseEntity<ApiResponse<EvaluacionDTO>> createEvaluacion(
            @Valid @RequestBody EvaluacionCreateRequest request) {
        
        log.info("POST /evaluaciones - Creando nueva evaluación: {}", request.getTitulo());
        EvaluacionDTO nuevaEvaluacion = evaluacionService.createEvaluacion(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Evaluación creada exitosamente", nuevaEvaluacion));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
    @Operation(summary = "Eliminar evaluación", description = "Elimina una evaluación del sistema")
    public ResponseEntity<ApiResponse<Void>> deleteEvaluacion(
            @Parameter(description = "ID de la evaluación", required = true)
            @PathVariable Long id) {
        
        log.warn("DELETE /evaluaciones/{} - Eliminando evaluación", id);
        evaluacionService.deleteEvaluacion(id);
        return ResponseEntity.ok(ApiResponse.success("Evaluación eliminada exitosamente", null));
    }
}