package daos.interfaces;

import models.Dish;

public interface DishDao {
    void createDish(Dish newDish);

    Dish getDishById(Long id);

    //Delete
    void deleteDish(Dish deleteDish);
}
