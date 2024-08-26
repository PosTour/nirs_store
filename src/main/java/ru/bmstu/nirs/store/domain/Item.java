package ru.bmstu.nirs.store.domain;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "item")
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "purchase_price", precision = 10, scale = 2)
    private BigDecimal purchasePrice;

    @Column(name = "selling_price", precision = 10, scale = 2)
    private BigDecimal sellingPrice;

    @ManyToOne()
    @JoinColumn(name = "category", referencedColumnName = "id")
    private Category category;

    @ManyToMany(mappedBy = "items")
    private List<Order> orders;

    @ManyToMany(mappedBy = "items")
    private List<Basket> baskets;

    public Item(String name, String description, BigDecimal purchasePrice, BigDecimal sellingPrice, Category category) {
        this.name = name;
        this.description = description;
        this.purchasePrice = purchasePrice;
        this.sellingPrice = sellingPrice;
        this.category = category;
    }
}
