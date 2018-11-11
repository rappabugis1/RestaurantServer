package models;


import io.ebean.Finder;
import io.ebean.Model;

import javax.persistence.*;
import javax.persistence.Table;
import javax.validation.constraints.Size;

@Entity
@Table(name = "user_data")
public class UserData extends Model {

    @Id
    public Long id;

    public UserData( String firstName, String lastName, String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
    }

    @Column (name="firstName",nullable = false)
    private String firstName;

    @Column (name="last_name", nullable = false)
    private String lastName;

    @Column (nullable = false)
    @Size(min=6)
    private String phone;

    @OneToOne
    private User user;

    @ManyToOne(optional = false)
    Location location;


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public static final Finder<Long, UserData> finder = new Finder<>(UserData.class);

    public static Finder<Long, UserData> getFinder() {
        return finder;
    }
}
