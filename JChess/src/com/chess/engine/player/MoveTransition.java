package com.chess.engine.player;

import com.chess.engine.board.Board;
import com.chess.engine.board.Move;

// Represents the transition of making a move. This includes the transition from one board to another, and any
// information that should be carried over.
public class MoveTransition {

    private final Board transitionBoard;
    private final Move move;
    private final MoveStatus moveStatus;

    public MoveTransition(final Board transitionBoard, final Move move, final MoveStatus moveStatus) {
        this.transitionBoard = transitionBoard;
        this.move = move;
        this.moveStatus = moveStatus;
    }

    // Returns the MoveStatus
    public MoveStatus getMoveStatus() {
        return this.moveStatus;
    }

    // Returns the previous board
    public Board getTransitionBoard() {
        return this.transitionBoard;
    }

    @Override
    // Returns a String representation of the move transition. TEST METHOD!!!
    public String toString() {
        return move.toString() + getMoveStatus().toString();
    }

}
