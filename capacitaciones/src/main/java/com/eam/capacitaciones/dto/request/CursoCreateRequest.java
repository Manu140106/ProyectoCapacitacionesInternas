package com.eam.capacitaciones.dto.request;

import com.eam.capacitaciones.domain.entity.Curso.NivelEnum;
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
public class CursoCreateRequest {
    
    @NotBlank(message = "El título es obligatorio")
    @Size(min = 5, max = 150)
    private String titulo;
    
    private String descripcion;
    
    @Size(max = 50)
    private String duracionEstim;
    
    @NotNull(message = "El nivel es obligatorio")
    private NivelEnum nivel;
    
    @NotNull(message = "El instructor es obligatorio")
    private Long instructorId;
    
    private String imagenUrl;
}