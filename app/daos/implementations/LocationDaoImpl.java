package daos.implementations;

import com.fasterxml.jackson.databind.JsonNode;
import daos.interfaces.LocationDao;
import io.ebean.ExpressionList;
import io.ebean.PagedList;
import models.Country;
import models.Location;

import java.util.List;

public class LocationDaoImpl implements LocationDao {

    //Create methods

    @Override
    public void createLocation(Location newLocation) {
        newLocation.save();
    }



    //Read methods

    @Override
    public Location getLocationById(Long id){
        return Location.getFinder().byId(id);
    }

    @Override
    public PagedList<Location> getFilteredLocations(JsonNode json){
        int itemsPerPage = json.get("itemsPerPage").asInt();
        int pageNumber = json.get("pageNumber").asInt();
        JsonNode searchTextNode = json.get("searchText");

        ExpressionList<Location> query = Location.getFinder().query().where();

        if (searchTextNode != null) {
            String searchText = searchTextNode.asText();
            query.or()
                .icontains("name", searchText)
                .icontains("country.name", searchText)
                .endOr();
        }

        query.setFirstRow(itemsPerPage * (pageNumber - 1)).setMaxRows(itemsPerPage);

        return query.findPagedList();
    }

    @Override
    public int getNumberLocations(){
        return Location.finder.query().findCount();
    }

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

    @Override
    public void updateLocation(Location location){
        location.update();
    }

    //Delete methods

    @Override
    public void deleteLocation(Location location){
        location.delete();
    }
}
