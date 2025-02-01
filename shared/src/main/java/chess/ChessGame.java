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
        // get piece on board
        ChessPiece currPiece = gameBoard.getPiece(startPosition);

        // return null if no piece
        if (gameBoard.getPiece(startPosition) == null) { return null; }

        // check that all moves don't leave king in check
        Collection<ChessMove> moves = currPiece.pieceMoves(gameBoard, startPosition);

        for (ChessMove m : moves) {
            //simulate move and test for check
            ChessBoard test_board = new ChessBoard();
            test_board.resetBoard();
        }

        return moves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {

        ChessPosition king_pos;
        Collection<ChessPosition> opp_pos = new ArrayList<>();

        //scan all spaces
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPiece curr_piece = gameBoard.getPiece(new ChessPosition(i, j));
                if (curr_piece != null) {
                    // get king space
                    if ((curr_piece.getTeamColor() == currTeam) && (curr_piece.getPieceType() == ChessPiece.PieceType.KING)) {
                        king_pos = new ChessPosition(i, j);
                    }
                    // get opponent spaces
                    if (curr_piece.getTeamColor() != currTeam) {
                        opp_pos.add(new ChessPosition(i, j));
                    }
                }
            }
        }

        // go through all moves of all opponent pieces
        for (ChessPosition pos : opp_pos) {
            Collection<ChessMove> curr_pos_moves = gameBoard.getPiece(pos).pieceMoves(gameBoard, pos);
            for (ChessMove mov : curr_pos_moves) {
                ChessPosition curr_end = mov.getEndPosition();
                // if any capture the curr king, return true
                if (gameBoard.getPiece(curr_end) != null) {
                    if ((gameBoard.getPiece(curr_end).getTeamColor() == currTeam) && (gameBoard.getPiece(curr_end).getPieceType() == ChessPiece.PieceType.KING)) {
                        return true;
                    }
                }
            }
            return false;
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
