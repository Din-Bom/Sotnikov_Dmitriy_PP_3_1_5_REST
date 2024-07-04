package ru.kata.spring.boot_security.demo.dao;


import ru.kata.spring.boot_security.demo.entity.User;

import java.util.List;

public interface UserDao {
    List<User> getUsersList();

    User getUser(int id);

    boolean addUser(User user);

    void deleteUser(int id);

    void editUser(User user);

    User findByUsername(String username);
}
