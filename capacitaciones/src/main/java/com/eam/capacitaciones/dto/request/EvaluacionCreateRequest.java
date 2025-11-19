package com.eam.capacitaciones.dto.request;

import com.eam.capacitaciones.domain.entity.Evaluacion.TipoEnum;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaluacionCreateRequest {
    
    @NotNull(message = "El ID del módulo es obligatorio")
    private Long moduloId;
    
    @NotBlank(message = "El título es obligatorio")
    @Size(max = 150)
    private String titulo;
    
    @NotNull(message = "El tipo es obligatorio")
    private TipoEnum tipo;
    
    @NotNull(message = "El puntaje máximo es obligatorio")
    @Min(value = 1, message = "El puntaje máximo debe ser mayor a 0")
    private Integer puntajeMax;
    
    private Integer duracionMinutos;
    
    private Integer intentosPermitidos;
}