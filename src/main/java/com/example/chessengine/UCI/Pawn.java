package com.example.chessengine.UCI;

import java.util.ArrayList;
import java.util.List;

public class Pawn extends Piece {
    boolean firstRank = false;

    public Pawn(Board board, int row, int col, Colour colour) {
        super(board, row, col, colour);
        if (colour == Colour.WHITE) {
            if (row == 1) {
                firstRank = true;
            }
        }
        else if (colour == Colour.BLACK) {
            if (row == 7) {
                firstRank = true;
            }
        }
    }

    @Override
    protected List<Cell> TheoreticalReachableCells() {
        List<Cell> cells = new ArrayList<Cell>();
        if (getColour() == Colour.WHITE) {
            cells.add(getBoard().getCell(getRow() + 1, getCol()));
            if (firstRank) {cells.add(getBoard().getCell(getRow() + 2, getCol()));}
            if (getCol() > 0) {cells.add(getBoard().getCell(getRow() + 1, getCol() - 1));}
            if (getCol() < 7) {cells.add(getBoard().getCell(getRow() + 1, getCol() + 1));}
        }
        else if (getColour() == Colour.BLACK) {
            cells.add(getBoard().getCell(getRow() - 1, getCol()));
            if (firstRank) {cells.add(getBoard().getCell(getRow() - 2, getCol()));}
            if (getCol() > 0) {cells.add(getBoard().getCell(getRow() - 1, getCol() - 1));}
            if (getCol() < 7) {cells.add(getBoard().getCell(getRow() - 1, getCol() + 1));}
        }
        return cells;
    }

    @Override
    protected void CalculateValidMoves() {
        for (Cell cell : cell) {

        }
    }
}
