package com.chess.engine.player.AI;

import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.chess.engine.pieces.Piece;
import com.chess.engine.player.Player;

public final class StandardBoardEvaluator implements BoardEvaluator {

    private static final int CHECK_BONUS = 50;
    private static final int CHECKMATE_BONUS = 100000;
    private static final int DEPTH_BONUS = 10;
    private static final int CASTLE_BONUS = 40;
    private final static int MOBILITY_MULTIPLIER = 2;
    private final static int ATTACK_MULTIPLIER = 2;
    private final static int TWO_BISHOPS_BONUS = 50;

    @Override
    public int evaluate(final Board board, final int depth) {
        return scorePlayer(board, board.whitePlayer(), depth) -
               scorePlayer(board, board.blackPlayer(), depth);
    }

    public int scorePlayer(final Board board, final Player player, final int depth) {
        return pieceValue(player) +
               checkmate(player, depth) +
               check(player) +
                attacks(player) +
               mobility(player) +
               checkmate(player, depth) +
               castle(player) +
               kingSafety(player) +
               pawnStructure(board, player) +
               rookStructure(board, player) +
               knightStructure(player);
    }

    private static int castle(Player player) {
        return player.isCastled() ? CASTLE_BONUS : 0;
    }

    private static int pieceValue(Player player) {
        int score = 0;
        int numBishops = 0;
        for(Piece piece : player.getActivePieces()) {
            score += piece.getPieceValue();
            if(piece.getPieceType().isBishop()) {
                numBishops++;
            }
        }
        if(numBishops >= 2) {
            score += TWO_BISHOPS_BONUS;
        }
        return score;
    }

    private static int checkmate(Player player, int depth) {
        if(player.getOpponent().isInCheckmate()) {
            return depthBonus(depth) * CHECKMATE_BONUS;
        }
        return 0;
    }

    private static int depthBonus(int depth) {
        return depth == 0 ? 1 : DEPTH_BONUS * depth;
    }

    private static int mobility(final Player player) {
        return MOBILITY_MULTIPLIER * mobilityRatio(player);
    }

    private static int mobilityRatio(final Player player) {
        return (int)((player.getLegalMoves().size() * 100.0f) / player.getOpponent().getLegalMoves().size());
    }

    private static int check(Player player) {
        if(player.getOpponent().isInCheck()) {
            return CHECK_BONUS;
        }
        return 0;
    }

    private static int attacks(final Player player) {
        int attackScore = 0;
        for(final Move move : player.getLegalMoves()) {
            if(move.isAttack()) {
                final Piece movedPiece = move.getMovedPiece();
                final Piece attackedPiece = move.getAttackedPiece();
                if(movedPiece.getPieceValue() <= attackedPiece.getPieceValue()) {
                    attackScore++;
                }
            }
        }
        return attackScore * ATTACK_MULTIPLIER;
    }

    // TODO: Flesh out these methods
    private static int pawnStructure(final Board board, final Player player) {
        int score = 0;
        for(Piece piece : player.getActivePieces()) {
            if(piece.getPieceType().isPawn()) {
                if(board.getTile(piece.getPiecePosition() + 8).isTileOccupied()) { // Doubled up pawns are worth half as much
                    Piece pieceOnTile = board.getTile(piece.getPiecePosition() + 8).getPiece();
                    if(pieceOnTile.getPieceType().isPawn() && pieceOnTile.getPieceAlliance() == player.getAlliance()) {
                        score -= 50;
                    }
                }
            }
        }
        return score;
    }
    private static int kingSafety(final Player player) {
        return 0;
    }
    private static int rookStructure(final Board board, final Player player) {
        return 0;
    }
    private static int knightStructure(final Player player) {
        int score = 0;
        for(Piece piece : player.getActivePieces()) {
            if(piece.getPieceType().isKnight()) {
                if(BoardUtils.FIRST_RANK[piece.getPiecePosition()]) {
                    score -= -100;
                }
                if(BoardUtils.EIGHTH_RANK[piece.getPiecePosition()]) {
                    score -=100;
                }
                if(BoardUtils.FIRST_COLUMN[piece.getPiecePosition()]) {
                    score -=100;
                }
                if(BoardUtils.EIGHTH_COLUMN[piece.getPiecePosition()]) {
                    score -=100;
                }
            }
        }
        return score;
    }

}
