package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import daos.implementations.LocationDaoImpl;
import daos.implementations.RestaurantDaoImpl;
import daos.implementations.UserDaoImpl;
import daos.interfaces.LocationDao;
import daos.interfaces.RestaurantDao;
import daos.interfaces.UserDao;
import play.mvc.Controller;
import play.mvc.Result;
import util.JWTUtil;

import java.util.Optional;

public class AdminCommonController extends Controller{

    RestaurantDao restDao= new RestaurantDaoImpl();
    UserDao userDao= new UserDaoImpl();
    LocationDao locDao= new LocationDaoImpl();

    public Result getAdministrationCounters(){
        Optional<String> token = request().getHeaders().get("Authorization");
        try{
            (new JWTUtil()).verifyJWT(token.get().substring(7));

        }catch (Exception e){
            return unauthorized("Not Authorized!");
        }

        try{
            JsonNode rootNode = (new ObjectMapper()).createObjectNode();

            ((ObjectNode) rootNode).put("restaurantsNumber", restDao.getNumberRestaurants());
            ((ObjectNode) rootNode).put("locationsNumber", locDao.getNumberLocations());
            ((ObjectNode) rootNode).put("usersNumber", userDao.getNumberUsers());

            return ok(rootNode);
        }
        catch (Exception e){
            return badRequest(e.getMessage());
        }
    }

}
