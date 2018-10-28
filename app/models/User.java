package models;

import io.ebean.Finder;
import io.ebean.Model;
import org.hibernate.validator.constraints.Email;
import play.data.validation.Constraints;

import javax.persistence.*;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Entity
@Table (name = "users")
public class User extends Model {

    @Id
    public Long id;


    public User(String email, String password,  String user_type) {
        this.email = email;
        this.password = password;
        this.user_type = user_type;
    }

    @Column (nullable = false, unique = true)
    @Email
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(nullable = false)
    private String user_type;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "user")
    private UserData user_data;

    public static final Finder<Long, User> finder= new Finder<>(User.class);


    public UserData getUser_data() {
        return user_data;
    }

    public void setUser_data(UserData user_data) {
        this.user_data = user_data;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public static Finder<Long, User> getFinder() {
        return finder;
    }

    public String getUser_type() {
        return user_type;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }

}
