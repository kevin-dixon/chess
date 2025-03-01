package dataaccess;

import model.AuthData;

import java.util.Collection;
import java.util.HashMap;

public class AuthDAO {
    final private HashMap<Integer, AuthData> auths = new HashMap<>();

    public AuthData addAuth(AuthData new_authData) {
        auths.put(new_authData.hashCode(), new_authData);
        return new_authData;
    }

    public Collection<AuthData> listAuths() {
        return auths.values();
    }

    public AuthData getAuth(AuthData find_authData) {
        return auths.get(find_authData.hashCode());
    }

    public void deleteAuth(AuthData del_authData) {
        auths.remove(del_authData.hashCode());
    }

    public void deleteAllAuths() {
        auths.clear();
    }
}
