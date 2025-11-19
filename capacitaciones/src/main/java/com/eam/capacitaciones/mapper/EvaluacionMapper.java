package com.eam.capacitaciones.mapper;

import com.eam.capacitaciones.dto.request.EvaluacionCreateRequest;
import com.eam.capacitaciones.dto.response.EvaluacionDTO;
import com.eam.capacitaciones.domain.entity.Evaluacion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EvaluacionMapper {
    
    @Mapping(target = "intentosRealizados", ignore = true)
    EvaluacionDTO toDTO(Evaluacion entity);
    
    List<EvaluacionDTO> toDTOList(List<Evaluacion> entities);
    
    @Mapping(target = "idEvaluacion", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "modulo", ignore = true)
    @Mapping(target = "respuestas", ignore = true)
    Evaluacion toEntity(EvaluacionCreateRequest request);
}