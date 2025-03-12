import chess.*;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import server.Server;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Server: " + piece);

        // Instantiate DatabaseManager and configure the database
        DatabaseManager dbManager = new DatabaseManager();
        try {
            dbManager.configureDatabase();
            System.out.println("Database configured successfully.");
        } catch (DataAccessException e) {
            System.err.println("Database configuration failed: " + e.getMessage());
            return;
        }

        Server myServer = new Server();
        myServer.run(8080);
    }
}