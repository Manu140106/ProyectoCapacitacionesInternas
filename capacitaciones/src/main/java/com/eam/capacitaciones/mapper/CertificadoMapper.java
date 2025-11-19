package com.eam.capacitaciones.mapper;

import com.eam.capacitaciones.dto.response.CertificadoDTO;
import com.eam.capacitaciones.domain.entity.Certificado;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CertificadoMapper {
    
    @Mapping(target = "usuarioNombre", source = "usuario.nombre")
    @Mapping(target = "cursoTitulo", source = "curso.titulo")
    CertificadoDTO toDTO(Certificado entity);
    
    List<CertificadoDTO> toDTOList(List<Certificado> entities);
}