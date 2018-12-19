package daos.implementations;

import daos.interfaces.TableDao;
import models.Table;

import java.util.List;

public class TableDaoImpl implements TableDao {

    //Create

    @Override
    public void CreateTable(Table  newTable){
        newTable.save();
    }


    //Read

    @Override
    public int GetNumTableType(Long id, int sittingPlaces) {
        return Table.getFinder().query().where()
                .eq("restaurant.id", id)
                .eq("sitting_places", sittingPlaces)
                .findList()
                .size();
    }

    @Override
    public List<Table> getFreeTablesToDelete(Long restaurantId, int tableType){
        return Table.getFinder().query().fetch("reservations").where().eq("restaurant.id", restaurantId).eq("sitting_places", tableType).isNull("t1.id").findList();
    }
}
