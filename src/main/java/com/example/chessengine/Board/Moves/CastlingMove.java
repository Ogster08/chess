package com.example.chessengine.Board.Moves;

import com.example.chessengine.Board.Cell;
import com.example.chessengine.Board.Pieces.King;
import com.example.chessengine.Board.Pieces.Rook;

/**
 * The CastlingMove class, extending the move class.
 * Contains the king being moved and the cell it is being moved to
 * Contains the rook and cell it is being moved to.
 */
public class CastlingMove extends Move {
    /**
     * The rook being moved
     */
    private final Rook r;
    /**
     * The cell the rook is being moved to.
     */
    private final Cell rookCell;

    /**
     * @param k The king piece being moved
     * @param r The rook piece being moved
     */
    public CastlingMove(King k, Rook r) {
        super(k, k.getBoard().getCell(k.getRow(), k.getCol() + 2 * Integer.signum(r.getCol() - k.getCol())));
        this.r = r;
        this.rookCell = k.getBoard().getCell(k.getRow(), k.getCol() + Integer.signum(r.getCol() - k.getCol()));
    }

    /**
     * @param obj the reference object with which to compare.
     * @return {@code true} if this record is equal to the
     * argument; {@code false} otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        return obj == this || obj != null && obj.getClass() == this.getClass();
    }

    /**
     * @return The string representation of the CastlingMove object, with the king, cell the king is moving to, the rook and the cell the rook is moving to.
     */
    @Override
    public String toString() {
        return "CastlingMove[" +
                "k= " + p() + ", " +
                "k cell= " + cell() + ", " +
                "r = " + r + ", " +
                "r cell = " + rookCell +
                "]";
    }

    /**
     * @return The cell the rook is moving to
     */
    public Cell getRookCell() {
        return rookCell;
    }

    /**
     * @return The rook piece being moved.
     */
    public Rook getR() {
        return r;
    }
}
