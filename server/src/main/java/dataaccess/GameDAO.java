package dataaccess;

import model.GameData;

import java.util.Collection;
import java.util.HashMap;

public class GameDAO {
    final private HashMap<Integer, GameData> games = new HashMap<>();

    public GameData addGame(GameData gameData) {
        games.put(gameData.hashCode(), gameData);
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
            throw new IllegalArgumentException("Game not found");
        }

        /** GameData Updated:
         *  GameID,
         *  whiteUsername,
         *  blackUsername,
         *  ChessGame,
         *  gameName
         */

        switch (playerColor.toUpperCase()) {
            case "WHITE":
                if (gameData.whiteUsername() != null) {
                    throw new IllegalArgumentException("already taken");
                }
                gameData = new GameData(
                        gameData.gameID(),
                        username,
                        gameData.blackUsername(),
                        gameData.game(),
                        gameData.gameName()
                );
                break;
            case "BLACK":
                if (gameData.blackUsername() != null) {
                    throw new IllegalArgumentException("already taken");
                }
                gameData = new GameData(
                        gameData.gameID(),
                        gameData.whiteUsername(),
                        username,
                        gameData.game(),
                        gameData.gameName()
                );
                break;
            default:
                throw new IllegalArgumentException("bad request");
        }

        games.put(gameID, gameData); //Update the game with the new player
    }
}
