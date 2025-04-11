package model.requests;

public class ObserveGameRequest {
    private final int gameID;

    public ObserveGameRequest(int gameID) {
        this.gameID = gameID;
    }

    public int getGameID() {
        return gameID;
    }
}