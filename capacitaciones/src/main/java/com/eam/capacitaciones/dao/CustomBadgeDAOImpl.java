package com.eam.capacitaciones.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
public class CustomBadgeDAOImpl implements CustomBadgeDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> obtenerBadgesDeUsuario(Integer usuarioId) {
        log.debug("Obteniendo badges del usuario: {}", usuarioId);

        String sql = """
            SELECT b.idBadge, b.nombre, b.criterio, b.icono, b.puntosRequeridos, b.fechaCreacion
            FROM Badge b
            JOIN usuario_badges ub ON b.idBadge = ub.badge_id
            WHERE ub.usuario_id = :usuarioId
            ORDER BY b.fechaCreacion DESC
        """;

        List<Object[]> results = entityManager.createNativeQuery(sql)
                .setParameter("usuarioId", usuarioId)
                .getResultList();

        List<Map<String, Object>> badges = new ArrayList<>();
        for (Object[] row : results) {
            Map<String, Object> badge = new HashMap<>();
            badge.put("idBadge", row[0]);
            badge.put("nombre", row[1]);
            badge.put("criterio", row[2]);
            badge.put("icono", row[3]);
            badge.put("puntosRequeridos", row[4]);
            badge.put("fechaCreacion", row[5]);
            badges.add(badge);
        }

        return badges;
    }

    @Override
    public boolean usuarioTieneBadge(Integer usuarioId, Integer badgeId) {
        log.debug("Verificando si usuario {} tiene badge {}", usuarioId, badgeId);

        String sql = """
            SELECT COUNT(*) > 0
            FROM usuario_badges
            WHERE usuario_id = :usuarioId AND badge_id = :badgeId
        """;

        Boolean result = (Boolean) entityManager.createNativeQuery(sql)
                .setParameter("usuarioId", usuarioId)
                .setParameter("badgeId", badgeId)
                .getSingleResult();

        return result != null && result;
    }
}
