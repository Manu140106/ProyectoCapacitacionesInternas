package com.example.plataforma_capacitacion.service;

import com.example.plataforma_capacitacion.model.Respuesta;
import com.example.plataforma_capacitacion.repository.RespuestaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RespuestaService {

    @Autowired
    private RespuestaRepository respuestaRepository;

    public List<Respuesta> listarRespuestas() {
        return respuestaRepository.findAll();
    }

    public Respuesta guardarRespuesta(Respuesta respuesta) {
        return respuestaRepository.save(respuesta);
    }

    public Respuesta obtenerPorId(Long id) {
        return respuestaRepository.findById(id).orElse(null);
    }

    public void eliminarRespuesta(Long id) {
        respuestaRepository.deleteById(id);
    }

    public List<Respuesta> listarPorEvaluacion(Long evaluacionId) {
        return respuestaRepository.findByEvaluacionId(evaluacionId);
    }
}