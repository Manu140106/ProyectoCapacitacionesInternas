package com.eam.capacitaciones.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
@Slf4j
public class CustomAuthDAOImpl implements CustomAuthDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Map<String, Object> loginPorEmail(String email) {
        log.debug("Buscando usuario con email: {}", email);

        String sql = """
            SELECT u.idUsuario, u.nombre, u.email, u.password, u.rol, u.departamento
            FROM Usuario u
            WHERE u.email = :email
        """;

        try {
            Object[] result = (Object[]) entityManager
                    .createNativeQuery(sql)
                    .setParameter("email", email)
                    .getSingleResult();

            Map<String, Object> usuario = new HashMap<>();
            usuario.put("idUsuario", result[0]);
            usuario.put("nombre", result[1]);
            usuario.put("email", result[2]);
            usuario.put("password", result[3]);
            usuario.put("rol", result[4]);
            usuario.put("departamento", result[5]);

            return usuario;

        } catch (NoResultException e) {
            log.warn("No existe usuario con ese email");
            return null;
        }
    }

    @Override
    public boolean emailExiste(String email) {
        String sql = "SELECT COUNT(*) FROM Usuario WHERE email = :email";

        Long count = ((Number) entityManager
                .createNativeQuery(sql)
                .setParameter("email", email)
                .getSingleResult()).longValue();

        return count > 0;
    }
}