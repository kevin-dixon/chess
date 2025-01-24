package chess;

import java.util.Arrays;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Arrays.equals(spaces, that.spaces);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(spaces);
    }

    private ChessPiece[][] spaces = new ChessPiece[9][9];

    public ChessBoard() {

    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        spaces[position.getRow()][position.getColumn()] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return spaces[position.getRow()][position.getColumn()];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        for (ChessPiece[] space : spaces) {
            space = null;
        }

        for (int i=8; i > 0; --i) {
            for (int j=8; j > 0; --j) {
                //add black pawns
                if (i == 7) {
                    spaces[i][j] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
                }
                //add white pawns
                if (i == 2) {
                    spaces[i][j] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
                }

                //add black rooks
                if (i == 8 && (j == 8 || j == 1)) {
                    spaces[i][j] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
                }
                //add white rooks
                if (i == 1 && (j == 8 || j == 1)) {
                    spaces[i][j] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
                }

                //add black knights
                if (i == 8 && (j == 7 || j == 2)) {
                    spaces[i][j] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
                }
                //add white knights
                if (i == 1 && (j == 7 || j == 2)) {
                    spaces[i][j] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
                }

                //add black bishops
                if (i == 8 && (j == 6 || j == 3)) {
                    spaces[i][j] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
                }
                //add white bishops
                if (i == 1 && (j == 6 || j == 3)) {
                    spaces[i][j] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
                }

                //add black queen
                if (i == 8 && j == 4) {
                    spaces[i][j] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN);
                }
                //add white queen
                if (i == 1 && j == 4) {
                    spaces[i][j] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN);
                }

                //add black king
                if (i == 8 && j == 5) {
                    spaces[i][j] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING);
                }
                //add white king
                if (i == 1 && j == 5) {
                    spaces[i][j] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING);
                }
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder boardString = new StringBuilder();
        for (int i=8; i > 0; --i) {
            for (int j=8; j > 0; --j) {
                boardString.append("|");
                if (spaces[i][j] == null) {
                    boardString.append(" ");
                } else {
                    boardString.append(spaces[i][j]);
                }
            }
            boardString.append("|\n");
        }
        return boardString.toString();
    }
}
