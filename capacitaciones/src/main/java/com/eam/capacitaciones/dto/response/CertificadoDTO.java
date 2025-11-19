package com.eam.capacitaciones.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CertificadoDTO {
    private Long idCertificado;
    private Long usuarioId;
    private String usuarioNombre;
    private Long cursoId;
    private String cursoTitulo;
    private LocalDate fechaEmision;
    private String hash;
    private String urlPdf;
    private Boolean revocado;
}