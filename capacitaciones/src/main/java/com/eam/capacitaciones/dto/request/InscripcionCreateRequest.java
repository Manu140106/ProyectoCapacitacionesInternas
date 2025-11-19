package com.eam.capacitaciones.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InscripcionCreateRequest {
    
    @NotNull(message = "El ID del curso es obligatorio")
    private Long cursoId;
}