package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class BishopMovesCalc implements PieceMoveCalculator{

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        //get all possible moves for the bishop (ignore other pieces and edges)
        int row = position.getRow();
        int col = position.getColumn();
        Collection<ChessMove> moves = new ArrayList<>();

        ChessGame.TeamColor currColor = board.getPiece(position).getTeamColor();

        boolean addRU = true;
        boolean addRD = true;
        boolean addLU = true;
        boolean addLD = true;

        //get all diagonal directions, stop if occupied
        Collection<ChessPosition> newPositions = new ArrayList<>();
        for (int i = 1; i < 9; i++) {
            //check for out of bounds
            if (row+i > 8) { addRU = false; addRD = false; }
            if (row-i < 1) { addLU = false; addLD = false; }
            if (col+i > 8) { addRU = false; addLU = false; }
            if (col-i < 1) { addRD = false; addLD = false; }

            //check for occupied
            if (board.getPiece(new ChessPosition(row + i, col + i)).getTeamColor() == currColor) {
                addRU = false;
            } else if (board.getPiece(new ChessPosition(row + i, col + i)).getTeamColor() != null) {
                newPositions.add(new ChessPosition(row + i, col + i));
                addRU = false;
            }

            if (board.getPiece(new ChessPosition(row - i, col - i)).getTeamColor() == currColor) {
                addLD = false;
            } else if (board.getPiece(new ChessPosition(row - i, col - i)).getTeamColor() != null) {
                newPositions.add(new ChessPosition(row - i, col - i));
                addLD = false;
            }

            if (board.getPiece(new ChessPosition(row + i, col - i)).getTeamColor() == currColor) {
                addRD = false;
            } else if (board.getPiece(new ChessPosition(row + i, col - i)).getTeamColor() != null) {
                newPositions.add(new ChessPosition(row + i, col - i));
                addRD = false;
            }

            if (board.getPiece(new ChessPosition(row - i, col + i)).getTeamColor() == currColor) {
                addLU = false;
            } else if (board.getPiece(new ChessPosition(row - i, col + i)).getTeamColor() != null) {
                newPositions.add(new ChessPosition(row - i, col + i));
                addLU = false;
            }

            if (addRU) { newPositions.add(new ChessPosition(row + i, col + i)); }
            if (addLD) { newPositions.add(new ChessPosition(row - i, col - i)); }
            if (addRD) { newPositions.add(new ChessPosition(row + i, col - i)); }
            if (addLU) { newPositions.add(new ChessPosition(row - i, col + i)); }
        }

        //add as new moves
        for (ChessPosition p : newPositions) {
            moves.add(new ChessMove(position, p, ChessPiece.PieceType.BISHOP));
        }

        return moves;
    }
}
