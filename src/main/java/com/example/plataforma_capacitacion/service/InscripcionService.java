package com.example.plataforma_capacitacion.service;

import com.example.plataforma_capacitacion.model.Inscripcion;
import com.example.plataforma_capacitacion.repository.InscripcionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class InscripcionService {

    @Autowired
    private InscripcionRepository inscripcionRepository;

    public List<Inscripcion> listarInscripciones() {
        return inscripcionRepository.findAll();
    }

    public Inscripcion guardarInscripcion(Inscripcion inscripcion) {
        return inscripcionRepository.save(inscripcion);
    }

    public Inscripcion obtenerInscripcionPorId(Long id) {
        return inscripcionRepository.findById(id).orElse(null);
    }

    public void eliminarInscripcion(Long id) {
        inscripcionRepository.deleteById(id);
    }

    public Inscripcion actualizarInscripcion(Long id, Inscripcion inscripcionActualizada) {
        Inscripcion inscripcion = inscripcionRepository.findById(id).orElse(null);
        if (inscripcion != null) {
            inscripcion.setUsuario(inscripcionActualizada.getUsuario());
            inscripcion.setCurso(inscripcionActualizada.getCurso());
            inscripcion.setEstado(inscripcionActualizada.getEstado());
            return inscripcionRepository.save(inscripcion);
        }
        return null;
    }
}