package models;

import io.ebean.Finder;
import io.ebean.Model;

import javax.persistence.*;
import javax.persistence.Table;
import java.util.List;


@Entity
@Table(name = "menus")
public class Menu extends Model {

    public Menu(String type, Restaurant restaurant) {
        this.type = type;
        this.restaurant = restaurant;
    }

    @Id
    public Long id;

    @Column(nullable = false)
    private String type;

    @ManyToOne(cascade = CascadeType.ALL, optional = false)
    Restaurant restaurant;

    @OneToMany(mappedBy = "menu")
    List<Dish> dishes;

    public List<Dish> getDishes() {
        return dishes;
    }

    public void setDishes(List<Dish> dishes) {
        this.dishes = dishes;
    }

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
