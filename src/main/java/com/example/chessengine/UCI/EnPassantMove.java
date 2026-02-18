package com.example.chessengine.UCI;

public class EnPassantMove extends Move{
    private final Cell targetPawnCell;

    /**
     * @param p    The piece making the move
     * @param cell The cell the piece is moving to
     */
    public EnPassantMove(Pawn p, Cell cell) {
        super(p, cell);
        targetPawnCell = super.p().getBoard().getCell(super.p().getRow(), super.cell().getCol());
    }

    public Cell getTargetPawnCell(){
        return targetPawnCell;
    }

    /**
     * @return
     */
    @Override
    public String toString(){
        return "Move[" +
                "p= " + p() + ", " +
                "cell= " + cell() + ", " +
                "target pawn cell= " + targetPawnCell +
                ']';
    }
}
