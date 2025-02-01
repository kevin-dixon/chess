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
    private ChessBoard tempBoard;

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
        // initialize empty list of validated moves
        Collection<ChessMove> validated_moves = new ArrayList<>();
        // get team of position
        TeamColor currColor = gameBoard.getPiece(startPosition).getTeamColor();

        // check that no moves leave king in check
        //for each move, set temp board
        tempBoard.setSpaces(gameBoard.getSpaces());
        for (ChessMove mov : candidate_moves) {
            // test move on board
            gameBoard.movePiece(mov);
            if (!isInCheck(currColor)) {
                // doesn't leave in check, add to valid moves
                validated_moves.add(mov);
            }
            // reset board back to how it was
            gameBoard.setSpaces(tempBoard.getSpaces());
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
        Collection<ChessMove> valid_moves;

        // only test move if there is a piece and it is current team
        if (gameBoard.getPiece(start) != null) {
            if (gameBoard.getPiece(start).getTeamColor() == currTeam) {
                valid_moves = validMoves(start);
            } else {
                throw new InvalidMoveException("Not team turn's piece");
            }
        } else {
            throw new InvalidMoveException("No piece");
        }

        // correct team piece is chosen, test if move is in list of valid moves
        boolean found_move = false;
        for (ChessMove m : valid_moves) {
            // move is a valid move
            if (m == move) {
                gameBoard.movePiece(move);
                found_move = true;
                nextTeam();
            }
        }
        if (!found_move) { throw new InvalidMoveException("Invalid move"); }
    }

    public void nextTeam() {
        if (currTeam == TeamColor.WHITE) {
            currTeam = TeamColor.BLACK;
        }
        else {
            currTeam = TeamColor.WHITE;
        }
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

        // Find the king's position first
        ChessPosition kingPosition = null;
        Collection<ChessPosition> teamPositions = gameBoard.teamPositions(teamColor);
        for (ChessPosition pos : teamPositions) {
            ChessPiece piece = gameBoard.getPiece(pos);
            if (piece.getPieceType() == ChessPiece.PieceType.KING) {
                kingPosition = pos;
                break;
            }
        }

        // check that no opponent can take king
        for (ChessPosition pos : oppTeamPositions) {
            // get moves of opponent piece
            Collection<ChessMove> oppMoves = gameBoard.getPiece(pos).pieceMoves(gameBoard, pos);

            // check if any moves take the king
            for (ChessMove mov : oppMoves) {
                // get the end position
                ChessPosition end = mov.getEndPosition();
                // if end position is the kings spot, it is in check
                if (end == kingPosition) {
                    return true;
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
        TeamColor oppColor = TeamColor.WHITE;
        if (teamColor == TeamColor.WHITE) { oppColor = TeamColor.BLACK; }

        // get all current team positions
        Collection<ChessPosition> team_positions = gameBoard.teamPositions(teamColor);
        Collection<ChessPosition> opp_positions = gameBoard.teamPositions(oppColor);

        // for each team piece, check that after each move the team is not in check
        for (ChessPosition pos : team_positions) {
            Collection<ChessMove> moves = validMoves(pos);
            for (ChessMove mov : moves) {
                ChessBoard temp_board = gameBoard;
                // test move
                gameBoard.movePiece(mov);
                if (!isInCheck(teamColor)) {
                    // doesn't leave in check, at least one move is possible to not be in checkmate
                    return false;
                }
                // set board back to how it was
                gameBoard = temp_board;
            }
        }
        // at this point, no moves are possible to avoid check

        // current team must be in check
        return isInCheck(teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        TeamColor oppColor = TeamColor.WHITE;
        if (teamColor == TeamColor.WHITE) { oppColor = TeamColor.BLACK; }

        // get all current team positions
        Collection<ChessPosition> team_positions = gameBoard.teamPositions(teamColor);
        Collection<ChessPosition> opp_positions = gameBoard.teamPositions(oppColor);

        // for each team piece, check that after each move the team is not in check
        for (ChessPosition pos : team_positions) {
            Collection<ChessMove> moves = validMoves(pos);
            for (ChessMove mov : moves) {
                ChessBoard temp_board = gameBoard;
                // test move
                gameBoard.movePiece(mov);
                if (!isInCheck(teamColor)) {
                    // doesn't leave in check, at least one move is possible to not be in checkmate
                    return false;
                }
                // set board back to how it was
                gameBoard = temp_board;
            }
        }
        // at this point, no moves are possible to avoid check
        return false;
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
