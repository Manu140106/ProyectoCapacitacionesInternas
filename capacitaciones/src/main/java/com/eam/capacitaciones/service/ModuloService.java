package com.eam.capacitaciones.service;

import com.eam.capacitaciones.dto.request.ModuloCreateRequest;
import com.eam.capacitaciones.dto.response.ModuloDTO;
import com.eam.capacitaciones.domain.entity.Modulo;
import com.eam.capacitaciones.exception.ResourceNotFoundException;
import com.eam.capacitaciones.mapper.ModuloMapper;
import com.eam.capacitaciones.repository.CursoRepository;
import com.eam.capacitaciones.repository.ModuloRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ModuloService {

    private final ModuloRepository moduloRepository;
    private final CursoRepository cursoRepository;
    private final ModuloMapper moduloMapper;

    @Transactional(readOnly = true)
    public List<ModuloDTO> getModulosByCurso(Long cursoId) {
        log.debug("Obteniendo módulos del curso: {}", cursoId);
        List<Modulo> modulos = moduloRepository.findByCursoIdOrderByOrdenAsc(cursoId);
        return moduloMapper.toDTOList(modulos);
    }

    @Transactional(readOnly = true)
    public ModuloDTO getModuloById(Long id) {
        log.debug("Obteniendo módulo por ID: {}", id);
        Modulo modulo = moduloRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Módulo no encontrado"));
        return moduloMapper.toDTO(modulo);
    }

    public ModuloDTO createModulo(ModuloCreateRequest request) {
        log.info("Creando módulo para curso: {}", request.getCursoId());

        if (!cursoRepository.existsById(request.getCursoId())) {
            throw new ResourceNotFoundException("Curso no encontrado");
        }

        Modulo modulo = moduloMapper.toEntity(request);
        Modulo moduloGuardado = moduloRepository.save(modulo);
        
        log.info("Módulo creado ID: {}", moduloGuardado.getIdModulo());
        return moduloMapper.toDTO(moduloGuardado);
    }

    public void deleteModulo(Long id) {
        log.warn("Eliminando módulo ID: {}", id);
        
        if (!moduloRepository.existsById(id)) {
            throw new ResourceNotFoundException("Módulo no encontrado");
        }

        moduloRepository.deleteById(id);
    }
}