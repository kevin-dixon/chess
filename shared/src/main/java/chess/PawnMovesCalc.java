package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMovesCalc {
    public static Collection<ChessMove> getMoves(ChessBoard board, ChessPosition position){
        Collection<ChessMove> moves = new ArrayList<>();

        int currRow = position.getRow();
        int currCol = position.getColumn();
        ChessGame.TeamColor currColor = board.getPiece(position).getTeamColor();

        //get team direction
        int moveDir;
        if(currColor == ChessGame.TeamColor.BLACK) {
            moveDir = -1;
        } else {
            moveDir = 1;
        }

        //check one space ahead
        int newRow = currRow + moveDir;
        if (newRow < 9 && newRow > 0) {
            ChessPosition newPos = new ChessPosition(newRow, currCol);

            if (board.getPiece(newPos) == null) {
                //open space, add to moves
                moves.add(new ChessMove(position, newPos, null));
                //check two space ahead if first move
                if ((currColor == ChessGame.TeamColor.BLACK && currRow==7) || (currColor == ChessGame.TeamColor.WHITE && currRow == 2)){
                    int twoRow = currRow + (moveDir*2);
                    ChessPosition twoPos = new ChessPosition(twoRow, currCol);
                    //two spaces ahead is free, add to moves
                    if (board.getPiece(twoPos) == null) {
                        moves.add(new ChessMove(position, twoPos, null));
                    }
                }
            }

        }


        //check diagonal captures
        // get direction vectors
        int[][] directions = {
                {moveDir,1},{moveDir,-1}
        };

        // for each direction
        for(int[] dir : directions) {
            int capRow = currRow + (dir[0]);
            int capCol = currCol + (dir[1]);
            if (newRow < 9 && capCol < 9 && newRow > 0 && capCol > 0) {
                ChessPosition capPos = new ChessPosition(capRow, capCol);
                //check for other pieces
                if (board.getPiece(capPos) != null) {
                    //piece in space is opponent
                    if (board.getPiece(capPos).getTeamColor() != currColor) {
                        moves.add(new ChessMove(position, capPos, null));
                    }
                }
            }
        }

        //validate all moves for promotions
        Collection<ChessMove> validatedPromotions = new ArrayList<>();

        for (ChessMove move : moves){
            //if black and ends on row 1
            if (move.getEndPosition().getRow() == 1 && currColor == ChessGame.TeamColor.BLACK) {
                //add moves as promotions
                validatedPromotions.add(new ChessMove(position, move.getEndPosition(), ChessPiece.PieceType.QUEEN));
                validatedPromotions.add(new ChessMove(position, move.getEndPosition(), ChessPiece.PieceType.KNIGHT));
                validatedPromotions.add(new ChessMove(position, move.getEndPosition(), ChessPiece.PieceType.ROOK));
                validatedPromotions.add(new ChessMove(position, move.getEndPosition(), ChessPiece.PieceType.BISHOP));
            }
            //if white and ends on row 8
            else if (move.getEndPosition().getRow() == 8 && currColor == ChessGame.TeamColor.WHITE) {
                validatedPromotions.add(new ChessMove(position, move.getEndPosition(), ChessPiece.PieceType.QUEEN));
                validatedPromotions.add(new ChessMove(position, move.getEndPosition(), ChessPiece.PieceType.KNIGHT));
                validatedPromotions.add(new ChessMove(position, move.getEndPosition(), ChessPiece.PieceType.ROOK));
                validatedPromotions.add(new ChessMove(position, move.getEndPosition(), ChessPiece.PieceType.BISHOP));
            } else {
                validatedPromotions.add(move);
            }
        }

        return validatedPromotions;
    }
}
