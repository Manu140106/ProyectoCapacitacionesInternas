package com.example.plataforma_capacitacion.controller;

import com.example.plataforma_capacitacion.model.Certificado;
import com.example.plataforma_capacitacion.repository.CertificadoRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/certificados")
public class CertificadoController {

    private final CertificadoRepository certificadoRepository;

    public CertificadoController(CertificadoRepository certificadoRepository) {
        this.certificadoRepository = certificadoRepository;
    }

    @GetMapping
    public List<Certificado> getAll() {
        return certificadoRepository.findAll();
    }

    @GetMapping("/{id}")
    public Certificado getById(@PathVariable Long id) {
        return certificadoRepository.findById(id).orElse(null);
    }

    @PostMapping
    public Certificado create(@RequestBody Certificado certificado) {
        return certificadoRepository.save(certificado);
    }

    @PutMapping("/{id}")
    public Certificado update(@PathVariable Long id, @RequestBody Certificado certificado) {
        certificado.setId(id);
        return certificadoRepository.save(certificado);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        certificadoRepository.deleteById(id);
    }
}

