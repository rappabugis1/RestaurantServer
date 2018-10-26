package models;


import io.ebean.Finder;
import io.ebean.Model;
import play.data.validation.Constraints;

import javax.persistence.*;

@Entity
@Table(name = "user_data")
public class UserData extends Model {

    @Id
    public Long id;

    public UserData(@Constraints.Required String firstName, @Constraints.Required String lastName, @Constraints.Required @Constraints.MinLength(9) String phonevb) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        //this.location = location;
    }

    @Column (name="firstName",nullable = false)
    @Constraints.Required
    private String firstName;

    @Column (name="last_name", nullable = false)
    @Constraints.Required
    private String lastName;

    @Column (nullable = false)
    @Constraints.Required
    @Constraints.MinLength(9)
    private String phone;

    @OneToOne(cascade = CascadeType.ALL)
    private User user;

    //@ManyToOne(optional = false)
    //Location location;


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

    //public Location getLocation() {
    //    return location;
    //}

    //public void setLocation(Location location) {
    //    this.location = location;
    //}

    public static final Finder<Long, UserData> finder = new Finder<>(UserData.class);

    public static Finder<Long, UserData> getFinder() {
        return finder;
    }
}
