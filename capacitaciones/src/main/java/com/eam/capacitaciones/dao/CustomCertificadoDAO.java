package com.eam.capacitaciones.dao;

import java.util.List;
import java.util.Map;

public interface CustomCertificadoDAO {
    
    List<Map<String, Object>> obtenerEstadisticasCertificadosPorPeriodo(
        int year, int month);
    
    List<Map<String, Object>> obtenerUsuariosConMasCertificados(int limit);

    List<Map<String, Object>> obtenerCursosConMasCertificados(int limit);
    
    Map<String, Object> verificarCertificadoPorHash(String hash);
}