package com.eam.capacitaciones.dao;

import com.eam.capacitaciones.domain.entity.Usuario;
import java.util.List;

public interface CustomUsuarioDAO {
    
    List<Usuario> buscarPorCriterios(String nombre, String departamento, String rol);
    
    List<Usuario> obtenerUsuariosConMasCursosCompletados(int limite);

    long contarUsuariosActivos();

    List<Usuario> buscarPorDepartamentoYRol(String departamento, String rol);
}