package com.eam.capacitaciones.mapper;

import com.eam.capacitaciones.dto.request.CursoCreateRequest;
import com.eam.capacitaciones.dto.response.CursoDTO;
import com.eam.capacitaciones.domain.entity.Curso;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CursoMapper {
    
    @Mapping(target = "instructorNombre", source = "instructor.nombre")
    @Mapping(target = "totalModulos", expression = "java(entity.getModulos().size())")
    @Mapping(target = "totalInscritos", expression = "java(entity.getInscripciones().size())")
    CursoDTO toDTO(Curso entity);
    
    List<CursoDTO> toDTOList(List<Curso> entities);
    
    @Mapping(target = "idCurso", ignore = true)
    @Mapping(target = "activo", constant = "true")
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaActualizacion", ignore = true)
    @Mapping(target = "instructor", ignore = true)
    @Mapping(target = "modulos", ignore = true)
    @Mapping(target = "inscripciones", ignore = true)
    @Mapping(target = "certificados", ignore = true)
    Curso toEntity(CursoCreateRequest request);
    
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "idCurso", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaActualizacion", ignore = true)
    @Mapping(target = "instructor", ignore = true)
    @Mapping(target = "modulos", ignore = true)
    @Mapping(target = "inscripciones", ignore = true)
    @Mapping(target = "certificados", ignore = true)
    void updateEntityFromRequest(CursoCreateRequest request, @MappingTarget Curso entity);
}
