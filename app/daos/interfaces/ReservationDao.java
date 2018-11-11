package daos.interfaces;

import models.Reservation;
import models.Table;

import java.sql.Timestamp;
import java.util.List;

public interface ReservationDao {

    void CreateReservation(Reservation reservation);
    List<Table> getTablesOfRestaurantWithPersons(int persons, Long restaurant_id);
    List<Reservation> findColisions(Long resaurant_id, Long table_id, Timestamp start, Timestamp end);
}
