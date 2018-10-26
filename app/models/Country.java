package models;

import io.ebean.Finder;
import io.ebean.Model;
import play.data.validation.Constraints;

import javax.persistence.*;

@Entity
@Table(name = "countries")
public class Country extends Model {

    @Id
    public Long id;

    @Column(nullable = false)
    @Constraints.MaxLength(30)
    private String country_name;

    public Country(@Constraints.MaxLength(30) String country_name) {
        this.country_name = country_name;
    }


    public String getCountry_name() {
        return country_name;
    }

    public void setCountry_name(String country_name) {
        this.country_name = country_name;
    }

    public static Finder<Long, Country> getFinder() {
        return finder;
    }

    public static final Finder<Long, Country> finder = new Finder<>(Country.class);



}
