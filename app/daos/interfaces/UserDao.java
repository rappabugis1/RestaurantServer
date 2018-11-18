package daos.interfaces;

import models.Reservation;
import models.User;
import models.UserData;

import java.util.List;

public interface UserDao {

    //Create methods
    Boolean createUser (User newUser);

    //Read methods

    List<User> getUsers();
    User getUserbyId(Long id);
    User getUserbyEmail(String email);
    Boolean checkEmailExists (String email);
    User verifyProvidedInfo(String email, String password);

    List<Reservation> getUserReservationsActive(Long id);

    List<Reservation> getUserReservationsPassed(Long id);

    //Update methods

    //Delete methods
}
