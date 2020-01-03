package com.chess.engine.player.AI;

import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.chess.engine.player.MoveTransition;

// TODO: best moves should be explored first (ie pawn taking piece). Possible solution is to order legal moves
// TODO: by the likelihood that they'll be good.
public class Minimax implements MoveStrategy {
    private final BoardEvaluator boardEvaluator;
    private int searchDepth;

    public Minimax(final int searchDepth) {
        this.boardEvaluator = new StandardBoardEvaluator();
        this.searchDepth = searchDepth;
    }

    @Override
    public String toString() {
        return "AmirMiniMax";
    }

    @Override
    public Move execute(Board board) {
        final long startTime = System.currentTimeMillis();
        Move bestMove = null;
        int greatestSeenValue = Integer.MIN_VALUE;
        int lowestSeenValue = Integer.MAX_VALUE;
        int currValue;

        if(board.getWhitePieces().size() + board.getBlackPieces().size() <= 10) {
            this.searchDepth += 4;
        }

        System.out.println(board.getCurrentPlayer() + " thinking with depth = " + this.searchDepth);
        int numMoves = board.getCurrentPlayer().getLegalMoves().size(); // For test purposes

        for (Move move : board.getCurrentPlayer().getLegalMoves()) {
            final MoveTransition moveTransition = board.getCurrentPlayer().makeMove(move);
            if(moveTransition.getMoveStatus().isDone()) {

                currValue = minimax(moveTransition.getTransitionBoard(), this.searchDepth - 1,
                            Integer.MIN_VALUE, Integer.MAX_VALUE, board.getCurrentPlayer().getAlliance().isWhite());

                if(board.getCurrentPlayer().getAlliance().isWhite() && currValue > greatestSeenValue) {
                    greatestSeenValue = currValue;
                    bestMove = move;
                } else if (board.getCurrentPlayer().getAlliance().isBlack() && currValue < lowestSeenValue) {
                    lowestSeenValue = currValue;
                    bestMove = move;
                }

            }
        }

        final long executionTime = System.currentTimeMillis() - startTime;
        System.out.println("Time taken: " + executionTime);
        return bestMove;
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////// WHERE THE ACTUAL MINIMAX ALGORITHM DOES ITS WORK ///////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // This method generates a tree of positions and propagates the values at the bottom of the tree back to
    // the top in such a way that satisfies the minimax decision algorithm
    private int minimax(final Board board, final int depth, int alpha, int beta, boolean maximizingPlayer) {
        if(depth == 0  || isEndGameScenario(board)) {
            return this.boardEvaluator.evaluate(board, depth);
        }
        if (maximizingPlayer) {
            int highestSeenValue = Integer.MIN_VALUE;
            for(Move move : board.getCurrentPlayer().getLegalMoves()) {
                final MoveTransition moveTransition = board.getCurrentPlayer().makeMove(move);
                if (moveTransition.getMoveStatus().isDone()) {
                    final int currentValue = minimax(moveTransition.getTransitionBoard(),
                                      depth - 1, alpha, beta, false);
                    highestSeenValue = Math.max(highestSeenValue, currentValue);

                    // Pruning occurs
                    alpha = Math.max(alpha, currentValue);
                    if(beta <= alpha) {
                        return currentValue;
                    }
                }
            }
            return highestSeenValue;
        } else { // minimizing player
            int lowestSeenValue = Integer.MAX_VALUE;
            for(Move move : board.getCurrentPlayer().getLegalMoves()) {
                final MoveTransition moveTransition = board.getCurrentPlayer().makeMove(move);
                if(moveTransition.getMoveStatus().isDone()) {
                    final int currentValue = minimax(moveTransition.getTransitionBoard(),
                                   depth - 1, alpha, beta, true);
                    lowestSeenValue = Math.min(lowestSeenValue, currentValue);

                    // Pruning occurs
                    beta = Math.min(beta, currentValue);
                    if(alpha <= beta) {
                        return currentValue;
                    }
                }
            }
            return lowestSeenValue;
        }
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private static boolean isEndGameScenario(Board board) {
        return board.getCurrentPlayer().isInCheckmate() ||
               board.getCurrentPlayer().isInStalemate();
    }
}
