package com.eam.capacitaciones.controller;

import com.eam.capacitaciones.dto.request.CursoCreateRequest;
import com.eam.capacitaciones.dto.response.ApiResponse;
import com.eam.capacitaciones.dto.response.CursoDTO;
import com.eam.capacitaciones.domain.entity.Curso.NivelEnum;
import com.eam.capacitaciones.security.CustomUserDetails;
import com.eam.capacitaciones.service.CursoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@RequestMapping("/cursos")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Cursos", description = "API para gestión de cursos de capacitación")
@SecurityRequirement(name = "Bearer Authentication")
public class CursoController {

    private final CursoService cursoService;

    @GetMapping
    @Operation(
        summary = "Listar todos los cursos",
        description = "Obtiene lista de cursos disponibles. Accesible para todos los usuarios autenticados."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<ApiResponse<List<CursoDTO>>> getAllCursos() {
        log.info("GET /cursos - Obteniendo todos los cursos");
        List<CursoDTO> cursos = cursoService.getAllCursos();
        return ResponseEntity.ok(ApiResponse.success(cursos));
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Obtener curso por ID",
        description = "Obtiene los detalles completos de un curso específico incluyendo información del instructor"
    )
    public ResponseEntity<ApiResponse<CursoDTO>> getCursoById(
            @Parameter(description = "ID del curso", required = true)
            @PathVariable Long id) {
        
        log.info("GET /cursos/{} - Obteniendo curso", id);
        CursoDTO curso = cursoService.getCursoById(id);
        return ResponseEntity.ok(ApiResponse.success(curso));
    }

    @GetMapping("/instructor/{instructorId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
    @Operation(summary = "Obtener cursos por instructor", description = "Lista todos los cursos creados por un instructor específico")
    public ResponseEntity<ApiResponse<List<CursoDTO>>> getCursosByInstructor(
            @Parameter(description = "ID del instructor", required = true)
            @PathVariable Long instructorId) {
        
        log.info("GET /cursos/instructor/{} - Obteniendo cursos", instructorId);
        List<CursoDTO> cursos = cursoService.getCursosByInstructor(instructorId);
        return ResponseEntity.ok(ApiResponse.success(cursos));
    }

    @GetMapping("/nivel/{nivel}")
    @Operation(summary = "Obtener cursos por nivel", description = "Filtra cursos por nivel de dificultad (BASICO, INTERMEDIO, AVANZADO)")
    public ResponseEntity<ApiResponse<List<CursoDTO>>> getCursosByNivel(
            @Parameter(description = "Nivel del curso", required = true)
            @PathVariable NivelEnum nivel) {
        
        log.info("GET /cursos/nivel/{} - Obteniendo cursos", nivel);
        List<CursoDTO> cursos = cursoService.getCursosByNivel(nivel);
        return ResponseEntity.ok(ApiResponse.success(cursos));
    }

    @GetMapping("/buscar")
    @Operation(summary = "Buscar cursos por título", description = "Búsqueda de cursos por palabra clave en el título")
    public ResponseEntity<ApiResponse<List<CursoDTO>>> buscarCursosPorTitulo(
            @Parameter(description = "Texto a buscar", required = true)
            @RequestParam String titulo) {
        
        log.info("GET /cursos/buscar?titulo={} - Buscando cursos", titulo);
        List<CursoDTO> cursos = cursoService.searchCursosByTitulo(titulo);
        return ResponseEntity.ok(ApiResponse.success(cursos));
    }

    @GetMapping("/activos")
    @Operation(summary = "Obtener cursos activos", description = "Lista todos los cursos disponibles y activos en el sistema")
    public ResponseEntity<ApiResponse<List<CursoDTO>>> getCursosActivos() {
        log.info("GET /cursos/activos - Obteniendo cursos activos");
        List<CursoDTO> cursos = cursoService.getCursosActivos();
        return ResponseEntity.ok(ApiResponse.success(cursos));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
    @Operation(
        summary = "Crear nuevo curso",
        description = "Registra un nuevo curso en el sistema. Solo ADMIN e INSTRUCTOR pueden crear cursos."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Curso creado exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    public ResponseEntity<ApiResponse<CursoDTO>> createCurso(
            @Valid @RequestBody CursoCreateRequest request) {
        
        log.info("POST /cursos - Creando nuevo curso: {}", request.getTitulo());
        CursoDTO nuevoCurso = cursoService.createCurso(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Curso creado exitosamente", nuevoCurso));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
    @Operation(
        summary = "Actualizar curso",
        description = "Actualiza los datos de un curso existente. Los instructores solo pueden actualizar sus propios cursos."
    )
    public ResponseEntity<ApiResponse<CursoDTO>> updateCurso(
            @PathVariable Long id,
            @Valid @RequestBody CursoCreateRequest request,
            Authentication authentication) {
        
        log.info("PUT /cursos/{} - Actualizando curso", id);
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        CursoDTO cursoActualizado = cursoService.updateCurso(id, request, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success("Curso actualizado exitosamente", cursoActualizado));
    }

    @PatchMapping("/{id}/desactivar")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Desactivar curso", description = "Marca un curso como inactivo sin eliminarlo de la BD")
    public ResponseEntity<ApiResponse<Void>> desactivarCurso(@PathVariable Long id) {
        log.info("PATCH /cursos/{}/desactivar - Desactivando curso", id);
        cursoService.desactivarCurso(id);
        return ResponseEntity.ok(ApiResponse.success("Curso desactivado exitosamente", null));
    }

    @PatchMapping("/{id}/activar")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Activar curso", description = "Marca un curso como activo")
    public ResponseEntity<ApiResponse<Void>> activarCurso(@PathVariable Long id) {
        log.info("PATCH /cursos/{}/activar - Activando curso", id);
        cursoService.activarCurso(id);
        return ResponseEntity.ok(ApiResponse.success("Curso activado exitosamente", null));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Eliminar curso permanentemente",
        description = "PRECAUCIÓN: Elimina físicamente el curso de la BD. Operación irreversible."
    )
    public ResponseEntity<ApiResponse<Void>> deleteCurso(@PathVariable Long id) {
        log.warn("DELETE /cursos/{} - Eliminando curso PERMANENTEMENTE", id);
        cursoService.deleteCurso(id);
        return ResponseEntity.ok(ApiResponse.success("Curso eliminado permanentemente", null));
    }
}