package daos.interfaces;

import models.Country;
import models.Location;

import java.util.List;

public interface LocationDao {

    //Create methods

    Boolean createCountry (Location newLocation);

    //Read methods
    Location getById(Long id);
    Location getLocationByName (String name);
    Boolean checkIfExists (String name);
    List<Location> getLocationsOfCountry (Country country);
    Location checkIfExistsThenReturn (Location location);

    //Update methods

    //Delete methods
}
