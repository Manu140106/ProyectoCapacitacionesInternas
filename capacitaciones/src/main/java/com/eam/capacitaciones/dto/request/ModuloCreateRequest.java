package com.eam.capacitaciones.dto.request;

import com.eam.capacitaciones.domain.entity.Modulo.TipoEnum;
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
public class ModuloCreateRequest {
    
    @NotNull(message = "El ID del curso es obligatorio")
    private Long cursoId;
    
    @NotBlank(message = "El título es obligatorio")
    @Size(min = 3, max = 150)
    private String titulo;
    
    @NotNull(message = "El tipo es obligatorio")
    private TipoEnum tipo;
    
    @NotNull(message = "El orden es obligatorio")
    @Min(value = 1, message = "El orden debe ser mayor a 0")
    private Integer orden;
    
    private String contenido;
    
    private Integer duracionMinutos;
}