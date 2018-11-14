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
    public Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @ManyToMany
    List<Restaurant> restaurants ;

    public static final Finder<Long, Category> finder = new Finder<>(Category.class);

    public String getName() {
        return name;
    }

    public Category(){

    }

    public Category(String name) {
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static Finder<Long, Category> getFinder() {
        return finder;
    }


}
