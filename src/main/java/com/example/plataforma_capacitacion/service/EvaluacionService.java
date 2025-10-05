package com.example.plataforma_capacitacion.service;

import com.example.plataforma_capacitacion.model.Evaluacion;
import com.example.plataforma_capacitacion.repository.EvaluacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EvaluacionService {

    @Autowired
    private EvaluacionRepository evaluacionRepository;

    public List<Evaluacion> listarEvaluaciones() {
        return evaluacionRepository.findAll();
    }

    public Evaluacion guardarEvaluacion(Evaluacion evaluacion) {
        return evaluacionRepository.save(evaluacion);
    }

    public Evaluacion obtenerEvaluacionPorId(Long id) {
        return evaluacionRepository.findById(id).orElse(null);
    }

    public void eliminarEvaluacion(Long id) {
        evaluacionRepository.deleteById(id);
    }

    public Evaluacion actualizarEvaluacion(Long id, Evaluacion evaluacionActualizada) {
        Evaluacion evaluacion = evaluacionRepository.findById(id).orElse(null);
        if (evaluacion != null) {
            evaluacion.setTitulo(evaluacionActualizada.getTitulo());
            evaluacion.setTipo(evaluacionActualizada.getTipo());
            evaluacion.setCurso(evaluacionActualizada.getCurso());
            return evaluacionRepository.save(evaluacion);
        }
        return null;
    }
}