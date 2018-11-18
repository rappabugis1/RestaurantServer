package daos.interfaces;

import com.fasterxml.jackson.core.JsonProcessingException;
import models.Restaurant;

import java.io.IOException;
import java.util.List;

public interface RestaurantDao {

    //Create methods
    Restaurant createRestaurant(Restaurant newRestaurant);

    //Read methods

    List<Restaurant> getRestaurants();

    Restaurant getRestaurantbyId(Long id);

    Restaurant getRestaurantByName(String name);

    String locationsRestaurant() throws IOException;

    String getRandomRestaurants() throws IOException;

    String getAllRestaurantComments(Long id) throws JsonProcessingException;
    //Update methods

    //Delete methods
}
