package ru.bmstu.nirs.store.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "basket")
@NoArgsConstructor
@Getter
@Setter
public class Basket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "id")
    private User user;

    @ManyToMany
    @JoinTable(
            name = "basket_item",
            joinColumns = @JoinColumn(name = "basket_id"),
            inverseJoinColumns = @JoinColumn(name = "item_id"))
    private List<Item> items;

    @Transient
    private Map<Item, Integer> quantities;

    @Transient
    private BigDecimal totalAmount;

    public Basket(User user) {
        this.user = user;
    }
}