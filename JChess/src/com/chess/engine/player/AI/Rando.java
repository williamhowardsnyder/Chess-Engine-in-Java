package com.chess.engine.player.AI;

import com.chess.engine.board.Board;
import com.chess.engine.board.Move;

import java.util.List;
import java.util.Random;

// Rando represents an AI chess player that makes a random legal move on each of their turns
public class Rando implements MoveStrategy {
    Random r;

    public Rando() {
        r = new Random();
    }

    @Override
    public Move execute(Board board) {
        List<Move> legals = (List) board.getCurrentPlayer().getLegalMoves();
        int selection = r.nextInt(legals.size());
        // TODO: This solution keeps the legal move size very large
        // Might not be convenient for higher complexity AI
        while(!board.getCurrentPlayer().makeMove(legals.get(selection)).getMoveStatus().isDone()) {
            selection = r.nextInt(legals.size());
        }
        return legals.get(selection);
    }
}
