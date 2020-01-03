package com.tests.chess.engine.board;

import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.chess.engine.player.AI.Minimax;
import com.chess.engine.player.AI.MoveStrategy;
import com.chess.engine.player.MoveTransition;
import org.junit.jupiter.api.Test;

import static com.chess.engine.board.Move.*;
import static org.junit.jupiter.api.Assertions.*;

class TestBoard {

    @Test
    // For each player, tests that they have 20 legal moves, are not in check or checkmate, and have not castled
    public void initialBoard() {
        final Board board = Board.createStandardBoard();
        assertEquals(board.getCurrentPlayer().getLegalMoves().size(), 20);
        assertEquals(board.getCurrentPlayer().getOpponent().getLegalMoves().size(), 20);
        assertFalse(board.getCurrentPlayer().isInCheck());
        assertFalse(board.getCurrentPlayer().isInCheckmate());
        assertFalse(board.getCurrentPlayer().isCastled());
        // assertTrue(board.getCurrentPlayer().isKingSideCastleCapable());
        // assertTrue(board.getCurrentPlayer().isQueenSideCastleCapable());
        assertEquals(board.getCurrentPlayer(), board.whitePlayer());

        assertEquals(board.getCurrentPlayer().getOpponent(), board.blackPlayer());
        assertFalse(board.getCurrentPlayer().getOpponent().isInCheck());
        assertFalse(board.getCurrentPlayer().getOpponent().isInCheckmate());
        assertFalse(board.getCurrentPlayer().getOpponent().isCastled());
        // assertTrue(board.getCurrentPlayer().getOpponent().isKingSideCastleCapable());
        // assertTrue(board.getCurrentPlayer().getOpponent().isQueenSideCastleCapable());
        assertTrue(board.whitePlayer().toString().equals("White"));
        assertTrue(board.blackPlayer().toString().equals("Black"));
        // assertEquals(StandardBoardEvaluator.get().evaluate(board, 0), 0);
    }

    @Test
    public void testFoolsMate() {
        final Board board = Board.createStandardBoard();
        assertTrue(board.getCurrentPlayer().getAlliance().isWhite());

        final MoveTransition t1 = board.getCurrentPlayer()
                .makeMove(MoveFactory.createMove(board, BoardUtils.getCoordinateAtPosition("f2"),
                        BoardUtils.getCoordinateAtPosition("f3")));
        assertTrue(t1.getTransitionBoard().getCurrentPlayer().getAlliance().isBlack());
        assertTrue(t1.getMoveStatus().isDone());

        final MoveTransition t2 = t1.getTransitionBoard()
                .getCurrentPlayer()
                .makeMove(MoveFactory.createMove(t1.getTransitionBoard(), BoardUtils.getCoordinateAtPosition("e7"),
                        BoardUtils.getCoordinateAtPosition("e5")));
        assertTrue(t2.getTransitionBoard().getCurrentPlayer().getAlliance().isWhite());
        assertTrue(t2.getMoveStatus().isDone());

        final MoveTransition t3 = t2.getTransitionBoard()
                .getCurrentPlayer()
                .makeMove(MoveFactory.createMove(t2.getTransitionBoard(), BoardUtils.getCoordinateAtPosition("g2"),
                        BoardUtils.getCoordinateAtPosition("g4")));
        assertTrue(t3.getTransitionBoard().getCurrentPlayer().getAlliance().isBlack());
        assertTrue(t3.getMoveStatus().isDone());

        final MoveStrategy strategy = new Minimax(4);
        final Move aiMove = strategy.execute(t3.getTransitionBoard());
        final Move bestMove = Move.MoveFactory.createMove(t3.getTransitionBoard(), BoardUtils.getCoordinateAtPosition("d8"),
                              BoardUtils.getCoordinateAtPosition("h4"));

        assertEquals(aiMove, bestMove);
    }

}