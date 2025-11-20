package com.eam.capacitaciones.dao;

import com.eam.capacitaciones.domain.entity.Usuario;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@Slf4j
public class CustomUsuarioDAOImpl implements CustomUsuarioDAO {
    
    @PersistenceContext
    private EntityManager entityManager;
    
    @Override
    public List<Usuario> buscarPorCriterios(String nombre, String departamento, String rol) {
        log.debug("Buscando usuarios con criterios - Nombre: {}, Dept: {}, Rol: {}", nombre, departamento, rol);
        
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Usuario> query = cb.createQuery(Usuario.class);
        Root<Usuario> usuario = query.from(Usuario.class);
        
        List<Predicate> predicates = new ArrayList<>();
        
        if (nombre != null && !nombre.isEmpty()) {
            predicates.add(cb.like(cb.lower(usuario.get("nombre")), "%" + nombre.toLowerCase() + "%"));
        }
        
        if (departamento != null && !departamento.isEmpty()) {
            predicates.add(cb.equal(usuario.get("departamento"), departamento));
        }
        
        if (rol != null && !rol.isEmpty()) {
            predicates.add(cb.equal(usuario.get("rol"), rol));
        }
        
        predicates.add(cb.equal(usuario.get("activo"), true));
        
        query.where(predicates.toArray(new Predicate[0]));
        
        return entityManager.createQuery(query).getResultList();
    }
    
    @Override
    public List<Usuario> obtenerUsuariosConMasCursosCompletados(int limite) {
        log.debug("Obteniendo top {} usuarios con más cursos completados", limite);
        
        String jpql = """
            SELECT u FROM Usuario u
            LEFT JOIN u.inscripciones i
            WHERE i.estado = 'COMPLETADO'
            GROUP BY u.idUsuario
            ORDER BY COUNT(i.idInscripcion) DESC
            """;
        
        return entityManager.createQuery(jpql, Usuario.class)
                .setMaxResults(limite)
                .getResultList();
    }
    
    @Override
    public long contarUsuariosActivos() {
        log.debug("Contando usuarios activos");
        
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Usuario> usuario = query.from(Usuario.class);
        
        query.select(cb.count(usuario));
        query.where(cb.equal(usuario.get("activo"), true));
        
        return entityManager.createQuery(query).getSingleResult();
    }
    
    @Override
    public List<Usuario> buscarPorDepartamentoYRol(String departamento, String rol) {
        log.debug("Buscando usuarios - Departamento: {}, Rol: {}", departamento, rol);
        
        String jpql = "SELECT u FROM Usuario u WHERE u.departamento = :dept AND u.rol = :rol AND u.activo = true";
        
        return entityManager.createQuery(jpql, Usuario.class)
                .setParameter("dept", departamento)
                .setParameter("rol", rol)
                .getResultList();
    }
}