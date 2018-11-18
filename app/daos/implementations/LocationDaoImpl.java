package daos.implementations;

import daos.interfaces.LocationDao;
import models.Country;
import models.Location;

import java.util.List;

public class LocationDaoImpl implements LocationDao {

    //Create methods

    @Override
    public Boolean createCountry(Location newLocation) {
        if (!checkIfExists(newLocation.getName())) {
            newLocation.save();
            return true;
        }
        return false;
    }

    //Read methods

    @Override
    public Location getById(Long id) {
        return Location.getFinder().byId(id);
    }

    @Override
    public Location getLocationByName(String name) {
        return Location.getFinder().query()
                .where()
                .eq("name", name)
                .findOne();
    }

    @Override
    public Boolean checkIfExists(String name) {
        return Location.getFinder().query()
                .where()
                .eq("name", name)
                .findCount() != 0;
    }

    @Override
    public List<Location> getLocationsOfCountry(Country country) {
        return Location.getFinder().query()
                .where()
                .eq("country", country)
                .findList();
    }

    @Override
    public Location checkIfExistsThenReturn(Location location) {
        Location temp = getLocationByName(location.getName());
        if (temp != null)
            return temp;
        else return location;
    }


    //Update methods

    //Delete methods
}
