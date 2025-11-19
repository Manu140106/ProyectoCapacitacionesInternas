package com.eam.capacitaciones.mapper;

import com.eam.capacitaciones.dto.response.InscripcionDTO;
import com.eam.capacitaciones.domain.entity.Inscripcion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InscripcionMapper {
    
    @Mapping(target = "cursoTitulo", source = "curso.titulo")
    @Mapping(target = "cursoImagenUrl", source = "curso.imagenUrl")
    InscripcionDTO toDTO(Inscripcion entity);
    
    List<InscripcionDTO> toDTOList(List<Inscripcion> entities);
}