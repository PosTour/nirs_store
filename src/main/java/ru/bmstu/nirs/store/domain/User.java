package ru.bmstu.nirs.store.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_table")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private int id;

    @NotEmpty(message = "Поле логина должно быть заполнено")
    @Size(min = 2, max = 100, message = "Логин содержать от 5 до 100 символов")
    @Column(name = "username")
    private String username;

    @Column(name = "name")
    private String name;

    @Column(name = "surname")
    private String surname;

    @Column(name = "phone")
    private String phone;

    @Column(name = "password")
    private String password;

    @Column(name = "role")
    private String role;

    public User(String username, String name, String password, String phone, String role, String surname) {
        this.username = username;
        this.name = name;
        this.password = password;
        this.phone = phone;
        this.role = role;
        this.surname = surname;
    }
}