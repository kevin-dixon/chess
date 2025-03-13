import chess.*;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import server.Server;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Server: " + piece);

        Server myServer = new Server();
        myServer.run(8080);
    }
}