package daos.implementations;

import daos.interfaces.ReservationDao;
import models.GuestStay;
import models.Reservation;
import models.StayByDayType;
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
                .le("sitting_places", persons+2)
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


    @Override
    public List<Table> findFreeTablesForLessPeople(Long idRestaurant, int guestNumber, Timestamp start, Timestamp end){
        return Table.getFinder().query().where()
                .eq("restaurant_id", idRestaurant)
                .lt("sitting_places", guestNumber)
                .notIn("id", Table.getFinder().query().fetch("reservations").where()
                        .or()
                        .betweenProperties("reservations.reservationDateTime", "reservations.reservationEndDateTime", start)
                        .betweenProperties("reservations.reservationDateTime", "reservations.reservationEndDateTime", end)
                        .between("reservations.reservationDateTime", start, end)
                        .endOr()
                        .findIds()
                ).findList();
    }


    @Override
    public StayByDayType getReservationLengthsForGuestNumber(Long restaurant_id, int guestNumber, String dayType){
        return StayByDayType.getFinder().query()
                .where()
                .eq("dayType", dayType)
                .eq("guestStay.restaurant.id", restaurant_id)
                .eq("guestStay.guestNumber", guestNumber)
                .findOne();
    }

    @Override
    public List<GuestStay> getReservationLengthsForRestaurant(Long id){
        return GuestStay.getFinder().query().where().eq("restaurant.id", id).findList();
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
