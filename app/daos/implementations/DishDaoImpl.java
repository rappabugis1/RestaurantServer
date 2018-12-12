package daos.implementations;

import daos.interfaces.DishDao;
import models.Dish;

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

    //Delete
    @Override
    public void deleteDish(Dish deleteDish){
        deleteDish.delete();
    }
}
