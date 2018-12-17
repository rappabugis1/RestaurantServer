package controllers;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import daos.implementations.CountryDaoImpl;
import daos.implementations.LocationDaoImpl;
import daos.implementations.UserDaoImpl;
import daos.interfaces.CountryDao;
import daos.interfaces.LocationDao;
import daos.interfaces.UserDao;
import io.ebean.PagedList;
import models.Country;
import models.Location;
import models.User;
import models.UserData;
import play.mvc.Controller;
import play.mvc.Result;
import util.JWTUtil;
import util.PasswordUtil;

import java.io.IOException;
import java.util.Optional;


public class UserController extends Controller {


    CountryDao countryDao = new CountryDaoImpl();
    UserDao userDao = new UserDaoImpl();
    LocationDao locDao = new LocationDaoImpl();

    public Result registerUser() throws IOException {

        JsonNode json = request().body().asJson();


        ObjectMapper mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        //Mapping to temp
        UserData newUserData = mapper.convertValue(json, UserData.class);
        User newUser = mapper.convertValue(json, User.class);
        Location newLocation = new Location(json.get("city").asText());
        Country newCountry = new Country(json.get("country").asText());

        newUser.setPassword(json.get("password").asText());
        newUser.setUser_type("regular_user");

        //Creating the user

        if (!userDao.checkEmailExists(newUser.getEmail())) {
            newLocation.setCountry(countryDao.checkIfExistsThenReturn(newCountry));

            newUserData.setLocation(locDao.checkIfExistsThenReturn(newLocation));

            newUser.setUser_data(newUserData);

            newUserData.setUser(newUser);

            //Password encrytpion
            PasswordSetting(newUser);


            try {
                userDao.createUser(newUser);
            } catch (Exception e) {
                return badRequest("Invalid JSON" + e.getMessage());
            }
            ObjectNode node = mapper.createObjectNode();

            node
                    .put("id", newUser.id)
                    .put("email", newUser.getEmail())
                    .put("phone", newUserData.getPhone())
                    .put("country", newCountry.getName())
                    .put("city", newLocation.getName())
                    .put("firstName", newUserData.getFirstName())
                    .put("lastName", newUserData.getLastName());

            JsonNode jsonNode = new ObjectMapper().readTree(node.toString());
            return ok(jsonNode.toString());
        } else {
            return badRequest("Email already in use!");
        }

    }

    public Result loginUser() {


        JsonNode json = request().body().asJson();

        if (json == null) {
            return badRequest("Invalid JSON!");
        }

        ObjectMapper mapper = new ObjectMapper();

        User newUser = mapper.convertValue(json, User.class);
        newUser.setPassword(json.get("password").asText());

        User temp = userDao.verifyProvidedInfo(newUser.getEmail(), newUser.getPassword());

        if (temp != null) {
            ObjectNode node = mapper.createObjectNode();

            node
                    .put("id", temp.id)
                    .put("email", temp.getEmail())
                    .put("phone", temp.getUser_data().getPhone())
                    .put("country", temp.getUser_data().getLocation().getCountry().getName())
                    .put("city", temp.getUser_data().getLocation().getName())
                    .put("firstName", temp.getUser_data().getFirstName())
                    .put("lastName", temp.getUser_data().getLastName());

            JsonNode jsonNode = null;
            try {
                jsonNode = new ObjectMapper().readTree(node.toString());
            } catch (IOException e) {
                return badRequest(e.getMessage());
            }
            return ok(jsonNode.toString()).withHeader("Authorization", (new JWTUtil()).getSignedToken(temp.id, temp.getUser_type()));
        } else {
            return badRequest("Entered data is not valid!");
        }
    }

