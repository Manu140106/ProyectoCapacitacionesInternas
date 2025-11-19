package com.eam.capacitaciones.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BadgeCreateRequest {
    
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100)
    private String nombre;
    
    @Size(max = 200)
    private String criterio;
    
    @Size(max = 200)
    private String icono;
    
    @Min(value = 0, message = "Los puntos requeridos no pueden ser negativos")
    private Integer puntosRequeridos;
}