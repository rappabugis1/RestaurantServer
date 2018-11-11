package daos.implementations;

import daos.interfaces.ReservationDao;
import models.Reservation;
import models.Table;
import play.Logger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ReservationDaoImpl implements ReservationDao {

    //Create
    @Override
    public void CreateReservation(Reservation reservation){
        reservation.save();
    }

    //Read
    @Override
    public Table CheckIfReservationAvailable(int persons, Long restaurant_id, Timestamp reservationDateTime, Timestamp reservationEndDateTime) throws Exception{

        //Get tables that are in restaurant
        List<Table> tableIds = Table.getFinder().query()
                .where()
                .eq("restaurant_id", restaurant_id)
                .and()
                .ge("sitting_places",persons)
                .findList();

        if(tableIds.isEmpty())
            throw new Exception("No available tables for that amount of people");

        ArrayList<Timestamp> availableTimes= new ArrayList<>();
        int freeTables =0;

    try{
        for (Table table: tableIds) {
            Reservation reservationColision = FindReservationColisions(restaurant_id, table.id, reservationDateTime, reservationEndDateTime);

            //If there is no collisionn add the reservation time to available times, that means the wanted time is free
            if(reservationColision==null){
                availableTimes.add(reservationDateTime);
                freeTables++;

                return table;
            }
            else{

                //If not, begin recursive algorithms to find the left and right best times available
                //Find the left and right best time, add them to the array

                Long lengthOfReservation = reservationEndDateTime.getTime()-reservationDateTime.getTime();

                Timestamp leftBest= FindNextAvailableTimeLeft(
                        restaurant_id,
                        table.id,
                        new Timestamp(reservationColision.getReservationDateTime().getTime()-lengthOfReservation),
                        reservationColision.getReservationDateTime()
                );

                Timestamp rightBest = FindNextAvailableTimeRight(
                        restaurant_id,
                        table.id,
                        reservationColision.getReservationEndDateTime(),
                        new Timestamp(reservationColision.getReservationEndDateTime().getTime()+lengthOfReservation)
                );

                availableTimes.add(leftBest);
                availableTimes.add(rightBest);
                freeTables++;
            }
        }

        Logger.info(availableTimes.toString());
    } catch (Exception e){
        throw e;
    }


        return null;
    }

    //Helper methods

    //For each table reservations that evaluate true to the expression: (endOfReservation > userReservationStartTime) and ( startOfReservation < endTimeOfUserReservation)
    //If there is none , the table is free for that time and the reservation is available
    private Reservation FindReservationColisions(Long resaurant_id, Long table_id, Timestamp start, Timestamp end){

        try{
             List<Reservation> collisions= Reservation.getFinder().query()
                .where()
                .eq("restaurant_id", resaurant_id)
                .eq("table_id", table_id)
                .gt("reservation_end_date_time", start)
                .lt("reservation_date_time", end)
                .findList();

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
