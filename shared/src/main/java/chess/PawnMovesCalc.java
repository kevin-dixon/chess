package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMovesCalc implements PieceMoveCalculator {

    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        int row = position.getRow();
        int col = position.getColumn();
        Collection<ChessMove> moves = new ArrayList<>();
        ChessGame.TeamColor currColor = board.getPiece(position).getTeamColor();

        //set team direction
        int teamDirection = 1;
        if (currColor == ChessGame.TeamColor.BLACK) {
            teamDirection = -1;
        }

        //check spaces ahead
        int newRow = row + teamDirection;
        int newCol = col;

        //1 space ahead if empty & in bounds
        if (newRow > 0 && newRow <= 8 && newCol > 0 && newCol <= 8) {
            ChessPosition newPos = new ChessPosition(newRow, newCol);
            ChessPiece piece = board.getPiece(newPos);
            //only add if space is empty
            if (piece == null) {
                moves.add(new ChessMove(position, newPos, null));

                //if first move (white start row 2, black start row 7)
                if (((teamDirection < 0 && position.getRow() == 7)) || ((teamDirection > 0 && position.getRow() == 2))) {
                    // can now check 2 spaces ahead
                    newRow = row + (2*teamDirection);
                    newCol = col;

                    //2 space ahead if empty & in bounds
                    if (newRow > 0 && newRow <= 8 && newCol > 0 && newCol <= 8) {
                        newPos = new ChessPosition(newRow, newCol);
                        piece = board.getPiece(newPos);
                        //only add if space is empty
                        if (piece == null) {
                            moves.add(new ChessMove(position, newPos, null));
                        }
                    }
                }

            }
        }

        //add diagonals if it can capture
        //create list of directions
        int[][] directions = {
                {teamDirection, -1}, {teamDirection, 1}
        };

        for (int[] dir : directions) {
            newRow = row + dir[0];
            newCol = col + dir[1];

            //only if in bounds
            if (newRow > 0 && newRow <= 8 && newCol > 0 && newCol <= 8) {
                ChessPosition newPos = new ChessPosition(newRow, newCol);
                ChessPiece piece = board.getPiece(newPos);

                //only add if space is an opponent piece
                if (piece != null) {
                    if (piece.getTeamColor() != currColor) {
                        moves.add(new ChessMove(position, newPos, null));
                    }
                }
            }
        }

        //check for possible promotions
        Collection<ChessMove> promMoves = new ArrayList<>();

        if(currColor == ChessGame.TeamColor.BLACK) {
            //check for any possible moves into row 1
            for (ChessMove m : moves) {
                if (m.getEndPosition().getRow() == 1) {
                    ChessPosition start = m.getStartPosition();
                    ChessPosition end = m.getEndPosition();

                    promMoves.add(new ChessMove(start, end, ChessPiece.PieceType.QUEEN));
                    promMoves.add(new ChessMove(start, end, ChessPiece.PieceType.BISHOP));
                    promMoves.add(new ChessMove(start, end, ChessPiece.PieceType.ROOK));
                    promMoves.add(new ChessMove(start, end, ChessPiece.PieceType.KNIGHT));
                } else {
                    promMoves.add(m);
                }
            }
        }
        if(currColor == ChessGame.TeamColor.WHITE) {
            //check for any possible moves into row 8
            for (ChessMove m : moves) {
                if (m.getEndPosition().getRow() == 8) {
                    ChessPosition start = m.getStartPosition();
                    ChessPosition end = m.getEndPosition();

                    promMoves.add(new ChessMove(start, end, ChessPiece.PieceType.QUEEN));
                    promMoves.add(new ChessMove(start, end, ChessPiece.PieceType.BISHOP));
                    promMoves.add(new ChessMove(start, end, ChessPiece.PieceType.ROOK));
                    promMoves.add(new ChessMove(start, end, ChessPiece.PieceType.KNIGHT));
                } else {
                    promMoves.add(m);
                }
            }
        }

        return promMoves;
    }
}
