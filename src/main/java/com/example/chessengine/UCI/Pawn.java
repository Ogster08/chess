package com.example.chessengine.UCI;

import java.util.ArrayList;
import java.util.List;

public class Pawn extends Piece {
    boolean firstRank;

    public Pawn(Board board, int row, int col, Colour colour) {
        super(board, row, col, colour);
        firstRank = (getColour() == Colour.WHITE && row == 1) || (getColour() == Colour.BLACK && row == 6);
    }

    @Override
    protected List<Cell> TheoreticalReachableCells() {
        List<Cell> cells = new ArrayList<Cell>();
        if (getColour() == Colour.WHITE) {
            cells.add(getBoard().getCell(getRow() + 1, getCol()));
            if (firstRank) {cells.add(getBoard().getCell(3, getCol()));}
            if (getCol() > 0) {cells.add(getBoard().getCell(getRow() + 1, getCol() - 1));}
            if (getCol() < 7) {cells.add(getBoard().getCell(getRow() + 1, getCol() + 1));}
/*
            if (getRow() == 3) {
                cells.add(getBoard().getCell(getRow(), getCol() - 1));
                cells.add(getBoard().getCell(getRow(), getCol() + 1));
            }
*/
        }
        else if (getColour() == Colour.BLACK) {
            cells.add(getBoard().getCell(getRow() - 1, getCol()));
            if (firstRank) {cells.add(getBoard().getCell(4, getCol()));}
            if (getCol() > 0) {cells.add(getBoard().getCell(getRow() - 1, getCol() - 1));}
            if (getCol() < 7) {cells.add(getBoard().getCell(getRow() - 1, getCol() + 1));}
/*
            if (getRow() == 7) {
                cells.add(getBoard().getCell(getRow(), getCol() - 1));
                cells.add(getBoard().getCell(getRow(), getCol() + 1));
            }
*/
        }
        return cells;
    }

    @Override
    protected void CalculateValidMoves() {
        for (Cell cell : cellsList) {
            Piece piece = cell.getPiece();

            if (cell.getCol() == getCol() && cell.getPiece() != null){
                    movesList.add(cell);
            } else if (piece != null && piece.getColour() != getColour()) {
                movesList.add(cell);
            }
        }
    }

    @Override
    protected void ReCalculateValidMoves(int row, int col, Colour oldColour, Colour newColour) {
        Cell cell = getBoard().getCell(row, col);
        movesList.remove(cell);
        if (getCol() == col && newColour == null){
            movesList.add(cell);
            if(firstRank){
                if(getColour() == Colour.WHITE){
                    cellsList.add(getBoard().getCell(3, col));
                    return;
                }
                cellsList.add(getBoard().getCell(4, col));
            }
        }else if (Math.abs(getCol() - col) == 1 &&
                newColour != getColour() &&
                newColour != null){
            movesList.add(cell);
        }
    }

    @Override
    protected void setRow(int row) {
        super.setRow(row);
        firstRank = (getColour() == Colour.WHITE && row == 1) || (getColour() == Colour.BLACK && row == 7);
    }
}
