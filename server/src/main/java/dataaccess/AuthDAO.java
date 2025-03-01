package dataaccess;

import model.AuthData;

import java.util.Collection;
import java.util.HashMap;

public class AuthDAO {
    final private HashMap<String, AuthData> auths = new HashMap<>();

    public AuthData addAuth(AuthData new_authData) {
        auths.put(new_authData.authToken(), new_authData);
        return new_authData;
    }

    public Collection<AuthData> listAuths() {
        return auths.values();
    }

    public AuthData getAuth(String authToken) {
        return auths.get(authToken);
    }

    public void deleteAuth(String authToken) {
        auths.remove(authToken);
    }

    public void deleteAllAuths() {
        auths.clear();
    }
}
