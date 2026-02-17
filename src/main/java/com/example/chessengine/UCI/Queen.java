package com.example.chessengine.UCI;

import java.util.ArrayList;
import java.util.List;

public class Queen extends Piece{
    /**
     * The list of all pseudolegal moves the queen can do based on the current position on the board above itself
     */
    private final List<Cell> upMovesList = new ArrayList<>();
    /**
     * The list of all pseudolegal moves the queen can do based on the current position on the board below itself
     */
    private final List<Cell> downMovesList  = new ArrayList<>();
    /**
     * The list of all pseudolegal moves the queen can do based on the current position on the board to the left of itself
     */
    private final List<Cell> leftMovesList = new ArrayList<>();
    /**
     * The list of all pseudolegal moves the queen can do based on the current position on the board to the right of itself
     */
    private final List<Cell> rightMovesList  = new ArrayList<>();
    /**
     * The list of all pseudolegal moves the queen can do based on the current position on the board diagonally up and left of itself
     */
    private final List<Cell> upLeftMovesList = new ArrayList<>();
    /**
     * The list of all pseudolegal moves the queen can do based on the current position on the board diagonally up and right of itself
     */
    private final List<Cell> upRightMovesList = new ArrayList<>();
    /**
     * The list of all pseudolegal moves the queen can do based on the current position on the board diagonally down and left of itself
     */
    private final List<Cell> downLeftMovesList = new ArrayList<>();
    /**
     * The list of all pseudolegal moves the queen can do based on the current position on the board diagonally down and right of itself
     */
    private final List<Cell> downRightMovesList = new ArrayList<>();

    /**
     * The constructor for a new queen being added to a chessboard
     *
     * @param board  The board the piece is being added to
     * @param row    must be between 0 and 7 inclusive
     * @param col    must be between 0 and 7 inclusive
     * @param colour The colour of the new piece
     */
    public Queen(Board board, int row, int col, Colour colour) {
        super(board, row, col, colour);
        init();
    }

    /**
     * Calculates all the squares the queen could move to if the board was empty
     *
     * @return A list of all the cells on the board that the queen could reach if the board was empty
     */
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
        WalkDiagonal(1,1, cells);
        WalkDiagonal(-1,1, cells);
        WalkDiagonal(1,-1, cells);
        WalkDiagonal(-1,-1, cells);
        return cells;
    }

    protected void WalkDiagonal(int rowD, int colD, List<Cell> cells) {
        int row = getRow() + rowD;
        int col = getCol() + colD;
        while (row <= 7 && col <= 7 && row >= 0 && col >= 0) {
            cells.add(getBoard().getCell(row, col));
            row += rowD;
            col += colD;
        }
    }

    protected void WalkDiagonalUntilHit(int rowD, int colD, List<Cell> cells) {
        int row = getRow() + rowD;
        int col = getCol() + colD;
        while (row <= 7 && col <= 7 && row >= 0 && col >= 0 && !hitPiece(row, col, cells)) {
            cells.add(getBoard().getCell(row, col));
            row += rowD;
            col += colD;
        }
    }

    /**
     * Calculates all the pseudolegal moves in the current position by seeing how far in each direction the queen can move before hitting a piece or reaching the edge of the board
     * All pieces will override this method as they each have a different movement
     */
    @Override
    protected void CalculateValidMoves() {
        upMovesList.clear();
        downMovesList.clear();
        leftMovesList.clear();
        rightMovesList.clear();
        upLeftMovesList.clear();
        upRightMovesList.clear();
        downLeftMovesList.clear();
        downRightMovesList.clear();
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
        WalkDiagonalUntilHit(1,-1, upLeftMovesList);
        WalkDiagonalUntilHit(1,1, upRightMovesList);
        WalkDiagonalUntilHit(-1,-1, downLeftMovesList);
        WalkDiagonalUntilHit(-1,1, downRightMovesList);
        UpdateMovesList();
    }

    /**
     * @param row must be between 0 and 7 inclusive
     * @param col must be between 0 and 7 inclusive
     * @param cells the list of cells for the cell at the position of the row and column to potentially be added to
     * @return a boolean of if the rook has hit a piece at the position specified.
     */
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

    /**
     * updates the pseudolegal moves based on the change
     *
     * @param row       must be between 0 and 7 inclusive
     * @param col       must be between 0 and 7 inclusive
     * @param oldColour the colour of the old piece
     * @param newColour the colour of the new piece
     */
    @Override
    protected void ReCalculateValidMoves(int row, int col, Colour oldColour, Colour newColour) {
        if (getRow() == row || getCol() == col) {
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
        }
        else{
            if (getRow() - row < 0){
                if (getCol() - col > 0){
                    upLeftMovesList.clear();
                    WalkDiagonalUntilHit(1,-1, upLeftMovesList);
                }
                upRightMovesList.clear();
                WalkDiagonalUntilHit(1,1, upRightMovesList);
            }else  if (getCol() - col > 0){
                downLeftMovesList.clear();
                WalkDiagonalUntilHit(-1,-1, downLeftMovesList);
            }
            else {
                downRightMovesList.clear();
                WalkDiagonalUntilHit(-1,1, downRightMovesList);
            }
        }
        UpdateMovesList();
    }

    /**
     * Update the movesList, with the changes to one or more of the arrays
     */
    private void UpdateMovesList() {
        movesList.clear();
        movesList.addAll(leftMovesList);
        movesList.addAll(rightMovesList);
        movesList.addAll(downMovesList);
        movesList.addAll(upMovesList);
        movesList.addAll(upLeftMovesList);
        movesList.addAll(upRightMovesList);
        movesList.addAll(downLeftMovesList);
        movesList.addAll(downRightMovesList);
    }
}
