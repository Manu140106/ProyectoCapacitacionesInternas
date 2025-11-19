package com.eam.capacitaciones.dto.response;

import com.eam.capacitaciones.domain.entity.Evaluacion.TipoEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaluacionDTO {
    private Long idEvaluacion;
    private Long moduloId;
    private String titulo;
    private TipoEnum tipo;
    private Integer puntajeMax;
    private Integer duracionMinutos;
    private Integer intentosPermitidos;
    private Integer intentosRealizados;
}