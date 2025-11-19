package com.eam.capacitaciones.service;

import com.eam.capacitaciones.dto.request.CursoCreateRequest;
import com.eam.capacitaciones.dto.response.CursoDTO;
import com.eam.capacitaciones.domain.entity.Curso;
import com.eam.capacitaciones.domain.entity.Curso.NivelEnum;
import com.eam.capacitaciones.exception.BadRequestException;
import com.eam.capacitaciones.exception.ForbiddenException;
import com.eam.capacitaciones.exception.ResourceNotFoundException;
import com.eam.capacitaciones.mapper.CursoMapper;
import com.eam.capacitaciones.repository.CursoRepository;
import com.eam.capacitaciones.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CursoService {

    private final CursoRepository cursoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CursoMapper cursoMapper;

    @Transactional(readOnly = true)
    public List<CursoDTO> getAllCursos() {
        log.debug("Obteniendo todos los cursos");
        List<Curso> cursos = cursoRepository.findAll();
        return cursoMapper.toDTOList(cursos);
    }

    @Transactional(readOnly = true)
    public List<CursoDTO> getCursosActivos() {
        log.debug("Obteniendo cursos activos");
        List<Curso> cursos = cursoRepository.findByActivoTrue();
        return cursoMapper.toDTOList(cursos);
    }

    @Transactional(readOnly = true)
    public CursoDTO getCursoById(Long id) {
        log.debug("Obteniendo curso por ID: {}", id);
        Curso curso = cursoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Curso no encontrado con ID: " + id));
        return cursoMapper.toDTO(curso);
    }

    @Transactional(readOnly = true)
    public List<CursoDTO> getCursosByInstructor(Long instructorId) {
        log.debug("Obteniendo cursos del instructor: {}", instructorId);
        List<Curso> cursos = cursoRepository.findByInstructorId(instructorId);
        return cursoMapper.toDTOList(cursos);
    }

    @Transactional(readOnly = true)
    public List<CursoDTO> getCursosByNivel(NivelEnum nivel) {
        log.debug("Obteniendo cursos por nivel: {}", nivel);
        List<Curso> cursos = cursoRepository.findByNivel(nivel);
        return cursoMapper.toDTOList(cursos);
    }

    @Transactional(readOnly = true)
    public List<CursoDTO> searchCursosByTitulo(String search) {
        log.debug("Buscando cursos por título: {}", search);
        List<Curso> cursos = cursoRepository.searchByTitulo(search);
        return cursoMapper.toDTOList(cursos);
    }

    public CursoDTO createCurso(CursoCreateRequest request) {
        log.info("Creando nuevo curso: {}", request.getTitulo());

        if (!usuarioRepository.existsById(request.getInstructorId())) {
            throw new BadRequestException("El instructor no existe");
        }

        Curso curso = cursoMapper.toEntity(request);
        Curso cursoGuardado = cursoRepository.save(curso);
        
        log.info("Curso creado exitosamente con ID: {}", cursoGuardado.getIdCurso());
        return cursoMapper.toDTO(cursoGuardado);
    }

    public CursoDTO updateCurso(Long id, CursoCreateRequest request, Long currentUserId) {
        log.info("Actualizando curso ID: {}", id);

        Curso curso = cursoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Curso no encontrado"));

        if (!curso.getInstructorId().equals(currentUserId)) {
            throw new ForbiddenException("No tiene permisos para actualizar este curso");
        }

        cursoMapper.updateEntityFromRequest(request, curso);
        Curso cursoActualizado = cursoRepository.save(curso);

        log.info("Curso actualizado exitosamente ID: {}", id);
        return cursoMapper.toDTO(cursoActualizado);
    }

    public void deleteCurso(Long id) {
        log.warn("Eliminando curso ID: {}", id);

        if (!cursoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Curso no encontrado");
        }

        cursoRepository.deleteById(id);
        log.info("Curso eliminado ID: {}", id);
    }

    public void activarCurso(Long id) {
        Curso curso = cursoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Curso no encontrado"));
        curso.setActivo(true);
        cursoRepository.save(curso);
        log.info("Curso activado ID: {}", id);
    }

    public void desactivarCurso(Long id) {
        Curso curso = cursoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Curso no encontrado"));
        curso.setActivo(false);
        cursoRepository.save(curso);
        log.info("Curso desactivado ID: {}", id);
    }
}