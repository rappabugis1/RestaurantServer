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
import models.StayByDayType;
import models.Table;
import play.mvc.Controller;
import play.mvc.Result;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ReservationController extends Controller {

    private UserDao userDao = new UserDaoImpl();
    private RestaurantDao restDao = new RestaurantDaoImpl();
    private ReservationDao resDao = new ReservationDaoImpl();

    //Post actions

    public Result makeReservation() {
        JsonNode json = request().body().asJson();

        if (json == null)
            return badRequest("Invalid Json is null");

        try {
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

            if (tempReservation.getTable() != null) {
                resDao.CreateReservation(tempReservation);
                ObjectNode returnValue = (new ObjectMapper()).createObjectNode();
                returnValue.put("id", tempReservation.id);
                returnValue.put("idTable", tempReservation.getTable().id);
                returnValue.put("idUser", tempReservation.getUser().id);
                returnValue.put("persons", tempReservation.getPersons());
                returnValue.put("reservationDateTime", tempReservation.getReservationDateTime().toString());

                return ok((new ObjectMapper()).writeValueAsString(returnValue));
            }

            //If there are no single tables try combinations

            //#1 try double tables ex for 5 people try 1+4 2+3 , for 10 1+9 2+8 3+7 4+5

            //Get all available tables that have less than < number of guests
            Map<Integer, ArrayList<Table>> freeLessTablesMap= getFreeTablesmap(tempReservation.getRestaurant().id, tempReservation.getPersons(), tempReservation.getReservationDateTime(), tempReservation.getReservationEndDateTime());

            //Try Combinations
            for(int i=1, j=tempReservation.getPersons()-1; i<j; i++, j--){
                if(freeLessTablesMap.containsKey(i) && freeLessTablesMap.containsKey(j) && freeLessTablesMap.get(i).size()>0 && freeLessTablesMap.get(j).size()>0){

                    Reservation part2 = getReservationFromRequest(json);

                    tempReservation.setTable(freeLessTablesMap.get(i).get(0));
                    part2.setTable(freeLessTablesMap.get(j).get(0));

                    resDao.CreateReservation(tempReservation);
                    resDao.CreateReservation(part2);

                    ObjectNode returnValue = (new ObjectMapper()).createObjectNode();
                    returnValue.put("id", tempReservation.id);
                    returnValue.put("idTable", tempReservation.getTable().id);
                    returnValue.put("idUser", tempReservation.getUser().id);
                    returnValue.put("persons", tempReservation.getPersons());
                    returnValue.put("reservationDateTime", tempReservation.getReservationDateTime().toString());

                    return ok((new ObjectMapper()).writeValueAsString(returnValue));
                }
            }

            return badRequest("No available tables for that time!");

        }
        catch (NullPointerException e){
            return badRequest("Missing json fields...");
        }
        catch (Exception e) {
            return badRequest(e.toString());
        }
    }

    public Result deleteReservation() {
        JsonNode json = request().body().asJson();

        if (json == null)
            return badRequest("Json is null");
        try {

            resDao.deleteReservation(json.get("idReservation").asLong());
            return ok();

        }
        catch (NullPointerException e){
            return badRequest("Missing json fields...");
        }
        catch (Exception e) {
            return badRequest(e.toString());
        }
    }

    public Result setReservationToFixed() {
        JsonNode json = request().body().asJson();

        if (json == null)
            return badRequest("Json is null");
        try {

            resDao.setReservationToFixed(json.get("idReservation").asLong(), json.get("request").asText());
            return ok();

        }
        catch (NullPointerException e){
            return badRequest("Missing json fields...");
        }
        catch (Exception e) {
            return badRequest(e.toString());
        }
    }

    public Result checkReservationAvailability() {
        JsonNode json = request().body().asJson();

        if (json == null)
            return badRequest("Invalid Json is null");

        try {
            //-------------Getting required data

            Timestamp reservationDateTime = getStampFromDate(json.get("reservationDate").asText(), json.get("reservationHour").asText());

            Long idRestaurant = json.get("idRestaurant").asLong();

            int persons = json.get("persons").asInt();

            //Set daytype to check length of stay depending on date
            String dayType= "workday";

            if(json.get("dayName").asText().equals("Sunday") || json.get("dayName").asText().equals("Saturday"))
                dayType="weekend";

            StayByDayType stayValues = resDao.getReservationLengthsForGuestNumber(idRestaurant, persons, dayType);

            //sets to default for restaurant
            int lengthOfStay = restDao.getRestaurantbyId(idRestaurant).getDefaultStay();

            //if there is stayValues set length of stay to corresponding day part length
            if(stayValues!=null){
                if(Integer.parseInt(json.get("reservationHour").asText().substring(0,1))<12)
                    lengthOfStay = stayValues.getMorning();
                if(Integer.parseInt(json.get("reservationHour").asText().substring(0,1))>=12)
                    lengthOfStay = stayValues.getDay();

                if(Integer.parseInt(json.get("reservationHour").asText().substring(0,1))>=18)
                    lengthOfStay = stayValues.getEvening();

            }


            //Get reservation end time
            Timestamp reservationEnd = new Timestamp(reservationDateTime.getTime() + (lengthOfStay * 60) * 1000);

            //Check availability
            ObjectNode returnValue = checkAvailability(persons, idRestaurant, reservationDateTime, reservationEnd);

            return ok((new ObjectMapper()).writeValueAsString(returnValue));

        }
        catch (NullPointerException e){
            return badRequest("Missing json fields...");
        }
        catch (Exception e) {
            return badRequest(e.toString());
        }
    }

    //Logic methods

    private ObjectNode checkAvailability(int persons, Long idRestaurant, Timestamp reservationDateTime, Timestamp reservationEnd) throws Exception {
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

        //If there are no single tables try combinations

        if(!setTimes.contains(reservationDateTime)){
            //#1 try double tables ex for 5 people try 1+4 2+3 , for 10 1+9 2+8 3+7 4+6 5+5

            //Get all available tables that have less than < number of guests
            Map<Integer, ArrayList<Table>> freeLessTablesMap= getFreeTablesmap(idRestaurant, persons, reservationDateTime, reservationEnd);

            //Try Combinations
            for(int i=1, j=persons-1; i<j; i++, j--){
                if(freeLessTablesMap.containsKey(i) && freeLessTablesMap.containsKey(j) && freeLessTablesMap.get(i).size()>0 && freeLessTablesMap.get(j).size()>0){
                    setTimes.add(reservationDateTime);
                    break;
                }
            }
        }



        ArrayList<String> convertedTime = new ArrayList<>();

        for (Timestamp stamp : setTimes) {
            if (!stamp.before(new Date()))
                convertedTime.add(getTimeStringFromStamp(stamp));
        }

        ObjectNode nodeValue = (new ObjectMapper()).createObjectNode();
        nodeValue.putArray("bestTime").addAll((ArrayNode) (new ObjectMapper()).valueToTree(convertedTime));
        nodeValue.put("tablesLeft", freeTables);
        nodeValue.put("idRestaurant", idRestaurant);
        nodeValue.put("restaurantName", restDao.getRestaurantbyId(idRestaurant).getRestaurantName());
        nodeValue.put("restaurantImageFileName", restDao.getRestaurantbyId(idRestaurant).getImageFileName());

        return nodeValue;
    }

    private Reservation getReservationFromRequest(JsonNode json) throws ParseException {

        //Get token
        Optional<String> token = request().getHeaders().get("Authorization");

        //Decode token and get user_id
        DecodedJWT jwt = JWT.decode(token.get().substring(7));
        Long idUser = jwt.getClaim("user_id").asLong();

        //Parse date fron json to timestamp
        Timestamp reservationDateTime = getStampFromDate(json.get("reservationDate").asText(), json.get("reservationHour").asText());

        Long idRestaurant = json.get("idRestaurant").asLong();

        int persons = json.get("persons").asInt();

        //Set daytype to check length of stay depending on date
        String dayType= "workday";

        if(json.get("dayName").asText().equals("Sunday") || json.get("dayName").asText().equals("Saturday"))
            dayType="weekend";

        StayByDayType stayValues = resDao.getReservationLengthsForGuestNumber(idRestaurant, persons, dayType);

        //sets to default for restaurant
        int lengthOfStay = restDao.getRestaurantbyId(idRestaurant).getDefaultStay();

        //if there is stayValues set length of stay to corresponding day part length
        if(stayValues!=null){
            if(Integer.parseInt(json.get("reservationHour").asText().substring(0,1))<12)
                lengthOfStay = stayValues.getMorning();
            if(Integer.parseInt(json.get("reservationHour").asText().substring(0,1))>=12)
                lengthOfStay = stayValues.getDay();

            if(Integer.parseInt(json.get("reservationHour").asText().substring(0,1))>=18)
                lengthOfStay = stayValues.getEvening();
        }

        //Get reservation end
        Timestamp reservationEnd = new Timestamp(reservationDateTime.getTime() + (lengthOfStay * 60) * 1000);

        Reservation returnReservation = new Reservation(persons, reservationDateTime, "", true, reservationEnd, new Timestamp(System.currentTimeMillis()));

        returnReservation.setRestaurant(restDao.getRestaurantbyId(idRestaurant));
        returnReservation.setUser(userDao.getUserbyId(idUser));

        return returnReservation;
    }


    private Map<Integer, ArrayList<Table>> getFreeTablesmap (Long idRest, int capacity, Timestamp begin, Timestamp end){
        List<Table> freeLessTables = resDao.findFreeTablesForLessPeople(idRest, capacity,begin, end);

        //Map tables to lists with key by sitting capacity
        Map<Integer, ArrayList<Table>> freeLessTablesMap = new HashMap<>();
        for (Table lessTable: freeLessTables) {
            freeLessTablesMap.computeIfAbsent(lessTable.getSitting_places(), k->new ArrayList<>()).add(lessTable);
        }
        return  freeLessTablesMap;
    }

    private String getTimeStringFromStamp(Timestamp stamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        return dateFormat.format(stamp);
    }

    private Timestamp getStampFromDate(String reservationDate, String reservationHour) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date parsedDate = dateFormat.parse(reservationDate + " " + reservationHour);

        if (parsedDate.before(new Date()))
            throw new ParseException("Date is expired", 1);

        return new Timestamp(parsedDate.getTime());
    }

    //-------------------RECURSIVE
    //For each table reservations that evaluate true to the expression: (endOfReservation > userReservationStartTime) and ( startOfReservation < endTimeOfUserReservation)
    //If there is none , the table is free for that time and the reservation is available
    private Reservation FindReservationColisions(Long resaurant_id, Long table_id, Timestamp start, Timestamp end) {

        try {
            List<Reservation> collisions = resDao.findColisions(resaurant_id, table_id, start, end);

            return collisions.get(0);
        } catch (Exception e) {
            return null;
        }
    }

    //Recursive algorithm to find the nearest available time of a table
    //Left side takes start as (beginning of collision - the length of the reservation), end is the beginning of collision
    private Timestamp FindNextAvailableTimeLeft(Long resaurant_id, Long table_id, Timestamp start, Timestamp end) {
        Reservation colision = FindReservationColisions(resaurant_id, table_id, start, end);

        if (colision == null)
            return start;
        else {
            //Calculates new start time
            Timestamp startNewLeft = new Timestamp(colision.getReservationDateTime().getTime() - (end.getTime() - start.getTime()));
            return FindNextAvailableTimeLeft(resaurant_id, table_id, startNewLeft, colision.getReservationDateTime());
        }
    }

    //Right side takes start as the end time of the collision, and the end as the (end of collision + length of reservation)
    private Timestamp FindNextAvailableTimeRight(Long resaurant_id, Long table_id, Timestamp start, Timestamp end) {
        Reservation colision = FindReservationColisions(resaurant_id, table_id, start, end);

        if (colision == null)
            return start;
        else {
            //Calculates new start time
            Timestamp endNewRight = new Timestamp(colision.getReservationEndDateTime().getTime() + (end.getTime() - start.getTime()));
            return FindNextAvailableTimeRight(resaurant_id, table_id, colision.getReservationEndDateTime(), endNewRight);

        }
    }


}
