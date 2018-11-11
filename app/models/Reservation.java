package models;

import io.ebean.Finder;
import io.ebean.Model;
import javax.persistence.*;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "reservations")
public class Reservation extends Model {
    @Id
    public Long id;

    @Column(nullable = false)
    private int persons;

    @Column(nullable = false, name = "reservation_date_time")
    private Timestamp reservationDateTime;

    @Column(nullable = false, name = "reservation_end_date_time")
    private Timestamp reservationEndDateTime;

    @Column
    private String request;

    @Column
    private Boolean temp;

    @Column(nullable = false, name="time_created")
    private Timestamp timeCreated;

    @ManyToOne( optional = false)
    private User user;

    @ManyToOne ( optional = false)
    private Restaurant restaurant;

    @ManyToOne( optional = false)
    private models.Table table;

    public static final Finder<Long,Reservation> finder = new Finder<>(Reservation.class);

    public Reservation(int persons, Timestamp reservationDateTime, String request, Boolean temp, Timestamp endTime, Timestamp timeCreated) {
        this.persons = persons;
        this.reservationDateTime = reservationDateTime;
        this.request = request;
        this.temp = temp;
        this.reservationEndDateTime = endTime;
        this.timeCreated = timeCreated;
    }

    public static Finder<Long, Reservation> getFinder() {
        return finder;
    }

    public models.Table getTable() {
        return table;
    }

    public void setTable(models.Table table) {
        this.table = table;
    }

    public Timestamp getReservationEndDateTime() {
        return reservationEndDateTime;
    }

    public void setReservationEndDateTime(Timestamp reservationEndDateTime) {
        this.reservationEndDateTime = reservationEndDateTime;
    }

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


    public int getPersons() {
        return persons;
    }

    public void setPersons(int persons) {
        this.persons = persons;
    }

    public Timestamp getReservationDateTime() {
        return reservationDateTime;
    }

    public void setReservationDateTime(Timestamp reservationDateTime) {
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
