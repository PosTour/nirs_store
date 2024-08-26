package ru.bmstu.nirs.store.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "category")
@NoArgsConstructor
@Getter
@Setter
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "name")
    @Pattern(regexp = "[А-Я]\\w+",
            message = "Название категории должно начинаться с большой буквы")
    private String name;

    @OneToMany(mappedBy = "category")
    private List<Item> items;

    public Category(String name) {
        this.name = name;
    }
}
