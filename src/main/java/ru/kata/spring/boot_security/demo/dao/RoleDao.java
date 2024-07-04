package ru.kata.spring.boot_security.demo.dao;

import ru.kata.spring.boot_security.demo.entity.Role;

import java.util.List;

public interface RoleDao {
    Role getRoleById(int id);

    Role getRoleByName(String name);

    List<Role> getAllRoles();

    Role saveRole(Role role);

    void deleteRoleById(int id);

    List<Role> findByIds(List<Integer> ids);
}
