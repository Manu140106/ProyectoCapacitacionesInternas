package com.eam.capacitaciones.service;

import com.eam.capacitaciones.dto.request.EvaluacionCreateRequest;
import com.eam.capacitaciones.dto.response.EvaluacionDTO;
import com.eam.capacitaciones.domain.entity.Evaluacion;
import com.eam.capacitaciones.exception.BadRequestException;
import com.eam.capacitaciones.exception.ResourceNotFoundException;
import com.eam.capacitaciones.mapper.EvaluacionMapper;
import com.eam.capacitaciones.repository.EvaluacionRepository;
import com.eam.capacitaciones.repository.ModuloRepository;
import com.eam.capacitaciones.repository.RespuestaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class EvaluacionService {

    private final EvaluacionRepository evaluacionRepository;
    private final ModuloRepository moduloRepository;
    private final RespuestaRepository respuestaRepository;
    private final EvaluacionMapper evaluacionMapper;

    @Transactional(readOnly = true)
    public List<EvaluacionDTO> getEvaluacionesByModulo(Long moduloId) {
        log.debug("Obteniendo evaluaciones del módulo: {}", moduloId);
        List<Evaluacion> evaluaciones = evaluacionRepository.findByModuloId(moduloId);
        return evaluacionMapper.toDTOList(evaluaciones);
    }

    @Transactional(readOnly = true)
    public EvaluacionDTO getEvaluacionById(Long id) {
        log.debug("Obteniendo evaluación por ID: {}", id);
        Evaluacion evaluacion = evaluacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evaluación no encontrada con ID: " + id));
        return evaluacionMapper.toDTO(evaluacion);
    }

    public EvaluacionDTO createEvaluacion(EvaluacionCreateRequest request) {
        log.info("Creando nueva evaluación: {}", request.getTitulo());

        if (!moduloRepository.existsById(request.getModuloId())) {
            throw new BadRequestException("El módulo no existe");
        }

        if (request.getPuntajeMax() <= 0) {
            throw new BadRequestException("El puntaje máximo debe ser mayor a 0");
        }

        Evaluacion evaluacion = evaluacionMapper.toEntity(request);
        Evaluacion evaluacionGuardada = evaluacionRepository.save(evaluacion);

        log.info("Evaluación creada exitosamente con ID: {}", evaluacionGuardada.getIdEvaluacion());
        return evaluacionMapper.toDTO(evaluacionGuardada);
    }

    public void deleteEvaluacion(Long id) {
        log.warn("Eliminando evaluación ID: {}", id);

        if (!evaluacionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Evaluación no encontrada");
        }

        Long cantidadRespuestas = respuestaRepository.countIntentosByUsuario(id, 0L);
        if (cantidadRespuestas > 0) {
            throw new BadRequestException("No se puede eliminar una evaluación con respuestas registradas");
        }

        evaluacionRepository.deleteById(id);
        log.info("Evaluación eliminada ID: {}", id);
    }

    @Transactional(readOnly = true)
    public Long contarIntentos(Long evaluacionId, Long usuarioId) {
        return respuestaRepository.countIntentosByUsuario(evaluacionId, usuarioId);
    }
}