package models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.ebean.Finder;
import io.ebean.Model;

import javax.persistence.*;
import java.util.List;

@Entity
@javax.persistence.Table(name = "tables")
public class Table extends Model {

    public Table(int sitting_places, Restaurant restaurant) {
        this.sitting_places = sitting_places;
        this.restaurant = restaurant;
    }

    @Id
    public Long id;

    @Column(nullable = false)
    private int sitting_places;

    @ManyToOne(cascade = CascadeType.PERSIST, optional = false)
    @JsonIgnore
    Restaurant restaurant;

    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "table")
    @JsonIgnore
    List<Reservation> reservations;

    @JsonProperty("restaurantId")
    private Long restiD (){
        return restaurant.id;
    }

    public static final Finder<Long, Table> finder = new Finder<>(Table.class);

    public int getSitting_places() {
        return sitting_places;
    }

    public void setSitting_places(int sitting_places) {
        this.sitting_places = sitting_places;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public static Finder<Long, Table> getFinder() {
        return finder;
    }
}
