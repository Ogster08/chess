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
            if (firstRank) {cells.add(getBoard().getCell(5, getCol()));}
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
            if (cell.getRow() == getRow() && cell.getPiece() != null){
                    movesList.add(cell);
            } else if (cell.getPiece() != null && cell.getPiece().getColour() != getColour()) {
                movesList.add(cell);
            }
        }
    }

    @Override
    protected void ReCalculateValidMoves(int row, int col, Colour oldColour, Colour newColour) {
        boolean alreadyAMove = movesList.contains(getBoard().getCell(row, col));
        Cell cell = getBoard().getCell(row, col);
        if (getCol() == col){
            if (newColour != getColour() && newColour != null) {
                if (!alreadyAMove) {
                    movesList.add(cell);
                    return;
                }
            }
            if (alreadyAMove){
                movesList.remove(cell);
                return;
            }
        }
        if (newColour == null){
            movesList.add(cell);
            if(firstRank){
                if(getColour() == Colour.WHITE){
                    cellsList.add(getBoard().getCell(3, col));
                }
                cellsList.add(getBoard().getCell(5, col));
            }
            return;
        }
        if (alreadyAMove){
            movesList.remove(cell);
        }
    }
}
