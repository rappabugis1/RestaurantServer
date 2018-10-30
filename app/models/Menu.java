package models;

import io.ebean.Finder;
import io.ebean.Model;

import javax.persistence.*;
import javax.persistence.Table;


@Entity
@Table(name = "menus")
public class Menu extends Model {


    @Id
    public Long id;

    @Column(nullable = false, unique = true)
    private String type;

    @ManyToOne(cascade = CascadeType.ALL,optional = false)
    Restaurant restaurant;

    public static final Finder<Long, Menu> finder = new Finder<>(Menu.class);

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public static Finder<Long, Menu> getFinder() {
        return finder;
    }
}
