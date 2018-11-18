package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.ebean.Finder;
import io.ebean.Model;
import javax.persistence.*;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

@Entity
@Table(name = "reservations")
public class Reservation extends Model {
    @Id
    @JsonIgnore
    public Long id;

    @Column(nullable = false)
    @JsonProperty("guests")
    private int persons;

    @Column(nullable = false, name = "reservation_date_time")
    @JsonIgnore
    private Timestamp reservationDateTime;

    @Column(nullable = false, name = "reservation_end_date_time")
    @JsonIgnore
    private Timestamp reservationEndDateTime;

    @Column
    private String request;

    @Column
    @JsonIgnore
    private Boolean temp;

    @Column(nullable = false, name="time_created")
    @JsonIgnore
    private Timestamp timeCreated;

    @ManyToOne( optional = false)
    @JsonIgnore
    private User user;

    @ManyToOne (optional = false)
    @JsonIgnore
    private Restaurant restaurant;

    @ManyToOne( optional = false)
    @JsonIgnore
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

    @JsonProperty("restaurantName")
    private String getRestaurantName (){
        return this.restaurant.getRestaurantName();
    }

    @JsonProperty("imageFileName")
    private String getRestImage(){
        return this.restaurant.getImageFileName();
    }

    @JsonProperty("reservationDate")
    private String getDate (){
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy");
        return dateFormat.format(this.reservationDateTime);
    }

    @JsonProperty("reservationHour")
    private String getHour (){
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        return dateFormat.format(this.reservationDateTime);
    }

    @JsonProperty("reservationDuration")
    private long getDuration (){
        return  ((this.reservationEndDateTime.getTime()-this.reservationDateTime.getTime())/1000)/60;
    }
}
