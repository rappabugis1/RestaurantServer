package controllers;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import daos.implementations.CountryDaoImpl;
import daos.implementations.LocationDaoImpl;
import daos.implementations.UserDaoImpl;
import daos.interfaces.CountryDao;
import daos.interfaces.LocationDao;
import daos.interfaces.UserDao;
import models.Country;
import models.Location;
import models.User;
import models.UserData;
import play.mvc.*;

import java.io.IOException;


public class UserController extends Controller {

    CountryDao countryDao= new CountryDaoImpl();
    UserDao userDao= new UserDaoImpl();
    LocationDao locDao = new LocationDaoImpl();

    public Result registerUser()
            throws  IOException {

        JsonNode json= request().body().asJson();

        ObjectMapper mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        //Mapping to temp
        UserData newUserData = mapper.convertValue(json, UserData.class);
        User newUser = mapper.convertValue(json, User.class);
        Location newLocation = mapper.convertValue(json, Location.class);
        Country newCountry = mapper.convertValue(json, Country.class);

        newUser.setUser_type("regular_user");

        //Creating the user

        if(!userDao.checkEmailExists(newUser.getEmail())){
            newLocation.setCountry(countryDao.checkIfExistsThenReturn(newCountry));

            newUserData.setLocation(locDao.checkIfExistsThenReturn(newLocation));

            newUser.setUser_data(newUserData);

            newUserData.setUser(newUser);
            try {
                newUser.save();
            } catch (Exception e){
                return badRequest("Invalid JSON");
            }
            ObjectNode node = mapper.createObjectNode();

            node
                    .put("id", newUser.id)
                    .put("email", newUser.getEmail())
                    .put("phone", newUserData.getPhone())
                    .put("country", newCountry.getCountry())
                    .put("city", newLocation.getCity())
                    .put("firstName", newUserData.getFirstName())
                    .put("lastName", newUserData.getLastName());

            JsonNode jsonNode =  new ObjectMapper().readTree(node.toString());
            return ok(jsonNode.toString());
        }
        else {
            return badRequest("Email already in use!");
        }

    }

    public Result loginUser()
            throws IOException{
        JsonNode json= request().body().asJson();
        ObjectMapper mapper = new ObjectMapper();

        User newUser = mapper.convertValue(json, User.class);
        User temp=userDao.findUserByPassEmail(newUser.getEmail(),newUser.getPassword());

        if(temp!=null){
            ObjectNode node = mapper.createObjectNode();

            node
                    .put("id", temp.id)
                    .put("email", temp.getEmail())
                    .put("phone", temp.getUser_data().getPhone())
                    .put("country", temp.getUser_data().getLocation().getCountry().getCountry())
                    .put("city", temp.getUser_data().getLocation().getCity())
                    .put("firstName", temp.getUser_data().getFirstName())
                    .put("lastName", temp.getUser_data().getLastName());

            JsonNode jsonNode =  new ObjectMapper().readTree(node.toString());
            return ok(jsonNode.toString());
        }
        else {
            return badRequest("Entered data is not valid!");
        }
    }
}
