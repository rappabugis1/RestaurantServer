package models;

import io.ebean.Model;

import javax.persistence.*;
import javax.persistence.Table;

@Entity
@Table(name = "reservations")
public class Reservation extends Model {
    @Id
    Long id;

    @Column(nullable = false)
    private int persons;

    @Column(nullable = false, name = "reservation_date_time")
    private String reservationDateTime;

    @ManyToOne(cascade = CascadeType.ALL)
    private User user;

    @ManyToOne (cascade = CascadeType.ALL)
    private Restaurant restaurant;
}
