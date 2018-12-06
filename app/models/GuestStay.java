package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.ebean.Finder;
import io.ebean.Model;

import javax.persistence.*;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "guest_stay")
public class GuestStay extends  Model{

    @Id
    public Long id;

    public static final Finder<Long, GuestStay> finder = new Finder<>(GuestStay.class);


    @Column(nullable = false, name = "guest_number")
    private int guestNumber;

    @ManyToOne(cascade = CascadeType.ALL, optional = false)
    Restaurant restaurant;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "guestStay")
    @JsonIgnore
    List<StayByDayType> stayByDays;

    public int getGuestNumber() {
        return guestNumber;
    }

    public void setGuestNumber(int guestNumber) {
        this.guestNumber = guestNumber;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public List<StayByDayType> getStayByDays() {
        return stayByDays;
    }

    public void setStayByDays(List<StayByDayType> stayByDays) {
        this.stayByDays = stayByDays;
    }

    public static Finder<Long, GuestStay> getFinder() {
        return finder;
    }
}
