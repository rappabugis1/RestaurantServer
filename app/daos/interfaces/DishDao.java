package daos.interfaces;

import models.Dish;

import java.util.List;

public interface DishDao {
    void createDish(Dish newDish);

    Dish getDishById(Long id);

    List<Dish> getRestaurantDishes(Long id);

    //Delete
    void deleteDish(Dish deleteDish);
}
