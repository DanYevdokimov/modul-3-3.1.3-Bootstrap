package ru.kata.spring.boot_security.demo.init;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserService userService;

    @PersistenceContext
    private EntityManager em;

    @Autowired
    public DataInitializer(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void run(String... args) {

        Role adminRole = getRoleByName("ROLE_ADMIN")
                .orElseGet(() -> {
                    Role newRole = new Role("ROLE_ADMIN");
                    saveRole(newRole);
                    return newRole;
                });

        Role userRole = getRoleByName("ROLE_USER")
                .orElseGet(() -> {
                    Role newRole = new Role("ROLE_USER");
                    saveRole(newRole);
                    return newRole;
                });


        Optional<User> adminUserOpt = findUserByUsername("admin@mail.ru");
        if (adminUserOpt.isEmpty()) {
            User adminUser = new User();
            adminUser.setUsername("admin@mail.ru");
            adminUser.setPassword("admin");
            adminUser.setName("Alex");
            adminUser.setLastName("Smith");
            adminUser.setAge((byte) 12);
            Set<Role> adminRoles = new HashSet<>();
            adminRoles.add(adminRole);
            adminRoles.add(userRole);
            String[] roleNames = adminRoles.stream()
                    .map(Role::getName)
                    .toArray(String[]::new);
            userService.saveUser(adminUser, roleNames);
        }

        Optional<User> regularUserOpt = findUserByUsername("user@mail.ru");
        if (regularUserOpt.isEmpty()) {
            User regularUser = new User();
            regularUser.setUsername("user@mail.ru");
            regularUser.setPassword("admin");
            regularUser.setName("Adam");
            regularUser.setLastName("Black");
            regularUser.setAge((byte) 60);
            Set<Role> userRoles = new HashSet<>();
            userRoles.add(userRole);
            String[] roleNames = userRoles.stream()
                    .map(Role::getName)
                    .toArray(String[]::new);
            userService.saveUser(regularUser, roleNames);
        }
    }


    private Optional<Role> getRoleByName(String roleName) {
        return em.createQuery("SELECT r FROM Role r WHERE r.name = :roleName", Role.class)
                .setParameter("roleName", roleName)
                .getResultList()
                .stream()
                .findFirst();
    }


    private void saveRole(Role role) {
        em.persist(role);
    }


    private Optional<User> findUserByUsername(String username) {
        return em.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class)
                .setParameter("username", username)
                .getResultList()
                .stream()
                .findFirst();
    }
}