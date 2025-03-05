package dataaccess;

import model.UserData;

import java.util.Collection;
import java.util.HashMap;

public class UserDAO {
    final private HashMap<String, UserData> users = new HashMap<>();

    public UserData addUser(UserData newUserData) {
        users.put(newUserData.username(), newUserData);
        return newUserData;
    }

    public Collection<UserData> listUsers() {
        return users.values();
    }

    public UserData getUser(String username) {
        return users.get(username);
    }

    public void deleteAllUsers() {
        users.clear();
    }

}
