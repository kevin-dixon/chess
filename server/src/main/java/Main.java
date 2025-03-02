import chess.*;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import server.Server;
import service.ClearService;
import service.GameService;
import service.UserService;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Server: " + piece);

        AuthDAO authDataAccess = new AuthDAO();
        GameDAO gameDataAccess = new GameDAO();
        UserDAO userDataAccess = new UserDAO();

        //TODO: remove unneeded DAO when done
        var clearService = new ClearService(
                authDataAccess,
                gameDataAccess,
                userDataAccess
        );
        var gameService = new GameService(
                gameDataAccess
        );
        var userService = new UserService(
                authDataAccess,
                userDataAccess
        );


        Server myServer = new Server();
        myServer.run(8080);
    }
}