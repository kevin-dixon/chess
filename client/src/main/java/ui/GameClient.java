package ui;

import chess.ChessGame;
import chess.ChessPiece;
import server.ServerFacade;
import model.GameData;

import java.util.List;

import static chess.ChessPiece.PieceType.*;
import static ui.EscapeSequences.*;

public class GameClient {
    private final ServerFacade server;
    private final String authToken;
    private final String gameId;
    private final boolean isBlackPerspective;
    private final ChessGame chessGame; // Add a reference to the ChessGame model
    private final List<GameData> cachedGameList; // Cached list of games

    public GameClient(String serverUrl, String authToken, String gameId, boolean isBlackPerspective, List<GameData> cachedGameList) {
        this.server = new ServerFacade(serverUrl);
        this.authToken = authToken;
        this.gameId = gameId;
        this.isBlackPerspective = isBlackPerspective;
        this.cachedGameList = cachedGameList;

        // Fetch the game state from the server (or initialize locally)
        this.chessGame = new ChessGame(); // Replace with actual game state retrieval if needed

        // Automatically draw the board when joining the game
        drawBoardWithCommands();
    }

    private void drawBoardWithCommands() {
        System.out.print(ERASE_SCREEN);

        System.out.println(drawChessBoard());

        System.out.println(help());
    }

    public String drawChessBoard() {
        StringBuilder board = new StringBuilder();

        // Set text color to white
        board.append(SET_TEXT_COLOR_WHITE);

        // Add top border with color
        board.append(SET_BG_COLOR_BORDER).append("   ").append("                                ").append(RESET_BG_COLOR).append("\n");

        // Loop through rows
        for (int row = 0; row < 8; row++) {
            int displayRow = isBlackPerspective ? row + 1 : 8 - row; // Flip the row numbers
            board.append(SET_BG_COLOR_BORDER).append(" ").append(displayRow).append(" ").append(RESET_BG_COLOR); // Add row number with color

            // Loop through columns
            for (int col = 0; col < 8; col++) {
                boolean isDarkSquare = (row + col) % 2 == 1; // Determine square color
                String squareColor = isDarkSquare ? SET_BG_COLOR_DARK_GREY : SET_BG_COLOR_LIGHT_GREY;

                // Get the piece for the current square from the ChessGame model
                ChessPiece piece = chessGame.getPieceAt(isBlackPerspective ? row : 7 - row, isBlackPerspective ? 7 - col : col);

                // Determine the text color and symbol based on the piece type
                String textColor = piece != null && piece.isBlack() ? SET_TEXT_COLOR_BLACK : SET_TEXT_COLOR_WHITE;
                String pieceSymbol = getPieceSymbol(piece);

                // Append the square with the piece and appropriate colors
                board.append(squareColor).append(textColor).append(pieceSymbol).append(RESET_BG_COLOR).append(SET_TEXT_COLOR_WHITE);
            }

            board.append(SET_BG_COLOR_BORDER).append("   ").append(RESET_BG_COLOR).append("\n"); // End the row with border color
        }

        // Add column labels with adjusted spacing
        board.append(SET_BG_COLOR_BORDER).append("    ");
        String[] columnLabels = isBlackPerspective
                ? new String[]{"h", "g", "f", "e", "d", "c", "b", "a"}
                : new String[]{"a", "b", "c", "d", "e", "f", "g", "h"};
        for (String label : columnLabels) {
            board.append(label).append("\u2007").append("\u2007").append("\u2007"); // Add extra spaces to align with chess piece columns
        }
        board.append("  ").append(RESET_BG_COLOR).append("\n");

        // Reset text color
        board.append(RESET_TEXT_COLOR);

        return board.toString();
    }

    private String getPieceSymbol(ChessPiece piece) {
        if (piece == null) {
            return EMPTY; // Return empty square if no piece is present
        }

        // Return the appropriate symbol based on the piece type
        return switch (piece.getType()) {
            case KING -> piece.isBlack() ? BLACK_KING : WHITE_KING;
            case QUEEN -> piece.isBlack() ? BLACK_QUEEN : WHITE_QUEEN;
            case BISHOP -> piece.isBlack() ? BLACK_BISHOP : WHITE_BISHOP;
            case KNIGHT -> piece.isBlack() ? BLACK_KNIGHT : WHITE_KNIGHT;
            case ROOK -> piece.isBlack() ? BLACK_ROOK : WHITE_ROOK;
            case PAWN -> piece.isBlack() ? BLACK_PAWN : WHITE_PAWN;
        };
    }

    public String help() {
        return """
                draw - show example board
                exit - exit the game
                """;
    }

    public Object evaluate(String input) {
        return switch (input.toLowerCase()) {
            case "draw" -> {
                drawBoardWithCommands();
                yield "";
            }
            case "exit" -> exitGame();
            default -> help();
        };
    }

    private Object exitGame() {
        try {
            // Call the server to leave the game
            server.leaveGame(authToken, Integer.parseInt(gameId));
            return new UserClient(server.getServerUrl(), null, authToken, null, cachedGameList);
        } catch (ResponseException | Exception e) {
            return e.getMessage();
        }
    }
}