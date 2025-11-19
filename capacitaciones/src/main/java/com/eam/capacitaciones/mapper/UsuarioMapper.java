package com.eam.capacitaciones.mapper;

import com.eam.capacitaciones.dto.request.UsuarioCreateRequest;
import com.eam.capacitaciones.dto.request.UsuarioUpdateRequest;
import com.eam.capacitaciones.dto.response.UsuarioDTO;
import com.eam.capacitaciones.domain.entity.Usuario;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UsuarioMapper {
    
    UsuarioDTO toDTO(Usuario entity);
    
    List<UsuarioDTO> toDTOList(List<Usuario> entities);
    
    @Mapping(target = "idUsuario", ignore = true)
    @Mapping(target = "activo", constant = "true")
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaActualizacion", ignore = true)
    @Mapping(target = "inscripciones", ignore = true)
    @Mapping(target = "respuestas", ignore = true)
    @Mapping(target = "certificados", ignore = true)
    @Mapping(target = "badges", ignore = true)
    Usuario toEntity(UsuarioCreateRequest request);
    
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "idUsuario", ignore = true)
    @Mapping(target = "password", ignore = true) 
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaActualizacion", ignore = true)
    @Mapping(target = "inscripciones", ignore = true)
    @Mapping(target = "respuestas", ignore = true)
    @Mapping(target = "certificados", ignore = true)
    @Mapping(target = "badges", ignore = true)
    void updateEntityFromRequest(UsuarioUpdateRequest request, @MappingTarget Usuario entity);
}