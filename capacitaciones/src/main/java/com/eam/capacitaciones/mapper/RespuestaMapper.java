package com.eam.capacitaciones.mapper;

import com.eam.capacitaciones.dto.response.RespuestaDTO;
import com.eam.capacitaciones.domain.entity.Respuesta;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RespuestaMapper {

    @Mapping(target = "evaluacionTitulo", source = "evaluacion.titulo")
    @Mapping(target = "usuarioNombre", source = "usuario.nombre")
    RespuestaDTO toDTO(Respuesta entity);
    
    List<RespuestaDTO> toDTOList(List<Respuesta> entities);
}