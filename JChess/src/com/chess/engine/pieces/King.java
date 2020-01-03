package com.chess.engine.pieces;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.chess.engine.board.Tile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.chess.engine.board.Move.*;

public class King extends Piece{
    private final int[] CANDIDATE_MOVE_COORDINATE = {-9, -8, -7, -1, 1, 7, 8, 9};
    private final boolean isCastled;

    public King(final int piecePosition, final Alliance pieceAlliance) {
        super(piecePosition, PieceType.KING, pieceAlliance, true);
        this.isCastled = false;
    }

    public King(final int piecePosition, final Alliance pieceAlliance, final boolean isFirstMove) {
        super(piecePosition, PieceType.KING, pieceAlliance, isFirstMove);
        this.isCastled = false;
    }

    public King(final int piecePosition, final Alliance pieceAlliance,
                final boolean isFirstMove, final boolean isCastled) {
        super(piecePosition, PieceType.KING, pieceAlliance, isFirstMove);
        this.isCastled = isCastled;
    }

    @Override
    public Collection<Move> calculateLegalMoves(Board board) {
        final List<Move> legalMoves = new ArrayList<>();

        for (final int currentCandidateOffset : CANDIDATE_MOVE_COORDINATE) {
            final int candidateDestinationCoordinate = this.piecePosition + currentCandidateOffset;
            if (BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) {
                final Tile candidateDestinationTile = board.getTile(candidateDestinationCoordinate);
                // Checks edge cases
                if (isFirstColumnExclusion(this.piecePosition, currentCandidateOffset) ||
                        isEighthColumnExclusion(this.piecePosition, currentCandidateOffset)) {
                    continue;
                }
                if (!candidateDestinationTile.isTileOccupied()) { // Non-attacking legal move
                    legalMoves.add(new MajorMove(board, this, candidateDestinationCoordinate));
                } else { // Piece occupies candidate destination tile
                    final Piece pieceAtDestination = candidateDestinationTile.getPiece();
                    final Alliance pieceAlliance = pieceAtDestination.getPieceAlliance(); // Attacking move
                    if (this.pieceAlliance != pieceAlliance) {
                        legalMoves.add(new AttackMove(board, this, candidateDestinationCoordinate,
                                pieceAtDestination));
                    }
                }
            }
        }
        return Collections.unmodifiableList(legalMoves);
    }

    @Override
    // Creates and returns a king whose position has been updated by the given move.
    public King movePiece(final Move move) {
        // TODO: Optimize by pre-computing pieces
        if(move.isCastlingMove() || ((King) move.getMovedPiece()).isCastled()) {
            return new King(move.getDestinationCoordinate(), move.getMovedPiece().getPieceAlliance(),
                false, true);
        }
        return new King(move.getDestinationCoordinate(), move.getMovedPiece().getPieceAlliance(), false);
    }

    @Override
    // Returns a character representation of the piece
    public String toString() {
        return PieceType.KING.toString();
    }

    public boolean isCastled() {
        return this.isCastled;
    }

////// EDGE CASE METHODS TO CHECK FOR VALID KING MOVES////////////////////////////////////////////////////////////////
    private static boolean isFirstColumnExclusion(final int currentPosition, final int candidateOffset) {
        return BoardUtils.FIRST_COLUMN[currentPosition] && (candidateOffset == -9 || candidateOffset == -1 ||
                candidateOffset == 7);
    }

    private static boolean isEighthColumnExclusion(final int currentPosition, final int candidateOffset) {
        return BoardUtils.EIGHTH_COLUMN[currentPosition] && (candidateOffset == -7 || candidateOffset == 1 ||
                candidateOffset == 9);
    }
}
