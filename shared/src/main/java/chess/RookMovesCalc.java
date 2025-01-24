package chess;

import java.util.ArrayList;
import java.util.Collection;

public class RookMovesCalc implements PieceMoveCalculator {

    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        int row = position.getRow();
        int col = position.getColumn();
        Collection<ChessMove> moves = new ArrayList<>();
        ChessGame.TeamColor currColor = board.getPiece(position).getTeamColor();

        // list of directional vectors
        int[][] directions = {{1, 0}, {0, -1}, {-1, 0}, {0, 1}};

        for (int[] dir : directions) {
            for (int i = 1; i < 8; i++) {
                int newRow = row + i * dir[0];
                int newCol = col + i * dir[1];

                //check boundaries
                if (newRow <= 0 || newRow > 8 || newCol <= 0 || newCol > 8) {
                    break;
                    //reached edge, stop adding in this direction
                }

                ChessPosition newPos = new ChessPosition(newRow, newCol);
                ChessPiece piece = board.getPiece(newPos);

                //check for other pieces
                if (piece == null) {
                    moves.add(new ChessMove(position, newPos, null));
                } else {
                    if (piece.getTeamColor() != currColor) {
                        moves.add(new ChessMove(position, newPos, null));
                    }
                    break; //occupied space, stop adding in this direction
                }
            }
        }
        return moves;
    }
}
