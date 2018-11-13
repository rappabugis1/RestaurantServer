package models;
import com.fasterxml.jackson.annotation.JsonValue;
import io.ebean.Finder;
import io.ebean.Model;
import javax.persistence.*;
import javax.persistence.Table;
import javax.validation.constraints.Size;

@Entity
@Table(name = "locations")
public class Location extends Model {

    @Id
    @JsonValue
    public Long id;

    public Location(String city, Country country) {
        this.city = city;
        this.country = country;
    }

    @Column(nullable = false, unique = true)
    @Size(max = 30)
    private String city;

    @ManyToOne(cascade = CascadeType.ALL,optional = false)
    Country country;

    public String getCity() {
        return city;
    }

    public static Finder<Long, Location> getFinder() {
        return finder;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public static final Finder<Long, Location> finder = new Finder<>(Location.class);

    public Location(Long id) {

    }
}
