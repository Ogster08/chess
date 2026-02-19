package com.example.chessengine.UCI;

import java.util.ArrayList;
import java.util.List;

/**
 * The class for the rook chess piece
 */
public class Rook extends Piece {
    public void setCanCastle(boolean canCastle) {
        this.canCastle = canCastle;
    }

    /**
     * if the rook is able to castle, used by the board to deal with castling
     */
    private boolean canCastle;
    /**
     * The list of all pseudolegal moves the rook can do based on the current position on the board above itself
     */
    private final List<Cell> upMovesList = new ArrayList<>();
    /**
     * The list of all pseudolegal moves the rook can do based on the current position on the board below itself
     */
    private final List<Cell> downMovesList  = new ArrayList<>();
    /**
     * The list of all pseudolegal moves the rook can do based on the current position on the board to the left of itself
     */
    private final List<Cell> leftMovesList = new ArrayList<>();
    /**
     * The list of all pseudolegal moves the rook can do based on the current position on the board to the right of itself
     */
    private final List<Cell> rightMovesList  = new ArrayList<>();

    /**
     * The constructor for a new rook being added to a chessboard
     * @param board The board the piece is being added to
     * @param row must be between 0 and 7 inclusive
     * @param col must be between 0 and 7 inclusive
     * @param colour The colour of the new piece
     */
    public Rook(Board board, int row, int col, Colour colour,  boolean canCastle) {
        super(board, row, col, colour);
        this.canCastle = canCastle;
        init();
    }

    /**
     * Calculates all the squares the rook could move to if the board was empty
     * @return A list of all the cells on the board that the rook could reach if the board was empty
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
        return cells;
    }

    /**
     *  Calculates all the pseudolegal moves in the current position by seeing how far in each direction the rook can move before hitting a piece or reaching the edge of the board, apart from castling
     */
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
     * @param row must be between 0 and 7 inclusive
     * @param col must be between 0 and 7 inclusive
     * @param oldColour the colour of the old piece
     * @param newColour the colour of the new piece
     */
    @Override
    protected void ReCalculateValidMoves(int row, int col, Colour oldColour, Colour newColour) {
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

    /**
     * moves the rook by recalculating all the theoretically reachable squares and then calculating all the valid moves from the new position.
     * prevents castling with this rook after it moves
     * @param newRow must be between 0 and 7 inclusive
     * @param newCol must be between 0 and 7 inclusive
     */
    @Override
    public void move(int newRow, int newCol) {
        canCastle = false;
        super.move(newRow, newCol);
    }

    /**
     * moves the rook by recalculating all the theoretically reachable squares and then calculating all the valid moves from the new position.
     * also sets if it can castle.
     * @param newRow must be between 0 and 7 inclusive
     * @param newCol must be between 0 and 7 inclusive
     */
    public void move(int newRow, int newCol, boolean canCastle) {
        this.canCastle = canCastle;
        super.move(newRow, newCol);
    }

    public boolean isCanCastle() {
        return canCastle;
    }
}
