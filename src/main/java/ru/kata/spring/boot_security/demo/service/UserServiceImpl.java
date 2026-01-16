package ru.kata.spring.boot_security.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.dao.UserDao;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    @PersistenceContext
    private EntityManager em;

    @Autowired
    public UserServiceImpl(UserDao userDao, RoleService roleService, PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<User> getAllUsers() {
        return userDao.getAllUsers();
    }

    @Override
    public Optional<User> getUserById(long id) {
        return userDao.getUserById(id);
    }

    @Override
    @Transactional
    public void saveUser(User user, String[] newRoles) {
        Optional<User> existingUser = findByUsername(user.getUsername());
        if (existingUser.isPresent()) {
            throw new RuntimeException("User with username " + user.getUsername() + " already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        setUserRoles(user, newRoles);
        userDao.saveUser(user);
    }

    @Override
    @Transactional
    public void deleteUser(long id) {
        getUserById(id).orElseThrow(() ->
                new EntityNotFoundException("User with id " + id + " not found"));
        userDao.deleteUser(id);
    }

    private void setUserRoles(User user, String[] selectedRoles) {
        if (selectedRoles != null) {
            Set<Role> roleSet = new HashSet<>();
            for (String roleName : selectedRoles) {
                roleService.getRoleByName(roleName)
                        .ifPresent(roleSet::add);
            }
            user.setRoles(roleSet);
        }
    }

    @Transactional
    @Override
    public void updateUser(long id, User user, String[] selectedRoles) {
        User existingUser = getUserById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + id + " not found"));

        existingUser.setName(user.getName());
        existingUser.setLastName(user.getLastName());
        existingUser.setAge(user.getAge());

        if (!existingUser.getUsername().equals(user.getUsername())) {

            Optional<User> userWithNewUsername = findByUsername(user.getUsername());
            if (userWithNewUsername.isPresent() && userWithNewUsername.get().getId() != id) {
                throw new RuntimeException("User with username " + user.getUsername() + " already exists");
            }
            existingUser.setUsername(user.getUsername());
        }


        if (user.getPassword() != null && !user.getPassword().trim().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        setUserRoles(existingUser, selectedRoles);
        userDao.updateUser(existingUser);
    }


    public Optional<User> findByUsername(String username) {
        return em.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class)
                .setParameter("username", username)
                .getResultList()
                .stream()
                .findFirst();
    }
}