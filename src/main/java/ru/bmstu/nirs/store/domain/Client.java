package ru.bmstu.nirs.store.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "client")
@NoArgsConstructor
@Getter
@Setter
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "full_name")
    @Pattern(regexp = "[А-Я]\\w+ [А-Я]\\w+ [А-Я]\\w+",
            message = "Полное имя имеет следующий вид: 'Фамилия Имя Отчество'")
    private String name;

    @Column(name = "phone")
    @Pattern(regexp = "^\\d{11}$",
            message = "Телефон записывается начинается с цифры 8 и содержит 11 символов")
    private String phone;

    @Column(name = "email")
    @Email(message = "Некорректный адрес электронной почты")
    private String email;

    @OneToMany(mappedBy = "customer")
    private List<Order> orders;

    public Client(String name, String phone, String email) {
        this.name = name;
        this.phone = phone;
        this.email = email;
    }
}
