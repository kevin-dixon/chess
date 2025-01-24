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


    /*public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
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
    }*/
}
