package com.eam.capacitaciones.controller;

import com.eam.capacitaciones.dto.response.ApiResponse;
import com.eam.capacitaciones.dto.response.CertificadoDTO;
import com.eam.capacitaciones.service.CertificadoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/certificados")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Certificados", description = "API para gestión de certificados de finalización")
@SecurityRequirement(name = "Bearer Authentication")
public class CertificadoController {

    private final CertificadoService certificadoService;

    @GetMapping("/usuario/{usuarioId}")
    @Operation(summary = "Listar certificados por usuario", description = "Obtiene todos los certificados obtenidos por un usuario")
    public ResponseEntity<ApiResponse<List<CertificadoDTO>>> getCertificadosByUsuario(
            @Parameter(description = "ID del usuario", required = true)
            @PathVariable Long usuarioId) {
        
        log.info("GET /certificados/usuario/{} - Obteniendo certificados", usuarioId);
        List<CertificadoDTO> certificados = certificadoService.getCertificadosByUsuario(usuarioId);
        return ResponseEntity.ok(ApiResponse.success(certificados));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener certificado por ID")
    public ResponseEntity<ApiResponse<CertificadoDTO>> getCertificadoById(
            @Parameter(description = "ID del certificado", required = true)
            @PathVariable Long id) {
        
        log.info("GET /certificados/{} - Obteniendo certificado", id);
        CertificadoDTO certificado = certificadoService.getCertificadoById(id);
        return ResponseEntity.ok(ApiResponse.success(certificado));
    }

    @GetMapping("/verificar/{hash}")
    @Operation(summary = "Verificar certificado por hash", description = "Verifica la autenticidad de un certificado usando su hash único")
    public ResponseEntity<ApiResponse<CertificadoDTO>> verificarCertificado(
            @Parameter(description = "Hash del certificado", required = true)
            @PathVariable String hash) {
        
        log.info("GET /certificados/verificar/{} - Verificando certificado", hash);
        CertificadoDTO certificado = certificadoService.verificarCertificadoPorHash(hash);
        return ResponseEntity.ok(ApiResponse.success("Certificado válido", certificado));
    }

    @PostMapping("/generar")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
    @Operation(summary = "Generar certificado", description = "Genera un certificado para un curso completado")
    public ResponseEntity<ApiResponse<CertificadoDTO>> generarCertificado(
    @Parameter(description = "ID del usuario", required = true)
    @RequestParam Long usuarioId,
    @Parameter(description = "ID del curso", required = true)
    @RequestParam Long cursoId) {
         log.info("POST /certificados/generar - Generando certificado para usuario {} y curso {}", usuarioId, cursoId);
    CertificadoDTO certificado = certificadoService.generarCertificado(usuarioId, cursoId);
    return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.success("Certificado generado exitosamente", certificado));
}

    @PatchMapping("/{id}/revocar")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Revocar certificado", description = "Revoca un certificado emitido previamente")
    public ResponseEntity<ApiResponse<Void>> revocarCertificado(
        @Parameter(description = "ID del certificado", required = true)
        @PathVariable Long id) {
    
        log.warn("PATCH /certificados/{}/revocar - Revocando certificado", id);
        certificadoService.revocarCertificado(id);
        return ResponseEntity.ok(ApiResponse.success("Certificado revocado exitosamente", null));
    }
}