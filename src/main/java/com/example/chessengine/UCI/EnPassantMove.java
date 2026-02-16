package com.example.chessengine.UCI;

public class EnPassantMove extends Move{
    /**
     * @param p    The piece making the move
     * @param cell The cell the piece is moving to
     */
    public EnPassantMove(Pawn p, Cell cell) {
        super(p, cell);
    }

    public Cell targetPawnCell(){
        return super.p().getBoard().getCell(super.cell().getRow(), super.p().getCol());
    }
}
