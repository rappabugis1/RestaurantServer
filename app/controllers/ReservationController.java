package controllers;


import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.JsonNode;
import daos.implementations.ReservationDaoImpl;
import daos.implementations.RestaurantDaoImpl;
import daos.implementations.UserDaoImpl;
import daos.interfaces.ReservationDao;
import daos.interfaces.RestaurantDao;
import daos.interfaces.UserDao;
import models.Reservation;
import models.Table;
import play.mvc.Controller;
import play.mvc.Result;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

public class ReservationController extends Controller {

    private UserDao userDao= new UserDaoImpl();
    private RestaurantDao restDao = new RestaurantDaoImpl();
    private ReservationDao resDao= new ReservationDaoImpl();

    //Post actions


    public Result checkReservationAvailability(){
        JsonNode json = request().body().asJson();

        if (json == null)
            return badRequest("Invalid Json is null");

        //Getting required data
        try{
            Timestamp reservationDateTime=getStampFromDate(json.get("reservationDate").asText(), json.get("reservationHour").asText());

            Long idRestaurant = json.get("idRestaurant").asLong();

            int persons = json.get("persons").asInt();

            //Hardcoded value for length of stay, right now not a requirement in frontend but this is how it should work, default stay is 2 hours
            int lengthOfStay= json.get("lengthOfStay").asInt();

            //Get reservation end
            Timestamp reservationEnd = new Timestamp(reservationDateTime.getTime() + (lengthOfStay*60)*1000);

            //check if available

            resDao.CheckIfReservationAvailable(persons, idRestaurant, reservationDateTime, reservationEnd);

        } catch (Exception e){
            return badRequest(e.getMessage());
        }
        return ok();
    }



    //Helper methods

    private Reservation getReservationFromRequest(JsonNode json) throws ParseException {

        //Get token
        Optional<String> token= request().getHeaders().get("Authorization");

        //Decode token and get user_id
        DecodedJWT jwt = JWT.decode(token.get().substring(7));
        Long idUser = jwt.getClaim("user_id").asLong();

        //Parse date fron json to timestamp
        Timestamp reservationDateTime = getStampFromDate(json.get("reservationDate").asText(), json.get("reservationHour").asText());

        Long idRestaurant = json.get("idRestaurant").asLong();

        int persons = json.get("persons").asInt();

        //Hardcoded value for length of stay, right now not a requirement in frontend but this is how it should work, default stay is 2 hours
        int lengthOfStay= json.get("lengthOfStay").asInt();

        //Get reservation end
        Timestamp reservationEnd = new Timestamp(reservationDateTime.getTime() + (lengthOfStay*60)*1000);

        Reservation returnReservation = new Reservation(persons, reservationDateTime, "", true, reservationEnd, new Timestamp(System.currentTimeMillis()));

        returnReservation.setRestaurant(restDao.getRestaurantbyId(idRestaurant));
        returnReservation.setUser(userDao.getUserbyId(idUser));

        return  returnReservation;
    }

    private Timestamp getStampFromDate(String reservationDate, String reservationHour) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date parsedDate = dateFormat.parse(reservationDate+" "+reservationHour);

        if(parsedDate.before(new Date()))
            throw new ParseException("Date is expired", 1);

        return new Timestamp(parsedDate.getTime());
    }


}
