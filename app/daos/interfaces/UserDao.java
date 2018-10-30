package daos.interfaces;

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

    //Update methods

    //Delete methods
}
