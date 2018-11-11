package models;

import io.ebean.Finder;
import io.ebean.Model;

import javax.persistence.*;
import javax.persistence.Table;

@Entity
@Table(name = "dishes")
public class Dish extends Model {

    @Id
    public Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private int price;

    @ManyToOne(optional = false)
    Menu menu;

    @ManyToOne( optional = false)
    DishType type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public Menu getMenu() {
        return menu;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }

    public String  getType() {

        return type.getType();
    }

    public void setType(DishType type) {
        this.type = type;
    }

    public static Finder<Long, Dish> getFinder() {
        return finder;
    }

    public static final Finder<Long, Dish> finder = new Finder<>(Dish.class);
}
