package daos.implementations;

import daos.interfaces.TableDao;
import models.Table;

public class TableDaoImpl implements TableDao {

    //Create

    @Override
    public void CreateTable(Table  newTable){
        newTable.save();
    }



}
