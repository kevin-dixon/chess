package dataaccess;

import model.UserData;

import java.util.Collection;
import java.util.HashMap;

public class UserDAO {
    final private HashMap<Integer, UserData> users = new HashMap<>();

    public UserData addUser(UserData new_userData) {
        users.put(new_userData.hashCode(), new_userData);
        return new_userData;
    }

    public Collection<UserData> listUsers() {
        return users.values();
    }

    public UserData getUser(UserData find_userData) {
        return users.get(find_userData.hashCode());
    }

    public void deleteUser(UserData del_userData) {
        users.remove(del_userData.hashCode());
    }

    public void deleteAllUsers() {
        users.clear();
    }

}
