package com.eam.capacitaciones.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RespuestaCalificarRequest {
    
    @NotNull(message = "La puntuación es obligatoria")
    @DecimalMin(value = "0.00", message = "La puntuación no puede ser negativa")
    private BigDecimal puntuacion;
    
    private String comentario;
}