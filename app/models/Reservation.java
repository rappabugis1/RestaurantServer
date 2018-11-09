package models;

import io.ebean.Model;
import io.ebean.config.JsonConfig;

import javax.persistence.*;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "reservations")
public class Reservation extends Model {
    @Id
    Long id;

    @Column(nullable = false)
    private int persons;

    @Column(nullable = false, name = "reservation_date_time")
    private String reservationDateTime;

    @Column
    private String request;

    @Column
    private Boolean temp;

    public Boolean getTemp() {
        return temp;
    }

    public void setTemp(Boolean temp) {
        this.temp = temp;
    }

    public Timestamp getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Timestamp timeCreated) {
        this.timeCreated = timeCreated;
    }

    @Column(nullable = false, name="time_created")
    private Timestamp timeCreated;

    @ManyToOne(cascade = CascadeType.ALL, optional = false)
    private User user;

    @ManyToOne (cascade = CascadeType.ALL, optional = false)
    private Restaurant restaurant;

    public int getPersons() {
        return persons;
    }

    public void setPersons(int persons) {
        this.persons = persons;
    }

    public String getReservationDateTime() {
        return reservationDateTime;
    }

    public void setReservationDateTime(String reservationDateTime) {
        this.reservationDateTime = reservationDateTime;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }
}
