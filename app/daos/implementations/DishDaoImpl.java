package daos.implementations;

import daos.interfaces.DishDao;
import models.Dish;

import java.util.List;

public class DishDaoImpl implements DishDao {

    //Create

    @Override
    public void createDish(Dish newDish){
        newDish.save();
    }

    //Read

    @Override
    public Dish getDishById(Long id) {
        return Dish.getFinder().byId(id);
    }

    @Override
    public List<Dish> getRestaurantDishes (Long id) {
        return Dish.getFinder().query().where().eq("menu.restaurant.id", id).findList();
    }

    //Delete
    @Override
    public void deleteDish(Dish deleteDish){
        deleteDish.delete();
    }
}
