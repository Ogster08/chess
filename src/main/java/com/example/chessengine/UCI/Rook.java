package com.example.chessengine.UCI;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Rook extends Piece {
    private boolean canCastle; //the Board will deal with castling
    private List<Cell> upMovesList;
    private List<Cell> downMovesList;
    private List<Cell> leftMovesList;
    private List<Cell> rightMovesList;

    public Rook(Board board, int row, int col, Colour colour,  boolean canCastle) {
        super(board, row, col, colour);
        this.canCastle = canCastle;
    }

    @Override
    protected List<Cell> TheoreticalReachableCells() {
        List<Cell> cells = new ArrayList<Cell>();
        for (int i = 0; i < 8; i++) {
            if (i != getRow()) {
                cells.add(getBoard().getCell(i, getCol()));
            }
        }
        for (int i = 0; i < 8; i++) {
            if (i != getCol()) {
                cells.add(getBoard().getCell(getRow(), i));
            }
        }
        return cells;
    }

    @Override
    protected void CalculateValidMoves() {
        for (int i = getRow() - 1; i >= 0; i--) {
            if (hitPiece(i, getCol(), leftMovesList)) break;
        }
        for (int i = getRow() + 1; i < 8; i++) {
            if (hitPiece(i, getCol(), rightMovesList)) break;
        }
        for (int i = getCol() - 1; i >= 0; i--) {
            if (hitPiece(getRow(), i, downMovesList)) break;
        }
        for (int i = getRow() + 1; i < 8; i++) {
            if (hitPiece(getRow(), i, upMovesList)) break;
        }
    }

    private boolean hitPiece(int row, int col, List<Cell> cells) {
        Cell cell = getBoard().getCell(row, col);
        if (cell.getPiece() == null){
            cells.add(cell);
            return false;
        } else if (cell.getPiece().getColour() != getColour()) {
            cells.add(cell);
        }
        return true;
    }

    @Override
    protected void ReCalculateValidMoves(int row, int col, Colour oldColour, Colour newColour) {
        Cell cell = getBoard().getCell(row, col);

        if (getRow() - row > 0){
            leftMovesList.clear();
            for (int i = getRow() - 1; i >= 0; i--) {
                if (hitPiece(i, getCol(), leftMovesList)) break;
            }
        }
        if (getRow() - row < 0){
            rightMovesList.clear();
            for (int i = getRow() + 1; i < 8; i++) {
                if (hitPiece(i, getCol(), rightMovesList)) break;
            }
        }
        if (getCol() - col > 0){
            downMovesList.clear();
            for (int i = getCol() - 1; i >= 0; i--) {
                if (hitPiece(getRow(), i, downMovesList)) break;
            }
        }
        if (getCol() - col < 0){
            upMovesList.clear();
            for (int i = getRow() + 1; i < 8; i++) {
                if (hitPiece(getRow(), i, upMovesList)) break;
            }
        }
        movesList.clear();
        movesList.addAll(leftMovesList);
        movesList.addAll(rightMovesList);
        movesList.addAll(downMovesList);
        movesList.addAll(upMovesList);
    }

    @Override
    public void move(int newRow, int newCol) {
        canCastle = false;
        super.move(newRow, newCol);
    }
}
