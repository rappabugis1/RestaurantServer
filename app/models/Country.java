package models;



import com.fasterxml.jackson.annotation.JsonValue;
import io.ebean.Finder;
import io.ebean.Model;

import javax.persistence.*;
import javax.persistence.Table;

@Entity
@Table(name = "countries")
public class Country extends Model {

    @Id
    public Long id;

    @Column(nullable = false, unique = true)
    private String country;

    public Country(String country) {
        this.country = country;
    }

    @JsonValue
    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public static Finder<Long, Country> getFinder() {
        return finder;
    }

    public static final Finder<Long, Country> finder = new Finder<>(Country.class);



}
