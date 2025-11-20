package com.eam.capacitaciones.dao;

import java.util.Map;

public interface CustomAuthDAO {

    Map<String, Object> loginPorEmail(String email);

    boolean emailExiste(String email);
}