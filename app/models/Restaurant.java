package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.ebean.Finder;
import io.ebean.Model;

import javax.persistence.*;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "restaurants")
public class Restaurant extends Model {

    @Id
    public Long id;

    @Column(nullable = false, name = "restaurant_name")
    private String restaurantName;

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPriceRange() {
        return priceRange;
    }

    public void setPriceRange(int priceRange) {
        this.priceRange = priceRange;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public String getImageFileName() {
        return imageFileName;
    }

    public void setImageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
    }

    public String getCoverFileName() {
        return coverFileName;
    }

    public void setCoverFileName(String coverFileName) {
        this.coverFileName = coverFileName;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public static Finder<Long, Restaurant> getFinder() {
        return finder;
    }

    @Column(nullable = false)
    private String description;

    @Column(nullable = false, name = "price_range")
    private int priceRange;

    @Column(nullable = false)
    private float latitude;

    @Column(nullable = false)
    private float longitude;

    @Column(nullable = false, name = "image_file_name")
    private String imageFileName;

    @Column(nullable = false, name="cover_file_name")
    private String coverFileName;

    @ManyToOne(cascade = CascadeType.ALL,optional = false)
    Location location;

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    @ManyToMany
            @JoinTable(
                    name="restaurant_categories",
                    joinColumns = @JoinColumn(name="restaurant_id", referencedColumnName = "id"),
                    inverseJoinColumns = @JoinColumn(name = "category_id", referencedColumnName = "id")
            )
    List<Category> categories;

    @OneToMany (mappedBy = "restaurant")
    List<Review> reviews;

    @OneToMany (mappedBy = "restaurant")
    List<Menu> menus;

    public List<Menu> getMenus() {
        return menus;
    }

    public void setMenus(List<Menu> menus) {
        this.menus = menus;
    }

    public static final Finder<Long, Restaurant> finder = new Finder<>(Restaurant.class);

}
