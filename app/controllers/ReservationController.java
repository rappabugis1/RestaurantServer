package controllers;


import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.JsonNode;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

public class ReservationController extends Controller {

    //Post actions

    public Result makeReservation(){
        JsonNode json = request().body().asJson();

        if (json == null)
            return badRequest("Invalid Json is null");

        //Getting required data, TODO split jwt to JWTUtil
        try{
            Optional<String> token= request().getHeaders().get("Authorization");

            DecodedJWT jwt = JWT.decode(token.get().substring(7));
            Long idUser = jwt.getClaim("user_id").asLong();

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm");
            Date parsedDate = dateFormat.parse(json.get("reservationDate").asText()+" "+json.get("reservationHour").asText());
            Timestamp reservationDateTime = new java.sql.Timestamp(parsedDate.getTime());

            Long idRestaurant = json.get("idRestaurant").asLong();

            Long persons = json.get("persons").asLong();

            //check if available 

        } catch (Exception e){
            return badRequest(e.getMessage());
        }
         return ok();
    }

}
