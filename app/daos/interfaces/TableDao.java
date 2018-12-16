package daos.interfaces;

import models.Table;

public interface TableDao {
    void CreateTable(Table newTable);

    int GetNumTableType(Long id, int sittingPlaces);
}
