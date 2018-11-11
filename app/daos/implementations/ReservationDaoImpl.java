package daos.implementations;

import daos.interfaces.ReservationDao;
import models.Reservation;
import models.Table;

import java.sql.Timestamp;
import java.util.List;

public class ReservationDaoImpl implements ReservationDao {

    //Create
    @Override
    public void CreateReservation(Reservation reservation){
        reservation.save();
    }

    //Read

    @Override

    public List<Table> getTablesOfRestaurantWithPersons(int persons, Long restaurant_id){
        return Table.getFinder().query()
                .where()
                .eq("restaurant_id", restaurant_id)
                .and()
                .ge("sitting_places",persons)
                .findList();
    }

    @Override

    public List<Reservation> findColisions(Long resaurant_id, Long table_id, Timestamp start, Timestamp end){

        return Reservation.getFinder().query()
                .where()
                .eq("restaurant_id", resaurant_id)
                .eq("table_id", table_id)
                .gt("reservation_end_date_time", start)
                .lt("reservation_date_time", end)
                .findList();
    }




}
