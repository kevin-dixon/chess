package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

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
        gameBoard.resetBoard();
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
        Collection<ChessMove> candidateMoves = gameBoard.getPiece(startPosition).pieceMoves(gameBoard, startPosition);
        // initialize empty list of validated moves
        Collection<ChessMove> validatedMoves = new ArrayList<>();
        // get team of position
        TeamColor currColor = gameBoard.getPiece(startPosition).getTeamColor();

        for (ChessMove mov : candidateMoves) {
            // create a deep copy of the board spaces for each move test
            ChessPiece[][] originalSpaces = gameBoard.getSpaces();
            ChessPiece[][] copySpaces = new ChessPiece[originalSpaces.length][originalSpaces[0].length];
            for (int i = 0; i < originalSpaces.length; i++) {
                for (int j = 0; j < originalSpaces[i].length; j++) {
                    copySpaces[i][j] = originalSpaces[i][j] != null ? originalSpaces[i][j] : null;
                }
            }

            // test move on board
            gameBoard.movePiece(mov);
            if (!isInCheck(currColor)) {
                // doesn't leave in check, add to valid moves
                validatedMoves.add(mov);
            }
            // reset board back to how it was
            gameBoard.setSpaces(copySpaces);
        }
        return validatedMoves;
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
        Collection<ChessMove> validMov;

        // only test move if there is a piece and it is current team
        if (gameBoard.getPiece(start) != null) {
            if (gameBoard.getPiece(start).getTeamColor() == currTeam) {
                validMov = validMoves(start);
            } else {
                throw new InvalidMoveException("Not team turn's piece");
            }
        } else {
            throw new InvalidMoveException("No piece");
        }

        // correct team piece is chosen, test if move is in list of valid moves
        boolean foundMove = false;
        for (ChessMove m : validMov) {
            // move is a valid move
            if (Objects.deepEquals(m, move)) {
                //make move
                gameBoard.movePiece(move);
                if (move.getPromotionPiece() != null) {
                    //pawn move promotion; change piece at end to promotion type
                    gameBoard.addPiece(m.getEndPosition(), new ChessPiece(currTeam, m.getPromotionPiece()));
                }
                foundMove = true;
                nextTeam();
            }
        }
        if (!foundMove) { throw new InvalidMoveException("Invalid move"); }
    }

    public void nextTeam() {
        currTeam = (currTeam == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        // get all opponent pieces
        TeamColor oppColor = (teamColor == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
        Collection<ChessPosition> oppTeamPositions = gameBoard.teamPositions(oppColor);

        // Find the king's position first
        ChessPosition kingPosition = null;
        Collection<ChessPosition> teamPositions = gameBoard.teamPositions(teamColor);
        for (ChessPosition pos : teamPositions) {
            ChessPiece piece = gameBoard.getPiece(pos);
            if (piece.getPieceType() == ChessPiece.PieceType.KING) {
                kingPosition = pos;
            }
        }

        // check that no opponent can take king
        for (ChessPosition pos : oppTeamPositions) {
            // get moves of opponent piece
            Collection<ChessMove> oppMoves = gameBoard.getPiece(pos).pieceMoves(gameBoard, pos);

            // check if any moves take the king
            for (ChessMove mov : oppMoves) {
                // if end position is the kings spot, it is in check
                assert kingPosition != null;
                if (Objects.deepEquals(mov.getEndPosition(), kingPosition)) {
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
        // king must be in check
        if (!isInCheck(teamColor)) {
            return false;
        }

        // get all current team positions
        Collection<ChessPosition> teamPos = gameBoard.teamPositions(teamColor);

        // for each team piece, check that after each move the team is not in check
        for (ChessPosition pos : teamPos) {
            Collection<ChessMove> moves = validMoves(pos);
            for (ChessMove mov : moves) {
                // create a deep copy of the board spaces for each move test
                ChessPiece[][] originalSpaces = gameBoard.getSpaces();
                ChessPiece[][] copySpaces = new ChessPiece[originalSpaces.length][originalSpaces[0].length];
                for (int i = 0; i < originalSpaces.length; i++) {
                    for (int j = 0; j < originalSpaces[i].length; j++) {
                        copySpaces[i][j] = originalSpaces[i][j] != null ? originalSpaces[i][j] : null;
                    }
                }

                // test move on board
                gameBoard.movePiece(mov);
                if (!isInCheck(teamColor)) {
                    // doesn't leave in check, at least one move is possible
                    return false;
                }
                // reset board back to how it was
                gameBoard.setSpaces(copySpaces);
            }
        }
        // at this point, no moves are possible to avoid check
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        // king must not be in check
        if (isInCheck(teamColor)) {
            return false;
        }

        // get all current team positions
        Collection<ChessPosition> teamPos = gameBoard.teamPositions(teamColor);

        // for each team piece, check that after each move the team is not in check
        for (ChessPosition pos : teamPos) {
            Collection<ChessMove> moves = validMoves(pos);
            for (ChessMove mov : moves) {
                // create a deep copy of the board spaces for each move test
                ChessPiece[][] originalSpaces = gameBoard.getSpaces();
                ChessPiece[][] copySpaces = new ChessPiece[originalSpaces.length][originalSpaces[0].length];
                for (int i = 0; i < originalSpaces.length; i++) {
                    for (int j = 0; j < originalSpaces[i].length; j++) {
                        copySpaces[i][j] = originalSpaces[i][j] != null ? originalSpaces[i][j] : null;
                    }
                }

                // test move on board
                gameBoard.movePiece(mov);
                if (!isInCheck(teamColor)) {
                    // doesn't leave in check, at least one move is possible
                    return false;
                }
                // reset board back to how it was
                gameBoard.setSpaces(copySpaces);
            }
        }
        // at this point, no moves are possible to avoid check
        return true;
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
