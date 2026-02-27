package com.example.chessengine.Board.Moves;

import com.example.chessengine.Board.Cell;
import com.example.chessengine.Board.Pieces.Piece;

/**
 * Move class, containing the piece being moved and cell it is moving to.
 */
public class Move {
    /**
     * The piece being moved
     */
    private final Piece p;
    /**
     * The cell the piece is moving to
     */
    private final Cell cell;

    /**
     * @param p    The piece making the move
     * @param cell The cell the piece is moving to
     */
    public Move(Piece p, Cell cell) {
        this.p = p;
        this.cell = cell;
    }

    /**
     * @param obj the reference object with which to compare.
     * @return if the Object obj is the same as this move.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj.getClass() != this.getClass()) return false;
        Move m = (Move) obj;
        if (this.p != m.p) return false;
        return this.cell == m.cell;
    }

    /**
     * @return The piece being moved.
     */
    public Piece p() {
        return p;
    }

    /**
     * @return The cell the piece is being moved to.
     */
    public Cell cell() {
        return cell;
    }


    /**
     * Makes a string out of the move object, by listing the piece and cell it is moving to.
     * @return The string representation of the object
     */
    @Override
    public String toString() {
        return "Move[" +
                "p= " + p + ", " +
                "cell= " + cell + ']';
    }
}
