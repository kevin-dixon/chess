package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KingMovesCalc {

    public static Collection<ChessMove> getMoves(ChessBoard board, ChessPosition position){
        Collection<ChessMove> moves = new ArrayList<>();

        int currRow = position.getRow();
        int currCol = position.getColumn();
        ChessGame.TeamColor currColor = board.getPiece(position).getTeamColor();

        // get direction vectors
        int[][] directions = {
                {1,1},{-1,1},{-1,-1},{1,-1},{0,1},{0,-1},{1,0},{-1,0}
        };

        // for each direction
        for(int[] dir : directions) {
            int newRow = currRow + (dir[0]);
            int newCol = currCol + (dir[1]);

            //check in bounds
            if (newRow < 9 && newCol < 9 && newRow > 0 && newCol > 0) {
                ChessPosition newPos = new ChessPosition(newRow, newCol);
                //check for other pieces
                if (board.getPiece(newPos) != null) {
                    //piece in space is opponent
                    if (board.getPiece(newPos).getTeamColor() != currColor) {
                        moves.add(new ChessMove(position, newPos, null));
                    }
                } else {
                    //empty spot, add as move
                    moves.add(new ChessMove(position, newPos, null));
                }
            }
        }
        return moves;
    }
}
