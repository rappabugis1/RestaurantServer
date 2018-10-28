package daos.implementations;

import daos.interfaces.UserDao;
import models.User;
import models.UserData;
import java.util.List;

public class UserDaoImpl implements UserDao {

    //Create methods
    @Override
    public Boolean createUser (User newUser){
        if(checkEmailExists(newUser.getEmail())){
            return false;
        }
        newUser.save();
        return true;
    }


    //Read methods
    @Override
    public List<User> getUsers() {
        return User.finder.all();
    }

    @Override
    public User getUserbyId(Long id){
        return User.finder.byId(id);
    }

    @Override
    public User getUserbyEmail(String email){
        return User.finder.query()
                .where()
                .eq("email", email)
                .findOne();
    }

    @Override
    public Boolean checkEmailExists (String email){
        return (User.finder.query()
                .where()
                .eq("email", email)
                .findCount())!=0;
    }

    @Override
    public User findUserByPassEmail (String email, String password){
        return User.finder.query()
                .where()
                .eq("email", email)
                .eq("password", password)
                .findOne();
    }



    //Update methods

    //Delete methods
}
