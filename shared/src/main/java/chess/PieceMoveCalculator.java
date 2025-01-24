package chess;

import java.util.Collection;

public interface PieceMoveCalculator {

    //methods
    Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position);


}
