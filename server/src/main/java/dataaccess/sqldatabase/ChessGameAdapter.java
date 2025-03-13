package dataaccess.sqldatabase;

import com.google.gson.*;
import chess.ChessGame;

import java.lang.reflect.Type;

public class ChessGameAdapter implements JsonDeserializer<ChessGame>, JsonSerializer<ChessGame> {

    @Override
    public ChessGame deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return context.deserialize(json, ChessGame.class);
    }

    @Override
    public JsonElement serialize(ChessGame src, Type typeOfSrc, JsonSerializationContext context) {
        return context.serialize(src, ChessGame.class);
    }
}