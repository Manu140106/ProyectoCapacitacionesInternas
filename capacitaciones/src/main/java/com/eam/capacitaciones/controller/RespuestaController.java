package com.eam.capacitaciones.controller;

import com.eam.capacitaciones.dto.request.RespuestaCalificarRequest;
import com.eam.capacitaciones.dto.request.RespuestaSubmitRequest;
import com.eam.capacitaciones.dto.response.ApiResponse;
import com.eam.capacitaciones.dto.response.RespuestaDTO;
import com.eam.capacitaciones.security.CustomUserDetails;
import com.eam.capacitaciones.service.RespuestaService;
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

import java.util.List;

@RestController
@RequestMapping("/respuestas")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Respuestas", description = "API para gestión de respuestas a evaluaciones")
@SecurityRequirement(name = "Bearer Authentication")
public class RespuestaController {

    private final RespuestaService respuestaService;

    @GetMapping("/evaluacion/{evaluacionId}/usuario/{usuarioId}")
    @Operation(summary = "Obtener respuestas de un usuario", description = "Lista todas las respuestas de un usuario en una evaluación específica")
    public ResponseEntity<ApiResponse<List<RespuestaDTO>>> getRespuestasByEvaluacionAndUsuario(
            @Parameter(description = "ID de la evaluación", required = true)
            @PathVariable Long evaluacionId,
            @Parameter(description = "ID del usuario", required = true)
            @PathVariable Long usuarioId) {
        
        log.info("GET /respuestas/evaluacion/{}/usuario/{}", evaluacionId, usuarioId);
        List<RespuestaDTO> respuestas = respuestaService.getRespuestasByEvaluacionAndUsuario(evaluacionId, usuarioId);
        return ResponseEntity.ok(ApiResponse.success(respuestas));
    }

    @PostMapping
    @Operation(summary = "Enviar respuesta", description = "Envía una respuesta del usuario autenticado a una evaluación")
    public ResponseEntity<ApiResponse<RespuestaDTO>> submitRespuesta(
            @Valid @RequestBody RespuestaSubmitRequest request,
            Authentication authentication) {
        
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        log.info("POST /respuestas - Usuario {} enviando respuesta a evaluación {}", 
                userDetails.getId(), request.getEvaluacionId());
        
        RespuestaDTO respuesta = respuestaService.submitRespuesta(userDetails.getId(), request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Respuesta enviada exitosamente", respuesta));
    }

    @PatchMapping("/{id}/calificar")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
    @Operation(summary = "Calificar respuesta", description = "Califica una respuesta de tipo abierta")
    public ResponseEntity<ApiResponse<RespuestaDTO>> calificarRespuesta(
            @Parameter(description = "ID de la respuesta", required = true)
            @PathVariable Long id,
            @Valid @RequestBody RespuestaCalificarRequest request) {
        
        log.info("PATCH /respuestas/{}/calificar - Calificando respuesta", id);
        RespuestaDTO respuesta = respuestaService.calificarRespuesta(id, request);
        return ResponseEntity.ok(ApiResponse.success("Respuesta calificada exitosamente", respuesta));
    }

    @GetMapping("/evaluacion/{evaluacionId}/pendientes")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
    @Operation(summary = "Obtener respuestas pendientes de calificación")
    public ResponseEntity<ApiResponse<List<RespuestaDTO>>> getRespuestasPendientes(
            @PathVariable Long evaluacionId) {
        
        log.info("GET /respuestas/evaluacion/{}/pendientes", evaluacionId);
        List<RespuestaDTO> respuestas = respuestaService.getRespuestasPendientesCalificacion(evaluacionId);
        return ResponseEntity.ok(ApiResponse.success(respuestas));
    }
}