package com.example.chessengine.UCI;

import java.util.ArrayList;
import java.util.List;

public class King extends Piece {
    boolean castleLeft;
    boolean castleRight;

    public King(Board board, int row, int col, Colour colour, boolean castleLeft, boolean castleRight) {
        super(board, row, col, colour);
        this.castleLeft = castleLeft;
        this.castleRight = castleRight;
    }

    @Override
    protected List<Cell> TheoreticalReachableCells() {
        List<Cell> cells = new ArrayList<Cell>();
        if (getRow() > 0) cells.add(new Cell(getRow() - 1, getCol()));
        if (getRow() < 7) cells.add(new Cell(getRow() + 1, getCol()));
        if (getCol() > 0) cells.add(new Cell(getRow(), getCol() - 1));
        if (getCol() < 7) cells.add(new Cell(getRow(), getCol() + 1));
        return cells;
    }

    @Override
    protected void CalculateValidMoves() {
        for (Cell cell : cellsList) {
            if (cell.getPiece() != null && cell.getPiece().getColour() != getColour()) {
                movesList.add(cell);
            }
        }
    }

    @Override
    protected void ReCalculateValidMoves(int row, int col, Colour oldColour, Colour newColour) {

    }
}
