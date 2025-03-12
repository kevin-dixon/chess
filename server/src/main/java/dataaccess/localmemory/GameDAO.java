package dataaccess.localmemory;

import model.GameData;

import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;

public class GameDAO {
    //Using GameID as key in hash
    final private HashMap<Integer, GameData> games = new HashMap<>();

    public GameData addGame(GameData gameData) {
        games.put(gameData.gameID(), gameData);
        return gameData;
    }

    public Collection<GameData> listGames() {
        return games.values();
    }

    public GameData getGameByID(int gameID) {
        return games.get(gameID);
    }

    public void deleteAllGames() {
        games.clear();
    }

    public void addPlayerToGame(int gameID, String playerColor, String username) {
        GameData gameData = games.get(gameID);
        if (gameData == null) {
            throw new IllegalArgumentException("bad request");
        }

        //Update player username
        GameData updatedGame;
        if (Objects.equals(playerColor, "BLACK")) {
            updatedGame = gameData.updateBlackUser(username);
        } else if (Objects.equals(playerColor, "WHITE")) {
            updatedGame = gameData.updateWhiteUser(username);
        } else {
            throw new IllegalArgumentException("bad request");
        }

        //Swap out new gameData
        games.put(updatedGame.gameID(), updatedGame);
    }
}