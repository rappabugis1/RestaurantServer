package models;


import com.fasterxml.jackson.annotation.JsonValue;
import io.ebean.Finder;
import io.ebean.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "countries")
public class Country extends Model {

    @Id
    public Long id;

    @Column(nullable = false, unique = true)
    private String name;

    public Country(String name) {
        this.name = name;
    }

    @JsonValue
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static Finder<Long, Country> getFinder() {
        return finder;
    }

    public static final Finder<Long, Country> finder = new Finder<>(Country.class);


}
