package daos.interfaces;

import models.Reservation;
import models.Table;

import java.sql.Timestamp;

public interface ReservationDao {

    void CreateReservation(Reservation reservation);
    Table CheckIfReservationAvailable(int persons, Long restaurant_id, Timestamp reservationDateTime, Timestamp reservationEndDateTime) throws Exception;
}
