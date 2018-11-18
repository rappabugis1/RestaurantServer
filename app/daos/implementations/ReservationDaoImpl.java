package daos.implementations;

import daos.interfaces.ReservationDao;
import models.Reservation;
import models.Table;

import java.sql.Timestamp;
import java.util.List;

public class ReservationDaoImpl implements ReservationDao {

    //Create
    @Override
    public void CreateReservation(Reservation reservation) {
        reservation.save();
    }

    //Read

    @Override
    public Reservation getReservationById(Long id) {
        return Reservation.getFinder().byId(id);
    }

    @Override

    public List<Table> getTablesOfRestaurantWithPersons(int persons, Long restaurant_id) {
        return Table.getFinder().query()
                .where()
                .eq("restaurant_id", restaurant_id)
                .and()
                .ge("sitting_places", persons)
                .findList();
    }

    @Override

    public List<Reservation> findColisions(Long resaurant_id, Long table_id, Timestamp start, Timestamp end) {

        return Reservation.getFinder().query()
                .where()
                .eq("restaurant_id", resaurant_id)
                .eq("table_id", table_id)
                .gt("reservation_end_date_time", start)
                .lt("reservation_date_time", end)
                .findList();
    }

    //Edit

    @Override
    public void setReservationToFixed(Long id, String request) throws Exception {
        Reservation tempReservation = getReservationById(id);
        if (tempReservation == null)
            throw new Exception("Reservation does not exist");

        tempReservation.setRequest(request);
        tempReservation.setTemp(false);
        tempReservation.update();
    }

    //Delete

    @Override
    public void deleteReservation(Long id) throws Exception {
        Reservation tempReservation = getReservationById(id);
        if (tempReservation == null)
            throw new Exception("Reservation does not exist");

        if (!tempReservation.getTemp())
            throw new Exception("Reservation is not temporary");

        getReservationById(id).delete();
    }


}
