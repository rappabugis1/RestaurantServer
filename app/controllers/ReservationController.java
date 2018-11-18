package controllers;


import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
import java.util.*;

public class ReservationController extends Controller {

    private UserDao userDao= new UserDaoImpl();
    private RestaurantDao restDao = new RestaurantDaoImpl();
    private ReservationDao resDao= new ReservationDaoImpl();

    //Post actions

    public Result makeReservation (){
        JsonNode json = request().body().asJson();

        if (json == null)
            return badRequest("Invalid Json is null");

        try{
            //-------------Getting required data
            Reservation tempReservation = getReservationFromRequest(json);

            //Check if there are collisions
            //Get tables that are in restaurant
            List<Table> tableIds = resDao.getTablesOfRestaurantWithPersons(tempReservation.getPersons(), tempReservation.getRestaurant().id);

            for (Table table : tableIds) {
                Reservation reservationColision = FindReservationColisions(tempReservation.getRestaurant().id, table.id, tempReservation.getReservationDateTime(), tempReservation.getReservationEndDateTime());

                //If there is no collisionn add the reservation time to available times, that means the wanted time is free
                if (reservationColision == null) {
                    tempReservation.setTable(table);
                    break;
                }
            }

            if(tempReservation.getTable()!=null){
                resDao.CreateReservation(tempReservation);
                ObjectNode returnValue = (new ObjectMapper()).createObjectNode();
                returnValue.put("id", tempReservation.id);
                returnValue.put("idTable", tempReservation.getTable().id);
                returnValue.put("idUser", tempReservation.getUser().id);
                returnValue.put("persons", tempReservation.getPersons());
                returnValue.put("reservationDateTime", tempReservation.getReservationDateTime().toString());

                return ok((new ObjectMapper()).writeValueAsString(returnValue));
            }
            return badRequest("No available tables for that time!");

        } catch (Exception e){
            return badRequest(e.getMessage());
        }
    }

    public Result deleteReservation(){
        JsonNode json = request().body().asJson();

        if(json==null)
            return badRequest("Json is null");
        try {

            resDao.deleteReservation(json.get("idReservation").asLong());
            return ok();

        }catch (Exception e){
            return badRequest(e.getMessage());
        }
    }

    public Result setReservationToFixed(){
        JsonNode json = request().body().asJson();

        if(json==null)
            return badRequest("Json is null");
        try {

            resDao.setReservationToFixed(json.get("idReservation").asLong(), json.get("request").asText());
            return ok();

        }catch (Exception e){
            return badRequest(e.getMessage());
        }
    }

    public Result checkReservationAvailability(){
        JsonNode json = request().body().asJson();

        if (json == null)
            return badRequest("Invalid Json is null");

        try{
            //-------------Getting required data

            Timestamp reservationDateTime=getStampFromDate(json.get("reservationDate").asText(), json.get("reservationHour").asText());

            Long idRestaurant = json.get("idRestaurant").asLong();

            int persons = json.get("persons").asInt();

            //Hardcoded value for length of stay, right now not a requirement in frontend but this is how it should work, default stay is 2 hours
            int lengthOfStay= json.get("lengthOfStay").asInt();

            //Get reservation end time
            Timestamp reservationEnd = new Timestamp(reservationDateTime.getTime() + (lengthOfStay*60)*1000);

            //Check availability
            ObjectNode returnValue=checkAvailability(persons, idRestaurant, reservationDateTime, reservationEnd);

            return ok((new ObjectMapper()).writeValueAsString(returnValue));

        } catch (Exception e){
            return badRequest(e.getMessage());
        }
    }

    //Logic methods

