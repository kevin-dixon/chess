package chess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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

    private ChessPiece[][] spaces;

    /**
     * new board constructor
     */
    public ChessBoard() {
        spaces = new ChessPiece[9][9];
    }

    /**
     * constructor with given spaces
     */
    public ChessBoard(ChessPiece [][] newSpaces) {
        this.spaces = newSpaces;
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

    public void setSpaces(ChessPiece[][] newSpaces){
        spaces = newSpaces;
    }

    public ChessPiece[][] getSpaces(){
        return spaces;
    }

    public void movePiece(ChessMove move) {
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();

        // only move if a piece is there
        if (spaces[start.getRow()][start.getColumn()] != null) {
            ChessPiece pieceToMove = spaces[start.getRow()][start.getColumn()];
            spaces[end.getRow()][end.getColumn()] = pieceToMove;
            spaces[start.getRow()][start.getColumn()] = null;
        }
    }

    public void swapPiece(ChessMove move) {
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();

        ChessPiece temp = spaces[start.getRow()][start.getColumn()];

        spaces[end.getRow()][end.getColumn()] = spaces[start.getRow()][start.getColumn()];
        spaces[start.getRow()][start.getColumn()] = temp;
    }

    /**
     * Gets the board positions of all pieces on the given team
     * @param color which team to get positions of
     * @return list of positions
     */
    public Collection<ChessPosition> teamPositions(ChessGame.TeamColor color) {
        Collection<ChessPosition> positions = new ArrayList<>();

        for(int i = 8; i > 0; i--) {
            for (int j = 8; j > 0; j--) {
                if (spaces[i][j] != null) {
                    if (spaces[i][j].getTeamColor() == color) {
                        positions.add(new ChessPosition(i, j));
                    }
                }
            }
        }

        return positions;
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
