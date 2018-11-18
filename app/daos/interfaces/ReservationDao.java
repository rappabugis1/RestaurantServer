package daos.interfaces;

import models.Reservation;
import models.Table;

import java.sql.Timestamp;
import java.util.List;

public interface ReservationDao {

    void CreateReservation(Reservation reservation);
    Reservation getReservationById(Long id);
    List<Table> getTablesOfRestaurantWithPersons(int persons, Long restaurant_id);
    List<Reservation> findColisions(Long resaurant_id, Long table_id, Timestamp start, Timestamp end);

    void setReservationToFixed(Long id, String request) throws Exception;

    void deleteReservation(Long id) throws Exception;
}
