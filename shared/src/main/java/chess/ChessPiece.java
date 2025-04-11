package chess;

import chess.movecalculators.*;

import java.util.Collection;
import java.util.Objects;
import static ui.EscapeSequences.*;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ChessPiece that)) {
            return false;
        }
        return color == that.color && pieceType == that.pieceType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, pieceType);
    }

    public ChessGame.TeamColor color;
    public PieceType pieceType;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        color = pieceColor;
        pieceType = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return color;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return pieceType;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return switch (pieceType) {
            case BISHOP -> BishopMovesCalc.getMoves(board, myPosition);
            case KING -> KingMovesCalc.getMoves(board, myPosition);
            case KNIGHT -> KnightMovesCalc.getMoves(board, myPosition);
            case QUEEN -> QueenMovesCalc.getMoves(board, myPosition);
            case ROOK -> RookMovesCalc.getMoves(board, myPosition);
            case PAWN -> PawnMovesCalc.getMoves(board, myPosition);
            default -> throw new RuntimeException("piece moves not implemented");
        };
    }

    @Override
    public String toString(){
        switch (pieceType) {
            case BISHOP -> {
                if (color == ChessGame.TeamColor.BLACK) {
                    return BLACK_BISHOP;
                } else {
                    return WHITE_BISHOP;
                }
            }
            case KING -> {
                if (color == ChessGame.TeamColor.BLACK) {
                    return BLACK_KING;
                } else {
                    return WHITE_KING;
                }
            }
            case KNIGHT -> {
                if (color == ChessGame.TeamColor.BLACK) {
                    return BLACK_KNIGHT;
                } else {
                    return WHITE_KNIGHT;
                }
            }
            case QUEEN -> {
                if (color == ChessGame.TeamColor.BLACK) {
                    return BLACK_QUEEN;
                } else {
                    return WHITE_QUEEN;
                }
            }
            case ROOK -> {
                if (color == ChessGame.TeamColor.BLACK) {
                    return BLACK_ROOK;
                } else {
                    return WHITE_ROOK;
                }
            }
            case PAWN -> {
                if (color == ChessGame.TeamColor.BLACK) {
                    return BLACK_PAWN;
                } else {
                    return WHITE_PAWN;
                }
            }
            default -> throw new RuntimeException("piece moves not implemented");
        }
    }
}
