package com.eam.capacitaciones.mapper;

import com.eam.capacitaciones.dto.request.ModuloCreateRequest;
import com.eam.capacitaciones.dto.response.ModuloDTO;
import com.eam.capacitaciones.domain.entity.Modulo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ModuloMapper {

    @Mapping(target = "completado", ignore = true) // Se calcula en el servicio
    @Mapping(target = "tieneEvaluaciones", expression = "java(!entity.getEvaluaciones().isEmpty())")
    ModuloDTO toDTO(Modulo entity);
    
    List<ModuloDTO> toDTOList(List<Modulo> entities);
    
    @Mapping(target = "idModulo", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "curso", ignore = true)
    @Mapping(target = "evaluaciones", ignore = true)
    Modulo toEntity(ModuloCreateRequest request);
    
    @Mapping(target = "idModulo", ignore = true)
    @Mapping(target = "cursoId", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "curso", ignore = true)
    @Mapping(target = "evaluaciones", ignore = true)
    void updateEntityFromRequest(ModuloCreateRequest request, @MappingTarget Modulo entity);
}
