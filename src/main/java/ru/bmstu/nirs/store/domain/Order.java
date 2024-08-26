package ru.bmstu.nirs.store.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "order")
@NoArgsConstructor
@Getter
@Setter
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "id")
    private Client customer;

    @Column(name = "city")
    private String city;

    @Column(name = "address")
    private String address;

    @Column(name = "order_date")
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date orderDate;

    @ManyToMany
    @JoinTable(
            name = "order_idem",
            joinColumns = @JoinColumn(name = "order_id"),
            inverseJoinColumns = @JoinColumn(name = "item_id"))
    private List<Item> items;

    public Order(Client customerId, String city, String address, Date orderDate) {
        this.customer = customerId;
        this.city = city;
        this.address = address;
        this.orderDate = orderDate;
    }
}
