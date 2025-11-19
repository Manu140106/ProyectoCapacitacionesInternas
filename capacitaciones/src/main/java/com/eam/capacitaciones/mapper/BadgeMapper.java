package com.eam.capacitaciones.mapper;

import com.eam.capacitaciones.dto.request.BadgeCreateRequest;
import com.eam.capacitaciones.dto.response.BadgeDTO;
import com.eam.capacitaciones.domain.entity.Badge;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BadgeMapper {
    
    @Mapping(target = "obtenido", ignore = true) // Se calcula en el servicio
    @Mapping(target = "fechaObtencion", ignore = true) // Se calcula en el servicio
    BadgeDTO toDTO(Badge entity);
    
    List<BadgeDTO> toDTOList(List<Badge> entities);

    @Mapping(target = "idBadge", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "usuarios", ignore = true)
    Badge toEntity(BadgeCreateRequest request);
}