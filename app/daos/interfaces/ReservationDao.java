package daos.interfaces;

import models.GuestStay;
import models.Reservation;
import models.StayByDayType;
import models.Table;

import java.sql.Timestamp;
import java.util.List;

public interface ReservationDao {

    void CreateReservation(Reservation reservation);

    Reservation getReservationById(Long id);

    List<Table> getTablesOfRestaurantWithPersons(int persons, Long restaurant_id);

    List<Reservation> findColisions(Long resaurant_id, Long table_id, Timestamp start, Timestamp end);

    StayByDayType getReservationLengthsForGuestNumber(Long restaurant_id, int guestNumber, String dayType);

    List<GuestStay> getReservationLengthsForRestaurant(Long id);

    void setReservationToFixed(Long id, String request) throws Exception;

    void deleteReservation(Long id) throws Exception;
}
