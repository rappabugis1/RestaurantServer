package daos.implementations;

import daos.interfaces.UserDao;
import models.Reservation;
import models.User;
import util.PasswordUtil;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

public class UserDaoImpl implements UserDao {

    //Create methods
    @Override
    public Boolean createUser(User newUser) {
        if (checkEmailExists(newUser.getEmail())) {
            return false;
        }
        newUser.save();
        return true;
    }


    //Read methods

    @Override
    public int getNumberUsers(){
        return User.finder.query().findCount();
    }

    @Override
    public List<User> getUsers() {
        return User.finder.all();
    }

    @Override
    public User getUserbyId(Long id) {
        return User.finder.byId(id);
    }

    @Override
    public User getUserbyEmail(String email) {
        return User.finder.query()
                .where()
                .eq("email", email)
                .findOne();
    }

    @Override
    public Boolean checkEmailExists(String email) {
        return (User.finder.query()
                .where()
                .eq("email", email)
                .findCount()) != 0;
    }

    @Override
    public User verifyProvidedInfo(String email, String password) {
        User tempUser = getUserbyEmail(email);

        if (tempUser != null) {
            String securedPassword = tempUser.getPassword();
            String salt = tempUser.getSalt();

            if (PasswordUtil.verifyUserPassword(password, securedPassword, salt)) {
                return tempUser;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }


    @Override
    public List<Reservation> getUserReservationsActive(Long id) {
        Timestamp today = new Timestamp(new Date().getTime());

        return Reservation.getFinder().query().where().eq("user.id", id).ge("reservationDateTime", today).setOrderBy("reservationDateTime").findList();
    }

    @Override
    public List<Reservation> getUserReservationsPassed(Long id) {
        Timestamp today = new Timestamp(new Date().getTime());

        return Reservation.getFinder().query().where().eq("user.id", id).lt("reservationDateTime", today).setOrderBy("reservationDateTime").findList();
    }

    //Update methods

    //Delete methods
}
