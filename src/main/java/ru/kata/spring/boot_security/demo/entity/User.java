package ru.kata.spring.boot_security.demo.entity;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;


@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "first_name")
    @Size(min = 2, message = "Имя не может содержать меньше двух букв")
    @Size(max = 30, message = "Имя не может содержать больше тридцати букв")
    @NotEmpty(message = "Пользователь не может быть создан без имени")
    private String firstName;

    @Column(name = "last_name")
    @Size(min = 2, message = "Имя не может содержать меньше двух букв")
    @Size(max = 30, message = "Имя не может содержать больше тридцати букв")
    @NotEmpty(message = "Пользователь не может быть создан без фамилии")
    private String lastName;

    @Column(name = "age")
    @Min(1)
    @Max(100)
    @NotNull(message = "Возразст не может быть пустым")
    private int age;

    @Column(name = "username", unique = true)
    @Size(min = 2, message = "Имя пользователя не может содержать меньше двух букв")
    @Size(max = 30, message = "Имя пользователя не может содержать больше тридцати букв")
    @NotEmpty(message = "Пользователь не может быть создан без имени пользователя")
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @ManyToMany(fetch = FetchType.LAZY)
    @Fetch(FetchMode.JOIN)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private List<Role> roles = new ArrayList<>();

    public User() {
    }

    public User(String firstName, String lastName, int age, String username, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.username = username;
        this.password = password;
    }

    public String getRoleNames() {
        return roles.stream()
                .map(role -> role.getRoleName()
                        .replace("ROLE_", ""))
                .collect(Collectors.joining(" "));
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public @Size(min = 2, message = "Имя не может содержать меньше двух букв") @Size(max = 30, message = "Имя не может содержать больше тридцати букв") @NotEmpty(message = "Пользователь не может быть создан без имени") String getFirstName() {
        return firstName;
    }

    public void setFirstName(@Size(min = 2, message = "Имя не может содержать меньше двух букв") @Size(max = 30, message = "Имя не может содержать больше тридцати букв") @NotEmpty(message = "Пользователь не может быть создан без имени") String firstName) {
        this.firstName = firstName;
    }

    public @Size(min = 2, message = "Имя не может содержать меньше двух букв") @Size(max = 30, message = "Имя не может содержать больше тридцати букв") @NotEmpty(message = "Пользователь не может быть создан без фамилии") String getLastName() {
        return lastName;
    }

    public void setLastName(@Size(min = 2, message = "Имя не может содержать меньше двух букв") @Size(max = 30, message = "Имя не может содержать больше тридцати букв") @NotEmpty(message = "Пользователь не может быть создан без фамилии") String lastName) {
        this.lastName = lastName;
    }

    @Min(1)
    @Max(100)
    @NotNull(message = "Возразст не может быть пустым")
    public int getAge() {
        return age;
    }

    public void setAge(@Min(1) @Max(100) @NotNull(message = "Возразст не может быть пустым") int age) {
        this.age = age;
    }

    @Override
    public @Size(min = 2, message = "Имя пользователя не может содержать меньше двух букв") @Size(max = 30, message = "Имя пользователя не может содержать больше тридцати букв") @NotEmpty(message = "Пользователь не может быть создан без имени пользователя") String getUsername() {
        return username;
    }

    public void setUsername(@Size(min = 2, message = "Имя пользователя не может содержать меньше двух букв") @Size(max = 30, message = "Имя пользователя не может содержать больше тридцати букв") @NotEmpty(message = "Пользователь не может быть создан без имени пользователя") String username) {
        this.username = username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    /*__________________________________________________________________________________________*/

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        for (Role role : this.roles) {
            authorities.add(new SimpleGrantedAuthority(role.getRoleName()));
        }
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id && age == user.age && Objects.equals(firstName, user.firstName) && Objects.equals(lastName, user.lastName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, age);
    }

    @Override
    public String toString() {
        return "User{" +
                "roleSet=" + roles.toString() +
                ", username='" + username + '\'' +
                ", age=" + age +
                ", lastName='" + lastName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", id=" + id +
                '}';
    }
}
