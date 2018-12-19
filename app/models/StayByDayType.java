package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.ebean.Finder;
import io.ebean.Model;
import javax.persistence.*;
import javax.persistence.Table;

@Entity
@Table(name = "stay_by_day_type")
public class StayByDayType extends Model {

    @Id
    public Long id;

    public static final Finder<Long, StayByDayType> finder = new Finder<>(StayByDayType.class);

    @Column(nullable = false, name="day_type")
    private String dayType;

    @Column(nullable = false)
    private int morning;

    @Column(nullable = false)
    private int day;

    @Column(nullable = false)
    private int evening;

    @ManyToOne(cascade = CascadeType.PERSIST,optional = false)
    @JsonIgnore
    private GuestStay guestStay;

    public int getMorning() {
        return morning;
    }

    public void setMorning(int morning) {
        this.morning = morning;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getEvening() {
        return evening;
    }

    public void setEvening(int evening) {
        this.evening = evening;
    }

    public GuestStay getGuestStay() {
        return guestStay;
    }

    public void setGuestStay(GuestStay guestStay) {
        this.guestStay = guestStay;
    }

    public static Finder<Long, StayByDayType> getFinder() {
        return finder;
    }

    public String getDayType() {
        return dayType;
    }

    public void setDayType(String dayType) {
        this.dayType = dayType;
    }

    public StayByDayType(int morning, int day, int evening, GuestStay guestStay, String dayType) {
        this.morning = morning;
        this.day = day;
        this.evening = evening;
        this.guestStay = guestStay;
        this.dayType=dayType;
    }
}
