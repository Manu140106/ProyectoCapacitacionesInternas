package com.eam.capacitaciones.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BadgeDTO {
    private Long idBadge;
    private String nombre;
    private String criterio;
    private String icono;
    private Integer puntosRequeridos;
    private Boolean obtenido; 
    private LocalDate fechaObtencion;
}