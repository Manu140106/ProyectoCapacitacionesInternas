package com.example.plataforma_capacitacion.service;

import com.example.plataforma_capacitacion.model.Curso;
import com.example.plataforma_capacitacion.repository.CursoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CursoService {

    @Autowired
    private CursoRepository cursoRepository;

    public List<Curso> listarCursos() {
        return cursoRepository.findAll();
    }

    public Curso guardarCurso(Curso curso) {
        return cursoRepository.save(curso);
    }

    public Curso obtenerCursoPorId(Long id) {
        return cursoRepository.findById(id).orElse(null);
    }

    public void eliminarCurso(Long id) {
        cursoRepository.deleteById(id);
    }

    public Curso actualizarCurso(Long id, Curso cursoActualizado) {
        Curso curso = cursoRepository.findById(id).orElse(null);
        if (curso != null) {
            curso.setNombre(cursoActualizado.getNombre());
            curso.setDescripcion(cursoActualizado.getDescripcion());
            curso.setDuracion(cursoActualizado.getDuracion());
            return cursoRepository.save(curso);
        }
        return null;
    }
}
