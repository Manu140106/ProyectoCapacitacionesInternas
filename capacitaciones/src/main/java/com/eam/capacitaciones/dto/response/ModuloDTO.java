package com.eam.capacitaciones.dto.response;

import com.eam.capacitaciones.domain.entity.Modulo.TipoEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModuloDTO {
    private Long idModulo;
    private Long cursoId;
    private String titulo;
    private TipoEnum tipo;
    private Integer orden;
    private String contenido;
    private Integer duracionMinutos;
    private Boolean completado; 
    private Boolean tieneEvaluaciones;
}