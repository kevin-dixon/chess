package chess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(spaces, that.spaces);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(spaces);
    }

    private final ChessPiece[][] spaces = new ChessPiece[9][9];

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
        for(int i = 8; i > 0; i--){
            for(int j = 8; j > 0; j--) {
                ChessPosition pos = new ChessPosition(i, j);
                //add black pawns
                if(i == 7){
                    addPiece(pos, new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
                }
                //add white pawns
                if(i == 2){
                    addPiece(pos, new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
                }
                //add rooks
                if(i == 1 && (j==1 || j==8)){
                    addPiece(pos, new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));
                }
                if(i == 8 && (j==1 || j==8)){
                    addPiece(pos, new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));
                }
                //add knights
                if(i == 1 && (j==2 || j==7)){
                    addPiece(pos, new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
                }
                if(i == 8 && (j==2 || j==7)){
                    addPiece(pos, new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
                }
                //add bishops
                if(i == 1 && (j==3 || j==6)){
                    addPiece(pos, new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
                }
                if(i == 8 && (j==3 || j==6)){
                    addPiece(pos, new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
                }
                //add queens
                if(i == 1 && j == 4){
                    addPiece(pos, new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN));
                }
                if(i == 8 && j == 4){
                    addPiece(pos, new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN));
                }
                //add kings
                if(i == 1 && j == 5){
                    addPiece(pos, new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING));
                }
                if(i == 8 && j == 5){
                    addPiece(pos, new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING));
                }
            }
        }
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("board: \n");
        for(int i = 8; i > 0; i--) {
            for (int j = 8; j > 0; j--) {
                sb.append("|");
                if(spaces[i][j] == null) {
                    sb.append(" ");
                } else {
                    sb.append(spaces[i][j]);
                }
            }
            sb.append("|\n");
        }
        return sb.toString();
    }

}
