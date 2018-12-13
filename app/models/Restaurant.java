package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.ebean.Finder;
import io.ebean.Model;

import javax.persistence.*;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "restaurants")
public class Restaurant extends Model {

    public Restaurant(String restaurantName, String description, float latitude, float longitude, String imageFileName, String coverFileName, int priceRange) {
        this.restaurantName = restaurantName;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.imageFileName = imageFileName;
        this.coverFileName = coverFileName;
        this.priceRange = priceRange;
    }

    @Id
    public Long id;

    @Column(nullable = false, name = "restaurant_name")
    private String restaurantName;

    @Column(nullable = false, length = 2000)
    private String description;

    @Column(nullable = false, name = "price_range")
    private int priceRange;

    @Column(nullable = false)
    private float latitude;

    @Column(nullable = false)
    private float longitude;

    @Column(nullable = false, name = "image_file_name")
    private String imageFileName;

    @Column(nullable = false, name = "cover_file_name")
    private String coverFileName;

    @Column(nullable = false, name= "default_stay")
    private int defaultStay;

    private int mark = average();

    public static final Finder<Long, Restaurant> finder = new Finder<>(Restaurant.class);


    @ManyToOne
    @JsonProperty("location_id")
    Location location;


    @ManyToMany(cascade=CascadeType.ALL,mappedBy = "restaurants")
    @JoinTable(
            name = "categories_restaurants",
            joinColumns = @JoinColumn(name = "restaurant_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "category_id", referencedColumnName = "id")
    )
    @JsonIgnore
    List<Category> categoryList;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "restaurant")
    @JsonIgnore
    List<Review> reviewList;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "restaurant")
    @JsonIgnore
    List<Menu> menus;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "restaurant")
    @JsonIgnore
    List<models.Table> tableList;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "restaurant")
    @JsonIgnore
    List<Reservation> reservationList;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "restaurant")
    @JsonIgnore
    List<GuestStay> guestStays;

    @JsonProperty("foodType")
    private String foodType() {
        StringBuilder foodType = new StringBuilder();
        for (Category cat : this.categoryList
        ) {
            foodType.append(cat.getName()).append(" | ");
        }
        foodType.setLength(foodType.length() - 2);
        return foodType.toString();
    }

    @JsonProperty("mark")
    private int average() {
        int avg = 0;

        for (Review review : this.reviewList) {
            avg += review.getMark();
        }

        if (avg > 0 && this.reviewList.size() > 0)
            avg /= this.reviewList.size();
        else
            avg = 0;

        return avg;
    }

    @JsonProperty("votes")
    private int votes() {
        return reviewList.size();
    }

    //Getters and setters

    public List<Review> getReviewList() {
        return reviewList;
    }

    public void setReviewList(List<Review> reviewList) {
        this.reviewList = reviewList;
    }

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

    public List<Category> getCategoryList() {
        return categoryList;
    }

    public void setCategoryList(List<Category> categoryList) {
        this.categoryList = categoryList;
    }

    public static Finder<Long, Restaurant> getFinder() {
        return finder;
    }

    public List<models.Table> getTableList() {
        return tableList;
    }

    public void setTableList(List<models.Table> tableList) {
        this.tableList = tableList;
    }

    public List<Menu> getMenus() {
        return menus;
    }

    public void setMenus(List<Menu> menus) {
        this.menus = menus;
    }

    public List<Reservation> getReservationList() {
        return reservationList;
    }

    public void setReservationList(List<Reservation> reservationList) {
        this.reservationList = reservationList;
    }

    public int getDefaultStay() {
        return defaultStay;
    }

    public void setDefaultStay(int defaultStay) {
        this.defaultStay = defaultStay;
    }

    public List<GuestStay> getGuestStays() {
        return guestStays;
    }

    public void setGuestStays(List<GuestStay> guestStays) {
        this.guestStays = guestStays;
    }

}
