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

    public Location(String name, Country country) {
        this.name = name;
        this.country = country;
    }

    @Column(nullable = false, unique = true)
    @Size(max = 30)
    private String name;

    @ManyToOne(cascade = CascadeType.ALL,optional = false)
    Country country;

    public String getName() {
        return name;
    }

    public static Finder<Long, Location> getFinder() {
        return finder;
    }

    public void setName(String name) {
        this.name = name;
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
