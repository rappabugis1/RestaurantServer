package daos.interfaces;

import com.fasterxml.jackson.core.JsonProcessingException;
import models.DishType;
import models.Menu;
import models.Restaurant;

import java.io.IOException;
import java.util.List;

public interface RestaurantDao {

    //Create methods
    void createRestaurant(Restaurant newRestaurant);

    //Read methods

    int getNumberRestaurants();

    List<Restaurant> getRestaurants();

    Restaurant getRestaurantbyId(Long id);

    Restaurant getRestaurantByName(String name);

    String locationsRestaurant() throws IOException;

    String getRandomRestaurants() throws IOException;

    String getAllRestaurantComments(Long id) throws JsonProcessingException;

    String getAllRestaurantTables(Long id) throws JsonProcessingException;

    List<DishType> getAllDishTypes();

    List<Menu> getRestaurantMenus(Long id);

    Menu getMenuByType(String name);

    //Delete methods
    void deleteRestaurant(Restaurant restaurant);
    //Update methods

    //Delete methods
}
