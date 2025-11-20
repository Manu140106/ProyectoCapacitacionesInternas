package com.eam.capacitaciones.service;

import com.eam.capacitaciones.dto.request.RespuestaCalificarRequest;
import com.eam.capacitaciones.dto.request.RespuestaSubmitRequest;
import com.eam.capacitaciones.dto.response.RespuestaDTO;
import com.eam.capacitaciones.domain.entity.Evaluacion;
import com.eam.capacitaciones.domain.entity.Respuesta;
import com.eam.capacitaciones.exception.BadRequestException;
import com.eam.capacitaciones.exception.ResourceNotFoundException;
import com.eam.capacitaciones.mapper.RespuestaMapper;
import com.eam.capacitaciones.repository.EvaluacionRepository;
import com.eam.capacitaciones.repository.RespuestaRepository;
import com.eam.capacitaciones.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class RespuestaService {

    private final RespuestaRepository respuestaRepository;
    private final EvaluacionRepository evaluacionRepository;
    private final UsuarioRepository usuarioRepository;
    private final RespuestaMapper respuestaMapper;

    @Transactional(readOnly = true)
    public List<RespuestaDTO> getRespuestasByEvaluacionAndUsuario(Long evaluacionId, Long usuarioId) {
        log.debug("Obteniendo respuestas de usuario {} en evaluación {}", usuarioId, evaluacionId);
        List<Respuesta> respuestas = respuestaRepository.findByEvaluacionIdAndUsuarioId(evaluacionId, usuarioId);
        return respuestaMapper.toDTOList(respuestas);
    }

    @Transactional(readOnly = true)
    public List<RespuestaDTO> getRespuestasPendientesCalificacion(Long evaluacionId) {
        log.debug("Obteniendo respuestas pendientes de calificación para evaluación: {}", evaluacionId);
        List<Respuesta> respuestas = respuestaRepository.findPendientesCalificacion(evaluacionId);
        return respuestaMapper.toDTOList(respuestas);
    }

    public RespuestaDTO submitRespuesta(Long usuarioId, RespuestaSubmitRequest request) {
        log.info("Usuario {} enviando respuesta a evaluación {}", usuarioId, request.getEvaluacionId());

        if (!usuarioRepository.existsById(usuarioId)) {
            throw new ResourceNotFoundException("Usuario no encontrado");
        }

        Evaluacion evaluacion = evaluacionRepository.findById(request.getEvaluacionId())
                .orElseThrow(() -> new ResourceNotFoundException("Evaluación no encontrada"));

        Long intentosRealizados = respuestaRepository.countIntentosByUsuario(
                request.getEvaluacionId(), usuarioId);

        if (evaluacion.tieneLimiteIntentos() && 
            intentosRealizados >= evaluacion.getIntentosPermitidos()) {
            throw new BadRequestException("Ha excedido el número máximo de intentos permitidos");
        }

        Respuesta respuesta = Respuesta.builder()
                .evaluacionId(request.getEvaluacionId())
                .usuarioId(usuarioId)
                .respuestaTexto(request.getRespuestaTexto())
                .puntuacion(BigDecimal.ZERO)
                .fecha(LocalDate.now())
                .intentoNumero(intentosRealizados.intValue() + 1)
                .calificada(false)
                .build();

        if (evaluacion.getTipo() == Evaluacion.TipoEnum.MCQ) {
            respuesta.setCalificada(true);
        }

        Respuesta respuestaGuardada = respuestaRepository.save(respuesta);
        log.info("Respuesta guardada con ID: {}", respuestaGuardada.getIdRespuesta());

        return respuestaMapper.toDTO(respuestaGuardada);
    }

    public RespuestaDTO calificarRespuesta(Long respuestaId, RespuestaCalificarRequest request) {
        log.info("Calificando respuesta ID: {}", respuestaId);

        Respuesta respuesta = respuestaRepository.findById(respuestaId)
                .orElseThrow(() -> new ResourceNotFoundException("Respuesta no encontrada"));

        if (respuesta.getCalificada()) {
            throw new BadRequestException("Esta respuesta ya ha sido calificada");
        }

        respuesta.calificar(request.getPuntuacion(), request.getComentario());
        Respuesta respuestaCalificada = respuestaRepository.save(respuesta);

        log.info("Respuesta calificada: {} puntos", request.getPuntuacion());
        return respuestaMapper.toDTO(respuestaCalificada);
    }
}