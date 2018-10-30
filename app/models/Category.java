package models;

import io.ebean.Finder;
import io.ebean.Model;

import javax.persistence.*;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "categories")
public class Category extends Model {

    @Id
    Long id;

    @Column(nullable = false, unique = true)
    private String foodType;

    @ManyToMany(mappedBy = "categories")
    List<Restaurant> restaurants ;

    public static final Finder<Long, Category> finder = new Finder<>(Category.class);

    public String getFoodType() {
        return foodType;
    }

    public void setFoodType(String foodType) {
        this.foodType = foodType;
    }

    public static Finder<Long, Category> getFinder() {
        return finder;
    }
}
