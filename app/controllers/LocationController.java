package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import daos.implementations.CountryDaoImpl;
import daos.implementations.LocationDaoImpl;
import daos.interfaces.CountryDao;
import daos.interfaces.LocationDao;
import io.ebean.PagedList;
import models.Country;
import models.Location;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import util.JWTUtil;

import java.util.List;
import java.util.Optional;

public class LocationController extends Controller {

    LocationDao locDao = new LocationDaoImpl();
    CountryDao coutDao = new CountryDaoImpl();

    //POST

    public Result getFilteredLocations  (){

        Optional<String> token = request().getHeaders().get("Authorization");
        try{
            (new JWTUtil()).verifyJWT(token.get().substring(7));

        }catch (Exception e){
            return unauthorized("Not Authorized!");
        }

        JsonNode json = request().body().asJson();

        if (json == null)
            return badRequest("Json is null");

        try {
            PagedList result = locDao.getFilteredLocations(json);

            ObjectMapper mapper = new ObjectMapper();

            ObjectNode returnNode = mapper.createObjectNode();
            returnNode.put("numberOfPages", result.getTotalPageCount());

            ArrayNode locationsNode = mapper.createArrayNode();

            List<Location> locations = result.getList();

            for (Location location:locations
                 ) {
                JsonNode node = mapper.createObjectNode();
                ((ObjectNode) node).put("id", location.id);
                ((ObjectNode) node).put("name", location.getName());
                ((ObjectNode) node).put("country", location.getCountry().getName());

                locationsNode.add(node);
            }

            returnNode.putArray("locations").addAll(locationsNode);

            return ok((new ObjectMapper()).writeValueAsString(returnNode));


        }
        catch (NullPointerException e){
            return badRequest("Missing json fields...");
        }
        catch (Exception e) {
            return badRequest(e.toString());
        }
    }

    public Result addLocation(){
        Optional<String> token = request().getHeaders().get("Authorization");
        try{
            (new JWTUtil()).verifyJWT(token.get().substring(7));

        }catch (Exception e){
            return unauthorized("Not Authorized!");
        }

        JsonNode json = request().body().asJson();

        if (json == null)
            return badRequest("Json is null");

        try {

            Location newLocation = new Location(json.get("name").asText());

            Country newCountry = new Country(json.get("country").asText());
            newLocation.setCountry(coutDao.checkIfExistsThenReturn(newCountry));

            locDao.createLocation(newLocation);

            return ok(getLocJson(newLocation));
        }
        catch (Exception e) {
            return badRequest("Location already exists");
        }

    }

    public Result editLocation(){
        Optional<String> token = request().getHeaders().get("Authorization");
        try{
            (new JWTUtil()).verifyJWT(token.get().substring(7));

        }catch (Exception e){
            return unauthorized("Not Authorized!");
        }

        JsonNode json = request().body().asJson();

        if (json == null)
            return badRequest("Json is null");

        try {
            Location location= locDao.getById(json.get("id").asLong());
            location.setName(json.get("name").asText());

            locDao.updateLocation(location);

            return ok(getLocJson(location));
        }
        catch (NullPointerException e){
            return badRequest("Missing json fields...");
        }
        catch (Exception e) {
            return badRequest(e.toString());
        }

    }

    public Result deleteLocation () {

        Optional<String> token = request().getHeaders().get("Authorization");
        try{
            (new JWTUtil()).verifyJWT(token.get().substring(7));

        }catch (Exception e){
            return unauthorized("Not Authorized!");
        }

        JsonNode json = request().body().asJson();

        if(json==null)
            return badRequest("Json is null");

        try{
            Location location= locDao.getById(json.get("id").asLong());

            locDao.deleteLocation(location);

            return ok();
        }
        catch (NullPointerException e){
            return badRequest("Missing json fields...");
        }
        catch (Exception e){
            return badRequest(e.toString());
        }

    }

    public Result getLocationDetails(){
        JsonNode json = request().body().asJson();

        if(json==null)
            return badRequest("Json is null");

        try{

            return ok(getLocJson(locDao.getById(json.get("id").asLong())));

        }
        catch (NullPointerException e){
            return badRequest("Missing json fields...");
        }
        catch (Exception e){
            return badRequest(e.toString());
        }
    }

    public Result getLocationsForSelect(){
        try{

            ObjectMapper mapper = new ObjectMapper();

            ArrayNode rootNode =  mapper.createArrayNode();

            for (Country country: coutDao.getAll()) {
                JsonNode countryNode = mapper.createObjectNode();

                ((ObjectNode) countryNode).put("country_name", country.getName());

                ArrayNode locationsNode = mapper.createArrayNode();

                for (Location location : locDao.getAllLocOfCountry(country.getName())
                ) {
                    ObjectNode cityNode = mapper.createObjectNode();
                    cityNode.put("name", location.getName());
                    cityNode.put("id", location.id);

                    locationsNode.add(cityNode);

                    ((ObjectNode) countryNode).put("city_names", locationsNode);

                }
                rootNode.add(countryNode);

            }
                return ok(mapper.writeValueAsString(rootNode));

        }
        catch (NullPointerException e){
            return badRequest("Missing json fields...");
        }
        catch (Exception e){
            return badRequest(e.toString());
        }
    }


    private JsonNode getLocJson(Location newLocation){
        JsonNode rootNode = (new ObjectMapper()).createObjectNode();

        ((ObjectNode) rootNode).put("id", newLocation.id);
        ((ObjectNode) rootNode).put("name", newLocation.getName());
        return rootNode;
    }

}
