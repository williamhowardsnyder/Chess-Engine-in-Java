package com.chess.engine.pieces;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;

import java.util.Collection;

public abstract class Piece {

    protected final PieceType pieceType;
    protected final int piecePosition;
    protected final Alliance pieceAlliance;
    protected final boolean isFirstMove;
    private final int cachedHashCode;

    Piece(final int piecePosition, final PieceType pieceType,
          final Alliance pieceAlliance, final boolean isFirstMove) {
        this.pieceType = pieceType;
        this.piecePosition = piecePosition;
        this.pieceAlliance = pieceAlliance;
        this.isFirstMove = isFirstMove;
        this.cachedHashCode = computeHashCode();
    }

    // Computes the unique hash code for this piece
    protected int computeHashCode() {
        int result = pieceType.hashCode();
        result = 31 * result + pieceAlliance.hashCode();
        result = 31 * result + piecePosition;
        result = 31 * result + (isFirstMove ? 1 : 0);
        return result;
    }

    @Override // Want something weaker than reference equality. We want something that returns true if all the fields
    // of the piece are the same.
    // Returns a boolean representing whether or not a piece is equal to another object
    public boolean equals(final Object other) {
        if (this == other) { // Checks to see if other object has same address in memory as this
            return true;
        }
        if (!(other instanceof Piece)) { // Checks to see if other object is a piece
            return false;
        }
        Piece otherPiece = (Piece) other; // If we get to this point we know the other object is a piece
        return pieceType == otherPiece.getPieceType() && piecePosition == otherPiece.getPiecePosition() &&
               pieceAlliance == otherPiece.getPieceAlliance() && isFirstMove == otherPiece.isFirstMove();
    }

    @Override
    // Returns an integer representing the unique hash code of the piece
    public int hashCode() {
        return this.cachedHashCode;
    }

    // Returns the current numerical position (e.g. 0-63) of the piece
    public int getPiecePosition() { return this.piecePosition;}

    // Returns the piece's alliance (white or black)
    public Alliance getPieceAlliance() {
        return this.pieceAlliance;
    }

    // Returns a boolean representing whether or not it is the piece's first move
    public boolean isFirstMove() { return isFirstMove; }

    public PieceType getPieceType() { return this.pieceType;}

    public int getPieceValue() {
        return this.pieceType.getPieceValue();
    }

    // Returns a collection of containing all legal moves on a given board. Each piece will
    // override this based on their unique behavior.
    public abstract Collection<Move> calculateLegalMoves(final Board board);

    // Returns a new piece representing a move (does so by updating the piece's position).
    public abstract Piece movePiece(Move move);

    public enum PieceType {
        PAWN("P", 100) {
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return false;
            }

            @Override
            public boolean isBishop() {
                return false;
            }

            @Override
            public boolean isPawn() {
                return true;
            }

            @Override
            public boolean isKnight() {
                return false;
            }
        },
        KNIGHT("N", 300) {
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return false;
            }

            @Override
            public boolean isBishop() {
                return false;
            }

            @Override
            public boolean isPawn() {
                return false;
            }

            @Override
            public boolean isKnight() {
                return true;
            }
        },
        BISHOP("B", 300) {
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return false;
            }

            @Override
            public boolean isBishop() {
                return true;
            }

            @Override
            public boolean isPawn() {
                return false;
            }

            @Override
            public boolean isKnight() {
                return false;
            }
        },
        ROOK("R", 500) {
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return true;
            }

            @Override
            public boolean isBishop() {
                return false;
            }

            @Override
            public boolean isPawn() {
                return false;
            }

            @Override
            public boolean isKnight() {
                return false;
            }
        },
        QUEEN("Q", 900) {
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return false;
            }

            @Override
            public boolean isBishop() {
                return false;
            }

            @Override
            public boolean isPawn() {
                return false;
            }

            @Override
            public boolean isKnight() {
                return false;
            }
        },
        KING("K", 10000) {
            @Override
            public boolean isKing() {
                return true;
            }

            @Override
            public boolean isRook() {
                return false;
            }

            @Override
            public boolean isBishop() {
                return false;
            }

            @Override
            public boolean isPawn() {
                return false;
            }

            @Override
            public boolean isKnight() {
                return false;
            }
        };

        private String pieceName;
        private int pieceValue;

        PieceType(String pieceName, int pieceValue) {
            this.pieceName = pieceName;
            this.pieceValue = pieceValue;
        }

        @Override
        public String toString() {
            return this.pieceName;
        }

        // returns a int representing the value of the given piece
        public int getPieceValue() {
            return this.pieceValue;
        }

        // returns a boolean representing whether or not the piece is a king
        public abstract boolean isKing();

        // returns a boolean representing whether or not the piece is a rook
        public abstract boolean isRook();

        // returns a boolean representing whether or not the piece is a bishop
        public abstract boolean isBishop();

        // returns a boolean representing whether or not the piece is a pawn
        public abstract boolean isPawn();

        // returns a boolean representing whether or not the piece is a knight
        public abstract boolean isKnight();
    }

}
