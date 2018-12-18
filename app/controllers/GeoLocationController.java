package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import daos.implementations.RestaurantDaoImpl;
import daos.interfaces.RestaurantDao;
import play.mvc.Controller;
import play.mvc.Result;

public class GeoLocationController extends Controller {

    RestaurantDao restDao= new RestaurantDaoImpl();

    public Result getCloseRestNumber(){
        JsonNode json = request().body().asJson();

        if (json == null)
            return badRequest("Invalid Json is null");

        try {
            ObjectMapper mapper = new ObjectMapper();



            return ok(mapper.writeValueAsString(restDao.getLocatedInProximity(json.get("longitude").asDouble(),json.get("latitude").asDouble(), json.get("radius").asDouble()).size()));
        } catch (Exception e) {
           return badRequest(e.getMessage());
        }

    }
}
