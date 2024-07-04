package ru.kata.spring.boot_security.demo.service;


import ru.kata.spring.boot_security.demo.entity.User;

import java.util.List;

public interface UserService {
    List<User> getUsersList();

    User getUser(int id);

    boolean addUser(User user);

    void deleteUser(int id);

    void editUser(User user);

    User findUserByUsername(String username);

    User findUserById(int userId);
}
