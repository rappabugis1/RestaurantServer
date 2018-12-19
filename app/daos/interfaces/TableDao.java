package daos.interfaces;

import models.Table;

import java.util.List;

public interface TableDao {
    void CreateTable(Table newTable);

    int GetNumTableType(Long id, int sittingPlaces);

    List<Table> getFreeTablesToDelete(Long restaurantId, int tableType);
}
