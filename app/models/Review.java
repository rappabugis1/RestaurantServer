package models;

import io.ebean.Finder;
import io.ebean.Model;

import javax.persistence.*;
import javax.persistence.Table;

@Entity
@Table(name="reviews")
public class Review extends Model {

    @Id
    Long id;

    @Column(nullable = false)
    private int mark;

    @Column(nullable = false)
    private String comment;

    @Column(nullable = false, name = "insert_time")
    private String insertTime;

    @ManyToOne (optional = false)
     User user;

    @ManyToOne (optional = false)
     Restaurant restaurant;

    public int getMark() {
        return mark;
    }

    public void setMark(int mark) {
        this.mark = mark;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(String insertTime) {
        this.insertTime = insertTime;
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

    public static Finder<Long, Review> getFinder() {
        return finder;
    }

    public static final Finder<Long, Review> finder = new Finder<>(Review.class);

}
