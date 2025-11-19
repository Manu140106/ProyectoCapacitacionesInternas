package com.eam.capacitaciones.dto.response;

import com.eam.capacitaciones.domain.entity.Inscripcion.EstadoEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InscripcionDTO {
    private Long idInscripcion;
    private Long usuarioId;
    private Long cursoId;
    private String cursoTitulo;
    private String cursoImagenUrl;
    private BigDecimal progreso;
    private EstadoEnum estado;
    private LocalDate fechaInscripcion;
    private LocalDate fechaCompletado;
}