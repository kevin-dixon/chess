package dataaccess;

import model.UserData;

public class MySqlDataAccess {

    private final String[] createStatements = {
            //TODO: finish
            //"initializing..." video 3:02
            """
            CREATE TABLE IF NOT EXISTS 
            """
    };

    private void configureDatabase() {
        //TODO: implement
        //"Initializaing..." video 2:57
    }

    void storeUserPassword(String username, String password) {
        //todo: implement
        //"password hasing" whole video
    }
    void verifyUser(String username, String password) {
        //todo: implement
        //"password hasing" whole video
    }

    public void addUser(UserData user) throws DataAccessException {
        //todo: implement
        //"...serialization..." video 2:00
    }
}
