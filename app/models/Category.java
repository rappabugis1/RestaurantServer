package models;

import com.fasterxml.jackson.annotation.JsonValue;
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

    @ManyToMany(mappedBy = "categories")
    List<Restaurant> restaurants ;

    public static final Finder<Long, Category> finder = new Finder<>(Category.class);

    public String getName() {
        return name;
    }

    public Category(int id) {

    }
    public Category(){

    }

    public void setName(String name) {
        this.name = name;
    }

    public static Finder<Long, Category> getFinder() {
        return finder;
    }


}
