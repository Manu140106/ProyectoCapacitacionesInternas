package com.eam.capacitaciones.service;

import com.eam.capacitaciones.dto.request.InscripcionCreateRequest;
import com.eam.capacitaciones.dto.response.InscripcionDTO;
import com.eam.capacitaciones.domain.entity.Inscripcion;
import com.eam.capacitaciones.domain.entity.Inscripcion.EstadoEnum;
import com.eam.capacitaciones.exception.BadRequestException;
import com.eam.capacitaciones.exception.ResourceNotFoundException;
import com.eam.capacitaciones.mapper.InscripcionMapper;
import com.eam.capacitaciones.repository.CursoRepository;
import com.eam.capacitaciones.repository.InscripcionRepository;
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
public class InscripcionService {

    private final InscripcionRepository inscripcionRepository;
    private final UsuarioRepository usuarioRepository;
    private final CursoRepository cursoRepository;
    private final InscripcionMapper inscripcionMapper;

    @Transactional(readOnly = true)
    public List<InscripcionDTO> getInscripcionesByUsuario(Long usuarioId) {
        log.debug("Obteniendo inscripciones del usuario: {}", usuarioId);
        List<Inscripcion> inscripciones = inscripcionRepository.findByUsuarioId(usuarioId);
        return inscripcionMapper.toDTOList(inscripciones);
    }

    @Transactional(readOnly = true)
    public List<InscripcionDTO> getInscripcionesByCurso(Long cursoId) {
        log.debug("Obteniendo inscripciones del curso: {}", cursoId);
        List<Inscripcion> inscripciones = inscripcionRepository.findByCursoId(cursoId);
        return inscripcionMapper.toDTOList(inscripciones);
    }

    public InscripcionDTO inscribirUsuarioEnCurso(Long usuarioId, InscripcionCreateRequest request) {
        log.info("Inscribiendo usuario {} en curso {}", usuarioId, request.getCursoId());

        if (!usuarioRepository.existsById(usuarioId)) {
            throw new ResourceNotFoundException("Usuario no encontrado");
        }

        if (!cursoRepository.existsById(request.getCursoId())) {
            throw new ResourceNotFoundException("Curso no encontrado");
        }

        if (inscripcionRepository.existsByUsuarioIdAndCursoId(usuarioId, request.getCursoId())) {
            throw new BadRequestException("Ya está inscrito en este curso");
        }

        Inscripcion inscripcion = Inscripcion.builder()
                .usuarioId(usuarioId)
                .cursoId(request.getCursoId())
                .progreso(BigDecimal.ZERO)
                .estado(EstadoEnum.INSCRITO)
                .fechaInscripcion(LocalDate.now())
                .build();

        Inscripcion inscripcionGuardada = inscripcionRepository.save(inscripcion);
        log.info("Inscripción creada ID: {}", inscripcionGuardada.getIdInscripcion());

        return inscripcionMapper.toDTO(inscripcionGuardada);
    }

    public void actualizarProgreso(Long inscripcionId, BigDecimal nuevoProgreso) {
        log.info("Actualizando progreso de inscripción: {}", inscripcionId);

        Inscripcion inscripcion = inscripcionRepository.findById(inscripcionId)
                .orElseThrow(() -> new ResourceNotFoundException("Inscripción no encontrada"));

        inscripcion.actualizarProgreso(nuevoProgreso);
        inscripcionRepository.save(inscripcion);
    }
}