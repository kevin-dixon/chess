package dataaccess.sqldatabase;

import chess.ChessGame;
import chess.ChessBoard;
import chess.ChessGame.TeamColor;
import com.google.gson.*;

import java.lang.reflect.Type;

public class ChessGameAdapter implements JsonSerializer<ChessGame>, JsonDeserializer<ChessGame> {

    @Override
    public JsonElement serialize(ChessGame src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("currTeam", src.getTeamTurn().name());
        jsonObject.add("gameBoard", context.serialize(src.getBoard()));
        return jsonObject;
    }

    @Override
    public ChessGame deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        TeamColor currTeam = TeamColor.valueOf(jsonObject.get("currTeam").getAsString());
        ChessBoard gameBoard = context.deserialize(jsonObject.get("gameBoard"), ChessBoard.class);

        ChessGame chessGame = new ChessGame();
        chessGame.setTeamTurn(currTeam);
        chessGame.setBoard(gameBoard);
        return chessGame;
    }
}