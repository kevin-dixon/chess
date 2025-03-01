package dataaccess;

import model.User;

import java.util.Collection;
import java.util.HashMap;

public class UserDAO {
    final private HashMap<Integer, User> users = new HashMap<>();

    public User addUser(User new_user) {
        users.put(new_user.hashCode(), new_user);
        return new_user;
    }

    public Collection<User> listUsers() {
        return users.values();
    }

    public User getUser(User find_user) {
        return users.get(find_user.hashCode());
    }

    public void deleteUser(User del_user) {
        users.remove(del_user.hashCode());
    }

    public void deleteAllUsers() {
        users.clear();
    }

}
