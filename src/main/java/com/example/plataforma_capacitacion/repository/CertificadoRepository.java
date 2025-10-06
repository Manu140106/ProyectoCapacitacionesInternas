package com.example.plataforma_capacitacion.repository;

import com.example.plataforma_capacitacion.model.Certificado;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CertificadoRepository extends JpaRepository<Certificado, Long> {
    List<Certificado> findByUsuarioId(Long usuarioId);
}