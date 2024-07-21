package ru.kata.spring.boot_security.demo.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import ru.kata.spring.boot_security.demo.entity.Role;
import ru.kata.spring.boot_security.demo.entity.User;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class UserDaolmp implements UserDao {

    @PersistenceContext
    private EntityManager entityManager;

    private final RoleDao roleDao;

    private final PasswordEncoder passwordEncoder;;

    public UserDaolmp(RoleDao roleDao, @Lazy PasswordEncoder passwordEncoder) {
        this.roleDao = roleDao;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<User> getUsersList() {
        return entityManager.createQuery("select distinct u from User u left join fetch u.roles", User.class).getResultList();
    }

    @Override
    public User getUser(int id) {
        return Optional.ofNullable(entityManager.find(User.class, id))
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с id=" + id + " не найден"));
    }

    @Override
    public boolean addUser(User user) {
        // Проверяем, существует ли пользователь с данным именем
        boolean userExists = entityManager.createQuery("SELECT COUNT(u) FROM User u WHERE u.username = :username", Long.class)
                .setParameter("username", user.getUsername())
                .getSingleResult() > 0;

        if (userExists) {
            return false; // Пользователь уже существует
        }

        // Сохраняем или используем существующие роли
        List<Role> savedRoles = user.getRoles().stream()
                .map(role -> Optional.ofNullable(roleDao.getRoleByName(role.getRoleName()))
                        .orElseGet(() -> roleDao.saveRole(role)))
                .collect(Collectors.toList());

        user.setRoles(savedRoles); // Присваиваем сохраненные роли пользователю
        entityManager.persist(user); // Сохраняем пользователя
        return true; // Успешно добавлено
    }


    @Override
    public void deleteUser(int id) {
        User user = entityManager.find(User.class, id);
        if (user == null) {
            throw new EntityNotFoundException("Пользователь с id=" + id + " не найден");
        }
        entityManager.remove(user);
    }

    @Override
    public void editUser(User user) {
        User existingUser = entityManager.find(User.class, user.getId());
        if (existingUser == null) {
            throw new EntityNotFoundException("Пользователь с id=" + user.getId() + " не найден");
        }

        // Обновляем поля пользователя
        existingUser.setAge(user.getAge());
        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setUsername(user.getUsername());

        // Обновляем роли
        List<Role> updatedRoles = user.getRoles().stream()
                .map(role -> Optional.ofNullable(roleDao.getRoleByName(role.getRoleName()))
                        .orElseGet(() -> roleDao.saveRole(role)))
                .collect(Collectors.toList());

        existingUser.setRoles(updatedRoles); // Устанавливаем актуальные роли

        // Проверка и обновление пароля
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        entityManager.merge(existingUser); // Сохраняем изменения
    }

    @Override
    public User findByUsername(String username) {
        return entityManager.createQuery("select u from User u where u.username = :username", User.class)
                .setParameter("username", username)
                .getSingleResult();
    }

}