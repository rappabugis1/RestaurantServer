package controllers;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.typesafe.config.Config;
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
import play.mvc.Controller;
import play.mvc.Result;
import util.PasswordUtil;

import javax.inject.Inject;
import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Optional;


public class UserController extends Controller {

    @Inject
    private Config config;

    CountryDao countryDao = new CountryDaoImpl();
    UserDao userDao = new UserDaoImpl();
    LocationDao locDao = new LocationDaoImpl();

    public Result registerUser()
            throws IOException {

        JsonNode json = request().body().asJson();


        ObjectMapper mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        //Mapping to temp
        UserData newUserData = mapper.convertValue(json, UserData.class);
        User newUser = mapper.convertValue(json, User.class);
        Location newLocation = new Location(json.get("city").asText());
        Country newCountry = new Country(json.get("country").asText());

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


        //admin

        UserData adminData = new UserData("Ridvan", "Appa Bugis", "061641709");
        adminData.setLocation(locDao.getLocationByName("Sarajevo"));

        User admin = new User("ridvan_appa@hotmail.com", "admin", "admin");

        admin.setUser_data(adminData);

        adminData.setUser(admin);

        PasswordSetting(admin);

        admin.save();
        return ok();
        /*JsonNode json = request().body().asJson();

        if (json == null) {
            return badRequest("Invalid JSON!");
        }

        ObjectMapper mapper = new ObjectMapper();

        User newUser = mapper.convertValue(json, User.class);
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
            return ok(jsonNode.toString()).withHeader("Authorization", getSignedToken(temp.id, temp.getUser_type()));
        } else {
            return badRequest("Entered data is not valid!");
        }*/
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

    private static void PasswordSetting(User user) {
        String salt = PasswordUtil.getSalt(30);
        String securedPassword = PasswordUtil.generateSecurePassword(user.getPassword(), salt);

        user.setPassword(securedPassword);
        user.setSalt(salt);
    }


    private String getSignedToken(Long userId, String usertype) {
        String secret = config.getString("play.http.secret.key");

        Algorithm algorithm = Algorithm.HMAC256(secret);
        return JWT.create()
                .withIssuer("server")
                .withClaim("user_id", userId)
                .withClaim("user_type", usertype)
                .withExpiresAt(Date.from(ZonedDateTime.now(ZoneId.systemDefault()).plusMinutes(10).toInstant()))
                .sign(algorithm);
    }


}
