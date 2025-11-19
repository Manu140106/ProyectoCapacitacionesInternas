package com.eam.capacitaciones.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RespuestaSubmitRequest {
    
    @NotNull(message = "El ID de la evaluación es obligatorio")
    private Long evaluacionId;
    
    @NotBlank(message = "La respuesta es obligatoria")
    private String respuestaTexto;
}