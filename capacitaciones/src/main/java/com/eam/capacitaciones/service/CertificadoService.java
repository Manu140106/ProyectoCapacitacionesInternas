package com.eam.capacitaciones.service;

import com.eam.capacitaciones.dto.response.CertificadoDTO;
import com.eam.capacitaciones.domain.entity.Certificado;
import com.eam.capacitaciones.domain.entity.Inscripcion;
import com.eam.capacitaciones.exception.BadRequestException;
import com.eam.capacitaciones.exception.ResourceNotFoundException;
import com.eam.capacitaciones.mapper.CertificadoMapper;
import com.eam.capacitaciones.repository.CertificadoRepository;
import com.eam.capacitaciones.repository.CursoRepository;
import com.eam.capacitaciones.repository.InscripcionRepository;
import com.eam.capacitaciones.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CertificadoService {

    private final CertificadoRepository certificadoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CursoRepository cursoRepository;
    private final InscripcionRepository inscripcionRepository;
    private final CertificadoMapper certificadoMapper;

    @Transactional(readOnly = true)
    public List<CertificadoDTO> getCertificadosByUsuario(Long usuarioId) {
        log.debug("Obteniendo certificados del usuario: {}", usuarioId);
        List<Certificado> certificados = certificadoRepository.findByUsuarioId(usuarioId);
        return certificadoMapper.toDTOList(certificados);
    }

    @Transactional(readOnly = true)
    public CertificadoDTO getCertificadoById(Long id) {
        log.debug("Obteniendo certificado por ID: {}", id);
        Certificado certificado = certificadoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Certificado no encontrado con ID: " + id));
        return certificadoMapper.toDTO(certificado);
    }

    @Transactional(readOnly = true)
    public CertificadoDTO verificarCertificadoPorHash(String hash) {
        log.info("Verificando certificado con hash: {}", hash);
        Certificado certificado = certificadoRepository.findByHash(hash)
                .orElseThrow(() -> new ResourceNotFoundException("Certificado no encontrado o inválido"));

        if (!certificado.esValido()) {
            throw new BadRequestException("Este certificado ha sido revocado");
        }

        return certificadoMapper.toDTO(certificado);
    }

    public CertificadoDTO generarCertificado(Long usuarioId, Long cursoId) {
        log.info("Generando certificado para usuario {} y curso {}", usuarioId, cursoId);

        usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        cursoRepository.findById(cursoId)
                .orElseThrow(() -> new ResourceNotFoundException("Curso no encontrado"));

        Inscripcion inscripcion = inscripcionRepository.findByUsuarioIdAndCursoId(usuarioId, cursoId)
                .orElseThrow(() -> new BadRequestException("El usuario no está inscrito en este curso"));

        if (!inscripcion.estaCompletado()) {
            throw new BadRequestException("El usuario no ha completado el curso");
        }

        if (certificadoRepository.existsByUsuarioIdAndCursoId(usuarioId, cursoId)) {
            throw new BadRequestException("Ya existe un certificado para este usuario y curso");
        }

        String hash = generarHashUnico();

        Certificado certificado = Certificado.builder()
                .usuarioId(usuarioId)
                .cursoId(cursoId)
                .fechaEmision(LocalDate.now())
                .hash(hash)
                .revocado(false)
                .build();

        Certificado certificadoGuardado = certificadoRepository.save(certificado);
        log.info("Certificado generado exitosamente con ID: {}", certificadoGuardado.getIdCertificado());

        return certificadoMapper.toDTO(certificadoGuardado);
    }

    public void revocarCertificado(Long certificadoId) {
        log.warn("Revocando certificado ID: {}", certificadoId);

        Certificado certificado = certificadoRepository.findById(certificadoId)
                .orElseThrow(() -> new ResourceNotFoundException("Certificado no encontrado"));

        if (certificado.getRevocado()) {
            throw new BadRequestException("El certificado ya está revocado");
        }

        certificado.revocar();
        certificadoRepository.save(certificado);
        log.info("Certificado revocado ID: {}", certificadoId);
    }

    private String generarHashUnico() {
        String hash;
        do {
            hash = UUID.randomUUID().toString().replace("-", "").substring(0, 32);
        } while (certificadoRepository.findByHash(hash).isPresent());
        return hash;
    }
}