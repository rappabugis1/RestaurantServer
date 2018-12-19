package daos.interfaces;

import com.fasterxml.jackson.databind.JsonNode;
import io.ebean.PagedList;
import models.Reservation;
import models.User;

import java.util.List;

public interface UserDao {

    //Create methods
    Boolean createUser(User newUser);

    //Read methods

    int getNumberUsers();

    List<User> getUsers();

    User getUserbyId(Long id);

    User getUserbyEmail(String email);

    Boolean checkEmailExists(String email);

    User verifyProvidedInfo(String email, String password);

    PagedList<User> getFilteredUsers(JsonNode json);

    List<Reservation> getUserReservationsActive(Long id);

    List<Reservation> getUserReservationsPassed(Long id);

    User editUser(User user) throws Exception;

    void deleteUser(User user);

    //Update methods

    //Delete methods
}
