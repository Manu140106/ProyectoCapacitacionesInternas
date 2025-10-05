package com.example.plataforma_capacitacion.service;

import com.example.plataforma_capacitacion.model.Certificado;
import com.example.plataforma_capacitacion.repository.CertificadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CertificadoService {

    @Autowired
    private CertificadoRepository certificadoRepository;

    public List<Certificado> listarCertificados() {
        return certificadoRepository.findAll();
    }

    public Certificado guardarCertificado(Certificado certificado) {
        return certificadoRepository.save(certificado);
    }

    public Certificado obtenerCertificadoPorId(Long id) {
        return certificadoRepository.findById(id).orElse(null);
    }

    public void eliminarCertificado(Long id) {
        certificadoRepository.deleteById(id);
    }
}
