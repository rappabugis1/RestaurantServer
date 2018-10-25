package models;

import io.ebean.Finder;
import io.ebean.Model;
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


    public User(@Constraints.Required @Constraints.Email String email, @Constraints.Required byte[] shaPassword, @Constraints.Required boolean user_type) {
        this.email = email;
        this.shaPassword = shaPassword;
        this.user_type = user_type;
    }

    @Column (nullable = false)
    @Constraints.Required
    @Constraints.Email
    private String email;

    @Column(name = "sha_password", nullable = false)
    @Constraints.Required
    private byte[] shaPassword;

    @Column(nullable = false)
    @Constraints.Required
    private boolean user_type;

    public UserData getUser_data() {
        return user_data;
    }

    public void setUser_data(UserData user_data) {
        this.user_data = user_data;
    }

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "user")
    private UserData user_data;

    public static final Finder<Long, User> finder= new Finder<>(User.class);


    public static byte[] getSha512(String value) {
        try {
            return MessageDigest.getInstance("SHA-512").digest(value.getBytes("UTF-8"));
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
