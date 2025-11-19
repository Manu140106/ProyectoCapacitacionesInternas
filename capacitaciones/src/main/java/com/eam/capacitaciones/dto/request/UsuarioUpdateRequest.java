package com.eam.capacitaciones.dto.request;

import com.eam.capacitaciones.domain.entity.Usuario.RolEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioUpdateRequest {
    
    @Size(min = 3, max = 100)
    private String nombre;
    
    @Email(message = "El email debe ser válido")
    @Size(max = 150)
    private String email;
    
    private RolEnum rol;
    
    @Size(max = 100)
    private String departamento;
    
    private Boolean activo;
}