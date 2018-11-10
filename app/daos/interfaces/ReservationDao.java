package daos.interfaces;

import models.Reservation;
import models.Table;

public interface ReservationDao {

    void CreateReservation(Reservation reservation);
    Table CheckIfReservationAvailable(Reservation reservation) throws Exception;
}
