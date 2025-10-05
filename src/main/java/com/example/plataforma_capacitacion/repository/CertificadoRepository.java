package com.example.plataforma_capacitacion.repository;

import com.example.plataforma_capacitacion.model.Certificado;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface CertificadoRepository extends JpaRepository<Certificado, Long> {
    List<Certificado> findByUsuarioId(Long usuarioId);
}