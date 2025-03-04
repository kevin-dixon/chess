package dataaccess;

import model.UserData;

import java.util.Collection;
import java.util.HashMap;

public class UserDAO {
    final private HashMap<String, UserData> users = new HashMap<>();

    public UserData addUser(UserData new_userData) {
        users.put(new_userData.username(), new_userData);
        return new_userData;
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
