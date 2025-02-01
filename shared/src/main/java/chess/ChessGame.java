package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor currTeam;
    private ChessBoard gameBoard;

    public ChessGame() {
        gameBoard = new ChessBoard();
        currTeam = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return currTeam;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        currTeam = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        //no piece
        if (gameBoard.getPiece(startPosition) == null) {
            return null;
        }

        // get candidate moves before validating
        Collection<ChessMove> candidate_moves = gameBoard.getPiece(startPosition).pieceMoves(gameBoard, startPosition);
        Collection<ChessMove> validated_moves = new ArrayList<>();
        TeamColor currColor = gameBoard.getPiece(startPosition).getTeamColor();

        // check that no moves leave king in check
        for (ChessMove mov : candidate_moves) {
            ChessBoard temp_board = gameBoard;
            // test move
            gameBoard.movePiece(mov);
            if (!isInCheck(currColor)) {
                // doesn't leave in check, add to valid moves
                validated_moves.add(mov);
            }
            // set board back to how it was
            gameBoard = temp_board;
        }
        return validated_moves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        // test move
        ChessPosition start = move.getStartPosition();
        Collection<ChessMove> valid_moves = validMoves(start);
        boolean found_move = false;

        for (ChessMove m : valid_moves) {
            // move is a valid move
            if (m == move) {
                gameBoard.movePiece(move);
                found_move = true;
            }
        }
        if (!found_move) { throw new InvalidMoveException("Invalid move"); }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        // get all opponent pieces
        TeamColor oppColor = TeamColor.WHITE;
        if (teamColor == TeamColor.WHITE) { oppColor = TeamColor.BLACK; }

        Collection<ChessPosition> oppTeamPositions = gameBoard.teamPositions(oppColor);

        // check that no opponent can take king
        for (ChessPosition pos : oppTeamPositions) {
            // get moves of opponent piece
            Collection<ChessMove> oppMoves = gameBoard.getPiece(pos).pieceMoves(gameBoard, pos);

            // check if any moves take the king
            for (ChessMove mov : oppMoves) {
                // get the end position
                ChessPosition end = mov.getEndPosition();
                // check if it is curr team king
                if (gameBoard.getPiece(end) != null) {
                    if ((gameBoard.getPiece(end).getTeamColor() == teamColor) && (gameBoard.getPiece(end).getPieceType() == ChessPiece.PieceType.KING)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        gameBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return gameBoard;
    }
}
