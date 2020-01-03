package com.chess.engine.pieces;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.chess.engine.board.Move.*;

public class Pawn extends Piece {

    private final static int[] CANDIDATE_MOVE_COORDINATE = { 7, 8, 9, 16};

    public Pawn(final int piecePosition, final Alliance pieceAlliance) {
        super(piecePosition, PieceType.PAWN, pieceAlliance, true);
    }

    public Pawn(final int piecePosition, final Alliance pieceAlliance, final boolean isFirstMove) {
        super(piecePosition, PieceType.PAWN, pieceAlliance, isFirstMove );
    }

    @Override
    public Collection<Move> calculateLegalMoves(final Board board) {
        final List<Move> legalMoves = new ArrayList<>();
        for(int currentCandidateOffset: CANDIDATE_MOVE_COORDINATE) {
            final int candidateDestinationCoordinate = this.piecePosition +
                      (this.pieceAlliance.getDirection() * currentCandidateOffset);
            if(!BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) { // Illegal move (i.e. off board)
                continue;
            }
            // Normal pawn moves
            if(currentCandidateOffset == 8 && !board.getTile(candidateDestinationCoordinate).isTileOccupied()) {
                if(this.pieceAlliance.isPawnPromotionSquare(candidateDestinationCoordinate)) {
                    legalMoves.add(new PawnPromotion(new PawnMove(board, this, candidateDestinationCoordinate)));
                } else {
                    legalMoves.add(new PawnMove(board, this, candidateDestinationCoordinate));
                }
            } else if(currentCandidateOffset == 16 && this.isFirstMove() && // Checks for first pawn move
                    ((BoardUtils.SEVENTH_RANK[this.piecePosition] && this.getPieceAlliance().isBlack()) ||
                    (BoardUtils.SECOND_RANK[this.piecePosition] && this.getPieceAlliance().isWhite()))) {
                final int behindCandidateDestinationCoordinate = this.piecePosition + (this.pieceAlliance.getDirection() * 8);
                if(!board.getTile(behindCandidateDestinationCoordinate).isTileOccupied() &&
                        !board.getTile(candidateDestinationCoordinate).isTileOccupied()) {
                    legalMoves.add(new PawnJump(board, this, candidateDestinationCoordinate));
                }
            } else if(currentCandidateOffset == 7 && // Checks to see if a diagonal move is valid
                     !(BoardUtils.EIGHTH_COLUMN[this.piecePosition] && this.pieceAlliance.isWhite() ||
                     BoardUtils.FIRST_COLUMN[this.piecePosition] && this.pieceAlliance.isBlack())) {
                if(board.getTile(candidateDestinationCoordinate).isTileOccupied()) {
                    final Piece pieceOnCandidate = board.getTile(candidateDestinationCoordinate).getPiece();
                    if(this.getPieceAlliance() != pieceOnCandidate.getPieceAlliance()) {
                        if(this.pieceAlliance.isPawnPromotionSquare(candidateDestinationCoordinate)) {
                            legalMoves.add(new PawnPromotion(new PawnAttackMove(board, this,
                                    candidateDestinationCoordinate, pieceOnCandidate)));
                        } else {
                            legalMoves.add(new PawnAttackMove(board, this, candidateDestinationCoordinate,
                                    pieceOnCandidate));
                        }
                    }
                } else if(board.getEnPassantPawn() != null) { // There is an En Passant Pawn
                        if(board.getEnPassantPawn().getPiecePosition() ==
                           (this.piecePosition - this.pieceAlliance.getDirection())){ // it is adjacent to the current pawn
                            final Piece pieceOnCandidate = board.getEnPassantPawn();
                            if(this.pieceAlliance != board.getEnPassantPawn().getPieceAlliance()) { // it can be taken
                                legalMoves.add(new PawnEnPassantAttackMove(board, this,
                                               candidateDestinationCoordinate, pieceOnCandidate));
                            }
                    }
                }
            } else if(currentCandidateOffset == 9 && // Checks to see if a diagonal move is valid
                     !(BoardUtils.EIGHTH_COLUMN[this.piecePosition] && this.pieceAlliance.isBlack() ||
                     BoardUtils.FIRST_COLUMN[this.piecePosition] && this.pieceAlliance.isWhite())) {
                if(board.getTile(candidateDestinationCoordinate).isTileOccupied()) {
                    final Piece pieceOnCandidate = board.getTile(candidateDestinationCoordinate).getPiece();
                    if (this.getPieceAlliance() != pieceOnCandidate.getPieceAlliance()) {
                        if(this.pieceAlliance.isPawnPromotionSquare(candidateDestinationCoordinate)) {
                            legalMoves.add(new PawnPromotion(new PawnAttackMove(board, this,
                                    candidateDestinationCoordinate, pieceOnCandidate)));
                        } else {
                            legalMoves.add(new PawnAttackMove(board, this, candidateDestinationCoordinate,
                                    pieceOnCandidate));
                        }
                    }
                } else if(board.getEnPassantPawn() != null) { // There is an En Passant Pawn
                    if(board.getEnPassantPawn().getPiecePosition() ==
                            (this.piecePosition + this.pieceAlliance.getDirection())){ // it is adjacent to the current pawn
                        final Piece pieceOnCandidate = board.getEnPassantPawn();
                        if(this.pieceAlliance != board.getEnPassantPawn().getPieceAlliance()) { // it can be taken
                            legalMoves.add(new PawnEnPassantAttackMove(board, this,
                                    candidateDestinationCoordinate, pieceOnCandidate));
                        }
                    }
                }
            }
        }
        return Collections.unmodifiableList(legalMoves);
    }

    @Override
    // Creates and returns a pawn whose position has been updated by the given move.
    public Pawn movePiece(final Move move) {
        // TODO: Optimize by pre-computing pieces
        return new Pawn(move.getDestinationCoordinate(), move.getMovedPiece().getPieceAlliance());
    }

    @Override
    // Returns a character representation of the piece
    public String toString() {
        return PieceType.PAWN.toString();
    }

    // TODO: Allow for under promotion
    public Piece getPromotionPiece() {
        return new Queen(this.piecePosition, this.pieceAlliance, false);
    }
}
