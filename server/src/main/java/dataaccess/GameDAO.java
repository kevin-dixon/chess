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

    public GameData getGame(GameData find_gameData) {
        return games.get(find_gameData.hashCode());
    }

    public void deleteGame(GameData del_gameData) {
        games.remove(del_gameData.hashCode());
    }

    public void deleteAllGames() {
        games.clear();
    }
}
