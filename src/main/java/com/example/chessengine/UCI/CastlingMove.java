package com.example.chessengine.UCI;

public final class CastlingMove extends Move {
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
        super(k, k.getBoard().getCell(k.getRow(), k.getCol() + 2 * Integer.signum(k.getCol() - r.getCol())));
        this.r = r;
        this.rookCell = k.getBoard().getCell(k.getRow(), k.getCol() + Integer.signum(k.getCol() - r.getCol()));
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
     * Converts the object into a string, by listing the rook and king and the cells that they are moving to.
     * @return The string representation of the CastlingMove object
     */
    @Override
    public String toString() {
        return "CastlingMove[]";
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
