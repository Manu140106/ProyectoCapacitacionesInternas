package com.eam.capacitaciones.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RespuestaDTO {
    private Long idRespuesta;
    private Long evaluacionId;
    private String evaluacionTitulo;
    private Long usuarioId;
    private String usuarioNombre;
    private BigDecimal puntuacion;
    private LocalDate fecha;
    private Integer intentoNumero;
    private String respuestaTexto;
    private Boolean calificada;
    private String comentarioInstructor;
}