package models;

import io.ebean.Finder;
import io.ebean.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

@Entity
@Table(name = "dish_types")
public class DishType extends Model {

    @Id
    public Long id;

    @Column(nullable = false, unique = true)
    private String type;

    public static final Finder<Long, DishType> finder = new Finder<>(DishType.class);

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public static Finder<Long, DishType> getFinder() {
        return finder;
    }
}
