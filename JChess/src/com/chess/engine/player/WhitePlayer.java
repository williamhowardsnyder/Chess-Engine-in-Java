package com.chess.engine.player;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.chess.engine.board.Move.KingSideCastleMove;
import com.chess.engine.board.Move.QueenSideCastleMove;
import com.chess.engine.board.Tile;
import com.chess.engine.pieces.Piece;
import com.chess.engine.pieces.Rook;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class WhitePlayer extends Player {

    public WhitePlayer(final Board board, final Collection<Move> whiteStandardLegalMoves,
                       final Collection<Move> blackStandardLegalMoves) {
        super(board, whiteStandardLegalMoves, blackStandardLegalMoves);
    }

    @Override
    public Collection<Piece> getActivePieces() {
        return this.board.getWhitePieces();
    }

    @Override
    public Alliance getAlliance() {
        return Alliance.WHITE;
    }

    @Override
    public Player getOpponent() {
        return this.board.blackPlayer();
    }

    @Override
    public String toString() {
        return "White";
    }

    @Override
    protected Collection<Move> calculateKingCastles(final Collection<Move> playerLegals,
                                                    final Collection<Move> opponentsLegals) {
        final List<Move> kingCastles = new ArrayList<>();

        if (this.playerKing.isFirstMove() && !this.isInCheck()) { // king's first move and not in check
            // King's side castle
            if (!this.board.getTile(61).isTileOccupied() &&
                !this.board.getTile(62).isTileOccupied()) { // no pieces in the way
                final Tile rookTile = this.board.getTile(63);
                if (rookTile.isTileOccupied() && rookTile.getPiece().isFirstMove()) { // rook's first move as well
                    if (Player.calculateAttacksOnTile(61, opponentsLegals).isEmpty() &&
                        Player.calculateAttacksOnTile(62, opponentsLegals).isEmpty() &&
                        rookTile.getPiece().getPieceType().isRook()) { // king isn't moving through check
                        kingCastles.add(new KingSideCastleMove(this.board, this.playerKing,
                                62, (Rook)rookTile.getPiece(), rookTile.getTileCoordinate(),
                                61));
                    }
                }
            }
            // Queen's side castle
            if (!this.board.getTile(59).isTileOccupied() &&
                !this.board.getTile(58).isTileOccupied() &&
                !this.board.getTile(57).isTileOccupied()) { // no pieces in the way
                final Tile rookTile = this.board.getTile(56);
                if (rookTile.isTileOccupied() && rookTile.getPiece().isFirstMove()) { // rook's first move as well
                    if (Player.calculateAttacksOnTile(59, opponentsLegals).isEmpty() &&
                        Player.calculateAttacksOnTile(58, opponentsLegals).isEmpty() &&
                        Player.calculateAttacksOnTile(57, opponentsLegals).isEmpty() &&
                        rookTile.getPiece().getPieceType().isRook()) { // king isn't moving through check
                        kingCastles.add(new QueenSideCastleMove(this.board, this.playerKing,
                                58, (Rook)rookTile.getPiece(), rookTile.getTileCoordinate(),
                                59));
                    }
                }
            }
        }

        return Collections.unmodifiableCollection(kingCastles);
    }

}
