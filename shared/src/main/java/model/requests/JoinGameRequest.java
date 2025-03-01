package model.requests;

import chess.ChessGame;

public record JoinGameRequest (
        String username,
        ChessGame.TeamColor playerColor,
        int gameID,
        String authToken
){}
