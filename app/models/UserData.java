package models;


import io.ebean.Finder;
import io.ebean.Model;
import play.data.validation.Constraints;

import javax.persistence.*;
import javax.validation.Constraint;

@Entity
@Table(name = "user_data")
public class UserData extends Model {

    @Id
    public Long id;

    public UserData(@Constraints.Required String first_name, @Constraints.Required String last_name, @Constraints.Required @Constraints.MinLength(9) String phone, Location location) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.phone = phone;
        this.location = location;
    }

    @Column (nullable = false)
    @Constraints.Required
    private String first_name;

    @Column (nullable = false)
    @Constraints.Required
    private String last_name;

    @Column (nullable = false)
    @Constraints.Required
    @Constraints.MinLength(9)
    private String phone;


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @OneToOne(cascade = CascadeType.ALL)
    private User user;

    @ManyToOne(optional = false)
    Location location;


    public static final Finder<Long, UserData> finder = new Finder<>(UserData.class);

}
