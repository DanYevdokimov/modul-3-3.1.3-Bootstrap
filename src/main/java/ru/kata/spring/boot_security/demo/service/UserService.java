
package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> getAllUsers();
    Optional<User> getUserById(long id);
    void saveUser(User user, String[] newRoles);
    void deleteUser(long id);
    void updateUser(long id, User user, String[] selectedRoles);


    Optional<User> findByUsername(String username);
}