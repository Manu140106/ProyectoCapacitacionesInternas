package com.eam.capacitaciones.dao;

import java.util.List;
import java.util.Map;

public interface CustomBadgeDAO {

    List<Map<String, Object>> obtenerBadgesDeUsuario(Integer usuarioId);

    boolean usuarioTieneBadge(Integer usuarioId, Integer badgeId);
}