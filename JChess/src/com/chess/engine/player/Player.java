package com.chess.engine.player;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.chess.engine.pieces.King;
import com.chess.engine.pieces.Piece;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class Player {

    protected final Board board;
    protected final King playerKing;
    protected final Collection<Move> legalMoves;
    private final boolean isInCheck;

    Player(final Board board, final Collection<Move> legalMoves, final Collection<Move> opponentMoves, boolean isCastled) {
        this.board = board;
        this.playerKing = establishKing();
        // If there is an enemy piece that attacks the player's king, then that player is in check
        this.isInCheck = !Player.calculateAttacksOnTile(this.playerKing.getPiecePosition(), opponentMoves).isEmpty();
        // Creates a new list to store the legal moves and the castle moves
            List<Move> combined = new ArrayList<>();
            combined.addAll(legalMoves);
            if(!this.isInCheck) {
                combined.addAll(calculateKingCastles(legalMoves, opponentMoves));
            }
        this.legalMoves = Collections.unmodifiableList(combined);
    }
    Player(final Board board, final Collection<Move> legalMoves, final Collection<Move> opponentMoves) {
        this.board = board;
        this.playerKing = establishKing();
        // If there is an enemy piece that attacks the player's king, then that player is in check
        this.isInCheck = !Player.calculateAttacksOnTile(this.playerKing.getPiecePosition(), opponentMoves).isEmpty();
        // Creates a new list to store the legal moves and the castle moves
        List<Move> combined = new ArrayList<>();
        combined.addAll(legalMoves);
        if(!this.isInCheck) {
            combined.addAll(calculateKingCastles(legalMoves, opponentMoves));
        }
        this.legalMoves = Collections.unmodifiableList(combined);
    }

    // Returns the player's king.
    public King getPlayerKing() {
        return this.playerKing;
    }

    // Returns a collection of all the legal moves that the player can make.
    public Collection<Move> getLegalMoves() {
        return this.legalMoves;
    }

    // Calculates and returns a list of moves that are attacking a given piece position
    protected static Collection<Move> calculateAttacksOnTile(int piecePosition, Collection<Move> moves) {
        final List<Move> attackMoves = new ArrayList<>();
        for (Move move : moves) {
            if (move.getDestinationCoordinate() == piecePosition) { //getMove method
                attackMoves.add(move);
            }
        }
        return Collections.unmodifiableList(attackMoves);
    }

    // Returns the player's king. If there is no king on the board, throws a runtime exception.
    private King establishKing(){
        for (final Piece piece: getActivePieces()) {
            if (piece.getPieceType().isKing()) {
                return (King) piece;
            }
        }
        throw new RuntimeException("Invalid board. Needs king.");
    }

    public boolean isMoveLegal(final Move move) {
        return this.legalMoves.contains(move);
    }

    // Returns a boolean representing whether or not the player is in check
    public boolean isInCheck() {
        return this.isInCheck;
    }

    // Returns a boolean representing whether or not the player is in checkmate
    public boolean isInCheckmate() {
        return this.isInCheck && !hasEscapeMoves(); // In check and no all moves lead to being in check
    }

    // Returns a boolean representing whether or not the player is in stalemate
    public boolean isInStalemate() {
        return !this.isInCheck && !hasEscapeMoves(); // Not in check and all moves lead to being in check
    }

    // Returns a boolean representing whether or not the king can escape check
    protected boolean hasEscapeMoves() {
        // For each of the player's legal move, check to see if they can make that move to get out of check. If they
        // can, then the loop breaks and the method returns true.
        for (Move move : this.legalMoves) {
            final MoveTransition transition = makeMove(move); // Make move on new board
            if (transition.getMoveStatus().isDone()) { // Does the move escape check?
                return true;
            }
        }
        return false;
    }

    // Returns a MoveTransition, which represents a player making a given move.
    public MoveTransition makeMove(final Move move) {
        if(!isMoveLegal(move)) {
            // If illegal returns a MoveTransition with an unchanged board.
            return new MoveTransition(this.board, move, MoveStatus.ILLEGAL_MOVE);
        }

        // Otherwise, the move might be legal. So, execute the move and examine the new board.
        final Board transitionBoard = move.execute();

        // Checks to see if new board puts the move making player in check.
        if(transitionBoard.getCurrentPlayer().getOpponent().isInCheck()) {
            return new MoveTransition(this.board, move, MoveStatus.LEAVES_PLAYER_IN_CHECK);
        }

        // If the move is legal, and does not put the player in check then it returns a MoveStatus representing a
        // completed move.
        return new MoveTransition(transitionBoard, move, MoveStatus.DONE);
    }

    public boolean isCastled() {
        return this.playerKing.isCastled();
    }


    public abstract Collection<Piece> getActivePieces();
    public abstract Alliance getAlliance();
    public abstract Player getOpponent();
    protected abstract Collection<Move> calculateKingCastles(Collection<Move> playerLegals,
                                                             Collection<Move> opponentsLegals);
}
