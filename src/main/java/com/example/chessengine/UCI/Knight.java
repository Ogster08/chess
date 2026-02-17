package com.example.chessengine.UCI;

import java.util.ArrayList;
import java.util.List;

public class Knight extends Piece {
    /**
     * The constructor for a new Knight being added to a chessboard
     *
     * @param board  The board the piece is being added to
     * @param row    must be between 0 and 7 inclusive
     * @param col    must be between 0 and 7 inclusive
     * @param colour The colour of the new piece
     */
    public Knight(Board board, int row, int col, Colour colour) {
        super(board, row, col, colour);
        init();
    }

    /**
     * Calculates all the squares the knight can move to
     *
     * @return A list of all the cells on the board that the knight could reach if the board was empty
     */
    @Override
    protected List<Cell> TheoreticalReachableCells() {
        List<Cell> cells = new ArrayList<>();
        if (getCol() + 1 <= 7) {
            if (getRow() + 2 <= 7) cells.add(getBoard().getCell(getRow() + 2, getCol() + 1));
            if (getRow() - 2 >= 0) cells.add(getBoard().getCell(getRow() - 2, getCol() + 1));
            if (getCol() + 2 <= 7) {
                if (getRow() + 1 <= 7) cells.add(getBoard().getCell(getRow() + 1, getCol() + 2));
                if (getRow() - 1 >= 0) cells.add(getBoard().getCell(getRow() - 1, getCol() + 2));
            }
        }
        if (getCol() - 1 >= 0) {
            if (getRow() + 2 <= 7) cells.add(getBoard().getCell(getRow() + 2, getCol() - 1));
            if (getRow() - 2 >= 0) cells.add(getBoard().getCell(getRow() - 2, getCol() - 1));
            if (getCol() - 2 >= 0) {
                if (getRow() + 1 <= 7) cells.add(getBoard().getCell(getRow() + 1, getCol() - 2));
                if (getRow() - 1 >= 0) cells.add(getBoard().getCell(getRow() - 1, getCol() - 2));
            }
        }
        return cells;
    }

    /**
     * Calculates all the pseudolegal moves in the current position by seeing if there isn't a piece of the same colour there
     * All pieces will override this method as they each have a different movement
     */
    @Override
    protected void CalculateValidMoves() {
        movesList.clear();
        for (Cell cell : cellsList) {
            if (cell.getPiece() == null || cell.getPiece().getColour() != getColour()) {
                movesList.add(cell);
            }
        }
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
        Cell cell = getBoard().getCell(row, col);
        movesList.remove(cell);
        if (newColour != getColour()) {
            movesList.add(cell);
        }
    }
}
