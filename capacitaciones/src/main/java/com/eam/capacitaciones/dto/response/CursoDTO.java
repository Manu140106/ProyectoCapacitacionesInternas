package com.eam.capacitaciones.dto.response;

import com.eam.capacitaciones.domain.entity.Curso.NivelEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CursoDTO {
    private Long idCurso;
    private String titulo;
    private String descripcion;
    private String duracionEstim;
    private NivelEnum nivel;
    private Long instructorId;
    private String instructorNombre;
    private Boolean activo;
    private String imagenUrl;
    private Integer totalModulos;
    private Integer totalInscritos;
    private LocalDateTime fechaCreacion;
}