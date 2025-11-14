package com.example.chessengine.UCI;

import java.util.ArrayList;
import java.util.List;

public class Rook extends Piece {
    private boolean canCastle; //the Board will deal with castling
    private final List<Cell> upMovesList = new ArrayList<>();
    private final List<Cell> downMovesList  = new ArrayList<>();
    private final List<Cell> leftMovesList = new ArrayList<>();
    private final List<Cell> rightMovesList  = new ArrayList<>();

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
        upMovesList.clear();
        downMovesList.clear();
        leftMovesList.clear();
        rightMovesList.clear();
        for (int i = getRow() - 1; i >= 0; i--) {
            if (hitPiece(i, getCol(), downMovesList)) break;
        }
        for (int i = getRow() + 1; i < 8; i++) {
            if (hitPiece(i, getCol(), upMovesList)) break;
        }
        for (int i = getCol() - 1; i >= 0; i--) {
            if (hitPiece(getRow(), i, leftMovesList)) break;
        }
        for (int i = getCol() + 1; i < 8; i++) {
            if (hitPiece(getRow(), i, rightMovesList)) break;
        }
        movesList.clear();
        movesList.addAll(leftMovesList);
        movesList.addAll(rightMovesList);
        movesList.addAll(downMovesList);
        movesList.addAll(upMovesList);
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
            downMovesList.clear();
            for (int i = getRow() - 1; i >= 0; i--) {
                if (hitPiece(i, getCol(), downMovesList)) break;
            }
        }
        if (getRow() - row < 0){
            upMovesList.clear();
            for (int i = getRow() + 1; i < 8; i++) {
                if (hitPiece(i, getCol(), upMovesList)) break;
            }
        }
        if (getCol() - col > 0){
            leftMovesList.clear();
            for (int i = getCol() - 1; i >= 0; i--) {
                if (hitPiece(getRow(), i, leftMovesList)) break;
            }
        }
        if (getCol() - col < 0){
            rightMovesList.clear();
            for (int i = getCol() + 1; i < 8; i++) {
                if (hitPiece(getRow(), i, rightMovesList)) break;
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
