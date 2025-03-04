package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.AuthData;
import model.GameData;
import model.requests.JoinGameRequest;
import spark.Request;

import java.util.Collection;
import java.util.Random;

public class GameService {
    private final GameDAO game_dao;
    private final AuthDAO auth_dao;

    public GameService(
            GameDAO gameDataAccess,
            AuthDAO authDataAccess
    ) {
        this.game_dao = gameDataAccess;
        this.auth_dao = authDataAccess;
    }

    public Collection<GameData> listGames() throws DataAccessException {
        return game_dao.listGames();
    }

    public int createGame(String authToken, String gameName) throws DataAccessException {
        //Validate authToken
        AuthData authData = auth_dao.getAuth(authToken);
        if (authData == null) {
            throw new DataAccessException("unauthorized");
        }

        /**
         *         String gameName,
         *         int gameID,
         *         String whiteUsername,
         *         String blackUsername,
         *         ChessGame game
         */

        //Create new game
        GameData newGame = new GameData(
                newGameID(),
                "",
                "",
                new ChessGame(),
                gameName);
        //Add new game to database
        game_dao.addGame(newGame);
        return newGame.gameID();
    }

    private int newGameID(){
        Random random = new Random();
        int randomNumber = 100000 + random.nextInt(900000); // Generates number between 100000 and 999999
        System.out.println(randomNumber);
        return randomNumber;
    }

    public void list(Request req) {
        //get authenticated
        //get all games
        //send response as array
    }

    public void create(Request req) {
        //get authenticated
        //create new game
        //send response as gamedata
    }

    public void join(Request req) {
        //get authenticated
        //get game data by id
        //verify color
        //update game data
        //update game in db
        //send response
    }


    public GameData createGame(GameData game) throws DataAccessException{
        if (getGame(game) != null) {
            //duplicate game
            throw new DataAccessException("Error: Game already exists");
        }
        return game_dao.addGame(game);
    }

    public boolean verifyColor(JoinGameRequest joinGameRequest, GameData game) throws DataAccessException{
        ChessGame.TeamColor newColor = joinGameRequest.playerColor();
        if (newColor == ChessGame.TeamColor.BLACK) {
            return game.blackUsername() == null;

        }
        else {
            return game.whiteUsername() == null;
        }
    }

    public void updateGameUser(GameData gameData, ChessGame.TeamColor playerColor, String username){
        //TODO: implement
    }

    public void updateGame(GameData gameData){
        //TODO: implement
    }

    public GameData getGame(GameData game) throws DataAccessException {
        return game_dao.getGame(game);
    }

}