    private ObjectNode checkAvailability(int persons, Long idRestaurant,Timestamp reservationDateTime, Timestamp reservationEnd) throws Exception {
        //check if available

        //Get tables that are in restaurant
        List<Table> tableIds = resDao.getTablesOfRestaurantWithPersons(persons, idRestaurant);

        if (tableIds.isEmpty())
            throw new Exception("No available tables for that amount of people");

        Set<Timestamp> setTimes = new HashSet<>();
        int freeTables = 0;

        //-------------Start algorithm

        for (Table table : tableIds) {
            Reservation reservationColision = FindReservationColisions(idRestaurant, table.id, reservationDateTime, reservationEnd);

            //If there is no collisionn add the reservation time to available times, that means the wanted time is free
            if (reservationColision == null) {
                setTimes.add(reservationDateTime);
                freeTables++;
            } else {

                //If not, begin recursive algorithms to find the left and right best times available
                Long lengthOfReservation = reservationEnd.getTime() - reservationDateTime.getTime();

                Timestamp leftBest = FindNextAvailableTimeLeft(
                        idRestaurant,
                        table.id,
                        new Timestamp(reservationColision.getReservationDateTime().getTime() - lengthOfReservation),
                        reservationColision.getReservationDateTime()
                );

                Timestamp rightBest = FindNextAvailableTimeRight(
                        idRestaurant,
                        table.id,
                        reservationColision.getReservationEndDateTime(),
                        new Timestamp(reservationColision.getReservationEndDateTime().getTime() + lengthOfReservation)
                );

                //Find the left and right best time, add them to the array
                setTimes.add(leftBest);
                setTimes.add(rightBest);
                freeTables++;
            }
        }

        //TODO reduce number of available times, right now its 2 per table with no indentical values

        ArrayList<String> convertedTime = new ArrayList<>();

        for (Timestamp stamp : setTimes) {
            if(!stamp.before(new Date()))
                convertedTime.add(getTimeStringFromStamp(stamp));
        }

        ObjectNode nodeValue = (new ObjectMapper()).createObjectNode();
        nodeValue.putArray("bestTime").addAll((ArrayNode)(new ObjectMapper()).valueToTree(convertedTime));
        nodeValue.put("tablesLeft", freeTables);
        nodeValue.put("idRestaurant", idRestaurant);
        nodeValue.put("restaurantName", restDao.getRestaurantbyId(idRestaurant).getRestaurantName());
        nodeValue.put("restaurantImageFileName", restDao.getRestaurantbyId(idRestaurant).getImageFileName());

        return nodeValue;
    }

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

    private String getTimeStringFromStamp(Timestamp stamp){
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        return dateFormat.format(stamp);
    }

    private Timestamp getStampFromDate(String reservationDate, String reservationHour) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date parsedDate = dateFormat.parse(reservationDate+" "+reservationHour);

        if(parsedDate.before(new Date()))
            throw new ParseException("Date is expired", 1);

        return new Timestamp(parsedDate.getTime());
    }

    //-------------------RECURSIVE
    //For each table reservations that evaluate true to the expression: (endOfReservation > userReservationStartTime) and ( startOfReservation < endTimeOfUserReservation)
    //If there is none , the table is free for that time and the reservation is available
    private Reservation FindReservationColisions(Long resaurant_id, Long table_id, Timestamp start, Timestamp end){

        try{
            List<Reservation> collisions= resDao.findColisions(resaurant_id, table_id, start, end);

            return collisions.get(0);
        }
        catch (Exception e){
            return null;
        }
    }

    //Recursive algorithm to find the nearest available time of a table
    //Left side takes start as (beginning of collision - the length of the reservation), end is the beginning of collision
    private Timestamp FindNextAvailableTimeLeft(Long resaurant_id, Long table_id, Timestamp start, Timestamp end){
        Reservation colision = FindReservationColisions(resaurant_id, table_id, start, end);

        if(colision==null)
            return start;
        else{
            //Calculates new start time
            Timestamp startNewLeft = new Timestamp( colision.getReservationDateTime().getTime()-(end.getTime()-start.getTime()));
            return FindNextAvailableTimeLeft(resaurant_id,table_id, startNewLeft, colision.getReservationDateTime());
        }
    }

    //Right side takes start as the end time of the collision, and the end as the (end of collision + length of reservation)
    private Timestamp FindNextAvailableTimeRight(Long resaurant_id, Long table_id, Timestamp start, Timestamp end){
        Reservation colision = FindReservationColisions(resaurant_id, table_id, start, end);

        if(colision==null)
            return start;
        else{
            //Calculates new start time
            Timestamp endNewRight = new Timestamp(colision.getReservationEndDateTime().getTime()+(end.getTime()-start.getTime()));
            return FindNextAvailableTimeRight(resaurant_id,table_id,colision.getReservationEndDateTime(), endNewRight);

        }
    }



}
