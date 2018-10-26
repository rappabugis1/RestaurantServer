package controllers;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.User;
import models.UserData;
import play.Logger;
import play.api.mvc.BodyParser;
import play.mvc.*;

import java.io.IOException;


public class RegisterController extends Controller {

    public Result registerUser()
            throws JsonParseException, JsonMappingException, IOException {

        JsonNode json= request().body().asJson();


        ObjectMapper mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        UserData newUserData = mapper.readValue(json.toString(), UserData.class);

        User newUser = mapper.convertValue(json, User.class);

        newUser.setUser_type("regular_user");

        newUser.setUser_data(newUserData);

        newUserData.setUser(newUser);

        newUserData.save();

        newUser.save();

        return ok();

    }
}
