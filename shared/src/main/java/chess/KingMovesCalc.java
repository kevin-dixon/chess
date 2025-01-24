package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KingMovesCalc implements PieceMoveCalculator {
    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        //get all possible moves for the bishop (ignore own pieces and edges)
        int row = position.getRow();
        int col = position.getColumn();
        Collection<ChessMove> moves = new ArrayList<>();

        ChessGame.TeamColor currColor = board.getPiece(position).getTeamColor();

        //check all surrounding spaces, exclude edges and spaces occupied by team pieces
        Collection<ChessPosition> newPositions = new ArrayList<>();

        //check if in of bounds
        if (row + 1 <= 8 && col + 1 <= 8) {
            //if occupied by own team don't add
            if (board.getPiece(new ChessPosition(row + 1, col + 1)) != null) {
                if (board.getPiece(new ChessPosition(row + 1, col + 1)).getTeamColor() != currColor) {
                    newPositions.add(new ChessPosition(row + 1, col + 1));
                }
            } else {
                //space not occupied
                newPositions.add(new ChessPosition(row + 1, col + 1));
            }
        }

        //check if in of bounds
        if (row + 1 <= 8) {
            //if occupied by own team don't add
            if (board.getPiece(new ChessPosition(row + 1, col)) != null) {
                if (board.getPiece(new ChessPosition(row + 1, col)).getTeamColor() != currColor) {
                    newPositions.add(new ChessPosition(row + 1, col));
                }
            } else {
                //space not occupied
                newPositions.add(new ChessPosition(row + 1, col));
            }
        }

        //check if in of bounds
        if (row + 1 <= 8 && col - 1 >= 1) {
            //if occupied by own team don't add
            if (board.getPiece(new ChessPosition(row + 1, col - 1)) != null) {
                if (board.getPiece(new ChessPosition(row + 1, col - 1)).getTeamColor() != currColor) {
                    newPositions.add(new ChessPosition(row + 1, col - 1));
                }
            } else {
                //space not occupied
                newPositions.add(new ChessPosition(row + 1, col - 1));
            }
        }

        //check if in of bounds
        if (col - 1 >= 0) {
            //if occupied by own team don't add
            if (board.getPiece(new ChessPosition(row, col - 1)) != null) {
                if (board.getPiece(new ChessPosition(row, col - 1)).getTeamColor() != currColor) {
                    newPositions.add(new ChessPosition(row, col - 1));
                }
            } else {
                //space not occupied
                newPositions.add(new ChessPosition(row, col - 1));
            }
        }

        //check if in of bounds
        if (row - 1 >= 0 && col - 1 >= 0) {
            //if occupied by own team don't add
            if (board.getPiece(new ChessPosition(row - 1, col - 1)) != null) {
                if (board.getPiece(new ChessPosition(row - 1, col - 1)).getTeamColor() != currColor) {
                    newPositions.add(new ChessPosition(row - 1, col - 1));
                }
            } else {
                //space not occupied
                newPositions.add(new ChessPosition(row - 1, col - 1));
            }
        }

        //check if in of bounds
        if (row - 1 >= 0) {
            //if occupied by own team don't add
            if (board.getPiece(new ChessPosition(row - 1, col)) != null) {
                if (board.getPiece(new ChessPosition(row - 1, col)).getTeamColor() != currColor) {
                    newPositions.add(new ChessPosition(row - 1, col));
                }
            } else {
                //space not occupied
                newPositions.add(new ChessPosition(row - 1, col));
            }
        }

        //check if in of bounds
        if (row - 1 >= 0 && col + 1 <= 8) {
            //if occupied by own team don't add
            if (board.getPiece(new ChessPosition(row - 1, col + 1)) != null) {
                if (board.getPiece(new ChessPosition(row - 1, col + 1)).getTeamColor() != currColor) {
                    newPositions.add(new ChessPosition(row - 1, col + 1));
                }
            } else {
                //space not occupied
                newPositions.add(new ChessPosition(row - 1, col + 1));
            }
        }

        //check if in of bounds
        if (col + 1 <= 8) {
            //if occupied by own team don't add
            if (board.getPiece(new ChessPosition(row, col + 1)) != null) {
                if (board.getPiece(new ChessPosition(row, col + 1)).getTeamColor() != currColor) {
                    newPositions.add(new ChessPosition(row, col + 1));
                }
            } else {
                //space not occupied
                newPositions.add(new ChessPosition(row, col + 1));
            }
        }

        //add as new moves
        for (ChessPosition p : newPositions) {
            moves.add(new ChessMove(position, p, null));
        }

        return moves;
    }
}
