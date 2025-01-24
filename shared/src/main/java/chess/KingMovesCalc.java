package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KingMovesCalc implements PieceMoveCalculator {

    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        int row = position.getRow();
        int col = position.getColumn();
        Collection<ChessMove> moves = new ArrayList<>();
        ChessGame.TeamColor currColor = board.getPiece(position).getTeamColor();

        //create list of direction vectors
        int[][] directions = {
                {1, 1}, {1, 0}, {1, -1}, {0, -1}, {-1, -1}, {-1, 0}, {-1, 1}, {0, 1}
        };

        for (int[] dir : directions) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];

            //only if in bounds
            if (newRow > 0 && newRow <= 8 && newCol > 0 && newCol <= 8) {
                ChessPosition newPos = new ChessPosition(newRow, newCol);
                ChessPiece piece = board.getPiece(newPos);

                //only add if space is empty or an opponent piece
                if (piece == null || piece.getTeamColor() != currColor) {
                    moves.add(new ChessMove(position, newPos, null));
                }
            }
        }
        return moves;
    }
}