    public Result getListOfReservationsForUser() {

        try {
            //Get token
            Optional<String> token = request().getHeaders().get("Authorization");

            //Decode token and get user_id
            DecodedJWT jwt = JWT.decode(token.get().substring(7));
            Long idUser = jwt.getClaim("user_id").asLong();

            ObjectNode nodeValue = (new ObjectMapper()).createObjectNode();
            nodeValue.putArray("activeReservations").addAll((ArrayNode) (new ObjectMapper()).valueToTree(userDao.getUserReservationsActive(idUser)));
            nodeValue.putArray("pastReservations").addAll((ArrayNode) (new ObjectMapper()).valueToTree(userDao.getUserReservationsPassed(idUser)));

            return ok((new ObjectMapper()).writeValueAsString(nodeValue));

        } catch (Exception e) {
            return badRequest("Unauthorized! " + e.getMessage());
        }

    }

    public Result getFilteredUsers(){
        JsonNode json = request().body().asJson();

        if (json == null)
            return badRequest("Json is null");

        try {
            PagedList result = userDao.getFilteredUsers(json);

            ObjectNode returnNode = (new ObjectMapper()).createObjectNode();
            returnNode.put("numberOfPages", result.getTotalPageCount());


            returnNode.putArray("users").addAll((ArrayNode) (new ObjectMapper()).valueToTree(result.getList()));

            return ok((new ObjectMapper()).writeValueAsString(returnNode));


        } catch (Exception e) {
            return badRequest(e.getMessage());
        }
    }

    public Result getUserDetails(){
        JsonNode json = request().body().asJson();

        if (json == null) {
            return badRequest("Invalid JSON!");
        }

        try{
            return ok((new ObjectMapper()).writeValueAsString(userDao.getUserbyId(json.get("id").asLong())));
        } catch (Exception e){
            return badRequest(e.getMessage());
        }

    }

    public Result editUser(){
        Optional<String> token = request().getHeaders().get("Authorization");
        try{
            (new JWTUtil()).verifyJWT(token.get().substring(7));

        }catch (Exception e){
            return unauthorized("Not Authorized!");
        }

        JsonNode json = request().body().asJson();

        if (json == null) {
            return badRequest("Invalid JSON!");
        }

        try{
            User userEdit=  userDao.getUserbyId(json.get("id").asLong());

            if(userEdit==null)
                throw new Exception("User does not exist");

            if(!json.get("email").isNull())
                userEdit.setEmail(json.get("email").asText());


            if(!json.get("password").isNull()){
                userEdit.setPassword(json.get("password").asText());
                PasswordSetting(userEdit);
            }

            if(!json.get("firstName").isNull())
                userEdit.getUser_data().setFirstName(json.get("firstName").asText());

            if(!json.get("lastName").isNull())
                userEdit.getUser_data().setLastName(json.get("lastName").asText());

            if(!json.get("phone").isNull())
                userEdit.getUser_data().setPhone(json.get("phone").asText());


            if(!json.get("city").isNull())
                userEdit.getUser_data().setLocation(locDao.getLocationByName(json.get("city").asText()));



            return ok((new ObjectMapper()).writeValueAsString(userDao.editUser(userEdit)));

        }catch(Exception e){
            return badRequest(e.getMessage());
        }
    }

    public Result adminDeleteUser(){
        Optional<String> token = request().getHeaders().get("Authorization");
        try{
            (new JWTUtil()).verifyJWT(token.get().substring(7));

        }catch (Exception e){
            return unauthorized("Not Authorized!");
        }

        JsonNode json = request().body().asJson();

        if (json == null) {
            return badRequest("Invalid JSON!");
        }

        try{
            userDao.deleteUser(userDao.getUserbyId(json.get("id").asLong()));
            return ok();
        } catch (Exception e){
            return badRequest();
        }
    }

    private static void PasswordSetting(User user) {
        String salt = PasswordUtil.getSalt(30);
        String securedPassword = PasswordUtil.generateSecurePassword(user.getPassword(), salt);

        user.setPassword(securedPassword);
        user.setSalt(salt);
    }




}
