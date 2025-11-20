package com.eam.capacitaciones.controller;

import com.eam.capacitaciones.dto.request.ModuloCreateRequest;
import com.eam.capacitaciones.dto.response.ApiResponse;
import com.eam.capacitaciones.dto.response.ModuloDTO;
import com.eam.capacitaciones.service.ModuloService;
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
@RequestMapping("/modulos")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Módulos", description = "API para gestión de módulos de cursos")
@SecurityRequirement(name = "Bearer Authentication")
public class ModuloController {

    private final ModuloService moduloService;

    @GetMapping("/curso/{cursoId}")
    @Operation(summary = "Listar módulos por curso", description = "Obtiene todos los módulos de un curso ordenados por su posición")
    public ResponseEntity<ApiResponse<List<ModuloDTO>>> getModulosByCurso(
            @Parameter(description = "ID del curso", required = true)
            @PathVariable Long cursoId) {
        
        log.info("GET /modulos/curso/{} - Obteniendo módulos", cursoId);
        List<ModuloDTO> modulos = moduloService.getModulosByCurso(cursoId);
        return ResponseEntity.ok(ApiResponse.success(modulos));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener módulo por ID", description = "Obtiene los detalles completos de un módulo específico")
    public ResponseEntity<ApiResponse<ModuloDTO>> getModuloById(
            @Parameter(description = "ID del módulo", required = true)
            @PathVariable Long id) {
        
        log.info("GET /modulos/{} - Obteniendo módulo", id);
        ModuloDTO modulo = moduloService.getModuloById(id);
        return ResponseEntity.ok(ApiResponse.success(modulo));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
    @Operation(summary = "Crear nuevo módulo", description = "Añade un módulo a un curso existente")
    public ResponseEntity<ApiResponse<ModuloDTO>> createModulo(
            @Valid @RequestBody ModuloCreateRequest request) {
        
        log.info("POST /modulos - Creando nuevo módulo: {}", request.getTitulo());
        ModuloDTO nuevoModulo = moduloService.createModulo(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Módulo creado exitosamente", nuevoModulo));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
    @Operation(summary = "Eliminar módulo", description = "Elimina un módulo del curso")
    public ResponseEntity<ApiResponse<Void>> deleteModulo(
            @Parameter(description = "ID del módulo", required = true)
            @PathVariable Long id) {
        
        log.warn("DELETE /modulos/{} - Eliminando módulo", id);
        moduloService.deleteModulo(id);
        return ResponseEntity.ok(ApiResponse.success("Módulo eliminado exitosamente", null));
    }
}