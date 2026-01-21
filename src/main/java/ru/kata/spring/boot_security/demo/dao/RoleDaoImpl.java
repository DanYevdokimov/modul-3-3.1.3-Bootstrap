package ru.kata.spring.boot_security.demo.dao;

import org.springframework.stereotype.Repository;
import ru.kata.spring.boot_security.demo.model.Role;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Repository
public class RoleDaoImpl implements RoleDao {
    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Role> getAllRoles() {
        return em.createQuery("select r from Role r", Role.class).getResultList();
    }

    @Override
    public Optional<Role> getRoleByName(String roleName) {
        TypedQuery<Role> query = em.createQuery(
                "select r from Role r where r.name = :roleName", Role.class);
        query.setParameter("roleName", roleName);
        List<Role> roles = query.getResultList();
        return roles.isEmpty() ? Optional.empty() : Optional.of(roles.get(0));
    }

    @Override
    public void saveRole(Role role) {
        em.persist(role);
    }
}