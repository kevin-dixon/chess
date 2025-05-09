package chess.movecalculators;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class QueenMovesCalc {

    public static Collection<ChessMove> getMoves(ChessBoard board, ChessPosition position){
        Collection<ChessMove> moves = new ArrayList<>();

        int currRow = position.getRow();
        int currCol = position.getColumn();
        ChessGame.TeamColor currColor = board.getPiece(position).getTeamColor();

        // get direction vectors
        int[][] directions = {
                {1,1},{-1,1},{-1,-1},{1,-1},
                {0,1},{0,-1},{1,0},{-1,0}
        };

        // for each direction
        for(int[] dir : directions) {
            for(int i = 1; i < 9; i++) {
                int newRow = currRow + (dir[0] * i);
                int newCol = currCol + (dir[1] * i);

                //validate in bounds
                if (newRow >= 9 || newCol >= 9 || newRow < 1 || newCol < 1) {
                    break;
                }

                ChessPosition newPos = new ChessPosition(newRow, newCol);
                //check for other pieces
                if (board.getPiece(newPos) != null) {
                    //piece in space is opponent
                    if (board.getPiece(newPos).getTeamColor() != currColor) {
                        moves.add(new ChessMove(position, newPos, null));
                    }
                    break;
                } else {
                    //empty spot, add as move
                    moves.add(new ChessMove(position, newPos, null));
                }
            }
        }
        return moves;
    }
}
