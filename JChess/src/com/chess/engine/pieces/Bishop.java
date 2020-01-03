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

public class Bishop extends Piece{

    private final static int[] CANDIDATE_MOVE_VECTOR_COORDINATES = {-9, -7, 7, 9};

    public Bishop(final int piecePosition, final Alliance pieceAlliance) {
        super(piecePosition, PieceType.BISHOP, pieceAlliance, true);
    }

    public Bishop(final int piecePosition, final Alliance pieceAlliance, final boolean isFirstMove) {
        super(piecePosition, PieceType.BISHOP, pieceAlliance, isFirstMove );
    }

    @Override
    public Collection<Move> calculateLegalMoves(final Board board) {
        final List<Move> legalMoves = new ArrayList<>();
        // Loops over all the possible movement vectors that a bishop can take, and adds all legal moves to the list of
        // legal moves
        for (final int candidateCoordinateOffset : CANDIDATE_MOVE_VECTOR_COORDINATES) {
            int candidateDestinationCoordinate = this.piecePosition;
            // Loops over all the legal moves for a particular move vector. Ends when the moves have gone off the board
            // or a piece is in the way.
            while(BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) {
                // Checks edge cases
                if (isFirstColumnExclusion(candidateDestinationCoordinate, candidateCoordinateOffset) ||
                        isEighthColumnExclusion(candidateDestinationCoordinate, candidateCoordinateOffset)) {
                    break;
                }
                candidateDestinationCoordinate += candidateCoordinateOffset;
                if (BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) {
                    final Tile candidateDestinationTile = board.getTile(candidateDestinationCoordinate);
                    // Non-attacking legal move
                    if (!candidateDestinationTile.isTileOccupied()) {
                        legalMoves.add(new MajorMove(board, this, candidateDestinationCoordinate));
                    } else { // Piece occupies candidate destination tile
                        final Piece pieceAtDestination = candidateDestinationTile.getPiece();
                        final Alliance pieceAlliance = pieceAtDestination.getPieceAlliance();
                        // Attacking move
                        if (this.pieceAlliance != pieceAlliance) {
                            legalMoves.add(new AttackMove(board, this, candidateDestinationCoordinate,
                                    pieceAtDestination));
                        }
                        break; // exits the loop if the next tile in the movement vector is occupied
                    }
                }
            }
        }
        return Collections.unmodifiableList(legalMoves);
    }

    @Override
    // Creates and returns a bishop whose position has been updated by the given move.
    public Bishop movePiece(final Move move) {
        // TODO: Optimize by pre-computing pieces
        return new Bishop(move.getDestinationCoordinate(), move.getMovedPiece().getPieceAlliance());
    }

    @Override
    // Returns a character representation of the piece
    public String toString() {
        return PieceType.BISHOP.toString();
    }

////EDGE CASES//////////////////////////////////////////////////////////////////////////////////////////////////////////
    private static boolean isFirstColumnExclusion(final int currentPosition, final int candidateOffset) {
        return BoardUtils.FIRST_COLUMN[currentPosition] && (candidateOffset == -9 || candidateOffset == 7);
    }

    private static boolean isEighthColumnExclusion(final int currentPosition, final int candidateOffset) {
        return BoardUtils.EIGHTH_COLUMN[currentPosition] && (candidateOffset == -7 || candidateOffset == 9);
    }
}
