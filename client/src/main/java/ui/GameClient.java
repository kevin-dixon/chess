package ui;

import server.ServerFacade;
import static ui.EscapeSequences.*;

public class GameClient {
    private final ServerFacade server;
    private final String authToken;
    private final String gameId;
    private final boolean isBlackPerspective;

    public GameClient(String serverUrl, String authToken, String gameId, boolean isBlackPerspective) {
        this.server = new ServerFacade(serverUrl); // Create a new ServerFacade instance
        this.authToken = authToken;
        this.gameId = gameId;
        this.isBlackPerspective = isBlackPerspective;
    }

    public String drawChessBoard() {
        StringBuilder board = new StringBuilder();

        // Set text color to white
        board.append(SET_TEXT_COLOR_WHITE);

        // Add top border with color
        board.append(SET_BG_COLOR_BORDER).append("   ").append("                                ").append(RESET_BG_COLOR).append("\n");

        // Loop through rows
        for (int row = 0; row < 8; row++) {
            int displayRow = isBlackPerspective ? 8 - row : row + 1; // Reverse the row numbers
            board.append(SET_BG_COLOR_BORDER).append(" ").append(displayRow).append(" ").append(RESET_BG_COLOR); // Add row number with color

            // Loop through columns
            for (int col = 0; col < 8; col++) {
                int displayCol = isBlackPerspective ? 7 - col : col; // Adjust column for perspective
                boolean isDarkSquare = (row + col) % 2 == 1; // Determine square color
                String squareColor = isDarkSquare ? SET_BG_COLOR_DARK_GREY : SET_BG_COLOR_LIGHT_GREY;

                // Get the piece for the current square
                String piece = getInitialPiece(isBlackPerspective ? row : 7 - row, isBlackPerspective ? col : 7 - col);

                // Append the square with the piece
                board.append(squareColor).append(piece).append(RESET_BG_COLOR);
            }

            board.append(SET_BG_COLOR_BORDER).append("   ").append(RESET_BG_COLOR).append("\n"); // End the row with border color
        }

        // Add column labels with adjusted spacing
        board.append(SET_BG_COLOR_BORDER).append("    "); // Add initial padding for alignment
        String[] columnLabels = isBlackPerspective ? new String[]{"a", "b", "c", "d", "e", "f", "g", "h"} : new String[]{"h", "g", "f", "e", "d", "c", "b", "a"};
        for (String label : columnLabels) {
            board.append(label).append("\u2007").append("\u2007").append("\u2007"); // Add extra spaces to align with chess piece columns
        }
        board.append("  ").append(RESET_BG_COLOR).append("\n");

        // Reset text color
        board.append(RESET_TEXT_COLOR);

        return board.toString();
    }

    private String getInitialPiece(int row, int col) {
        // Initial chessboard setup
        String[][] initialBoard = {
                {BLACK_ROOK, BLACK_KNIGHT, BLACK_BISHOP, BLACK_QUEEN, BLACK_KING, BLACK_BISHOP, BLACK_KNIGHT, BLACK_ROOK},
                {BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN},
                {EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
                {EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
                {EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
                {EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
                {WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN},
                {WHITE_ROOK, WHITE_KNIGHT, WHITE_BISHOP, WHITE_QUEEN, WHITE_KING, WHITE_BISHOP, WHITE_KNIGHT, WHITE_ROOK}
        };

        return initialBoard[row][col];
    }

    public String help() {
        return """
                draw - show example board
                exit - exit the game
                help - display this help text
                """;
    }

    public Object evaluate(String input) {
        return switch (input.toLowerCase()) {
            case "draw" -> drawChessBoard();
            case "exit" -> exitGame();
            default -> help();
        };
    }

    private String exitGame() {
        try {
            // Call the server to leave the game
            server.leaveGame(authToken, Integer.parseInt(gameId));
            return "Exiting game.";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
    }
}
