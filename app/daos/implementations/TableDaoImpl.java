package daos.implementations;

import daos.interfaces.TableDao;
import models.Table;

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
}
