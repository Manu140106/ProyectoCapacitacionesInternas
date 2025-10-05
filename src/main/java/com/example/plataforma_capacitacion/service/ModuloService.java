package com.example.plataforma_capacitacion.service;

import com.example.plataforma_capacitacion.model.Modulo;
import com.example.plataforma_capacitacion.repository.ModuloRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ModuloService {

    @Autowired
    private ModuloRepository moduloRepository;

    public List<Modulo> listarModulos() {
        return moduloRepository.findAll();
    }

    public Modulo guardarModulo(Modulo modulo) {
        return moduloRepository.save(modulo);
    }

    public Modulo obtenerModuloPorId(Long id) {
        return moduloRepository.findById(id).orElse(null);
    }

    public void eliminarModulo(Long id) {
        moduloRepository.deleteById(id);
    }

    public Modulo actualizarModulo(Long id, Modulo moduloActualizado) {
        Modulo modulo = moduloRepository.findById(id).orElse(null);
        if (modulo != null) {
            modulo.setTitulo(moduloActualizado.getTitulo());
            modulo.setContenido(moduloActualizado.getContenido());
            modulo.setCurso(moduloActualizado.getCurso());
            return moduloRepository.save(modulo);
        }
        return null;
    }
}