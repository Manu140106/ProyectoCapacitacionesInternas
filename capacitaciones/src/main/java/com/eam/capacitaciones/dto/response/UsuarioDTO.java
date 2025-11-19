package com.eam.capacitaciones.dto.response;

import com.eam.capacitaciones.domain.entity.Usuario.RolEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioDTO {
    private Long idUsuario;
    private String nombre;
    private String email;
    private RolEnum rol;
    private String departamento;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
}