package com.example.chessengine.Board.Pieces;

import com.example.chessengine.Board.Board;
import com.example.chessengine.Board.Cell;
import com.example.chessengine.Board.Colour;

import java.util.ArrayList;
import java.util.List;

/**
 * The class for the king chess piece
 */
public class King extends Piece {
    /**
     * If the king can still castle (independent of if the rooks can castle with the king)
     */
    private boolean canCastle;

    /**
     * The constructor for a new king being added to a chessboard
     * @param board The board the piece is being added to
     * @param row must be between 0 and 7 inclusive
     * @param col must be between 0 and 7 inclusive
     * @param colour The colour of the new piece
     * @param castle if the king can castle (independent of the rooks)
     */
    public King(Board board, int row, int col, Colour colour, boolean castle) {
        super(board, row, col, colour);
        this.canCastle = castle;
        init();
    }

    /**
     * Calculates all the squares the king can move to apart from for castling as that is done by the board
     * @return A list of all the cells on the board that the king could reach if the board was empty
     */
    @Override
    protected List<Cell> TheoreticalReachableCells() {
        List<Cell> cells = new ArrayList<Cell>();
        if (getRow() > 0) {
            cells.add(getBoard().getCell(getRow() - 1, getCol()));
            if (getCol() > 0) cells.add(getBoard().getCell(getRow() - 1, getCol() - 1));
            if (getCol() < 7) cells.add(getBoard().getCell(getRow() - 1, getCol() + 1));
        }
        if (getRow() < 7) {
            cells.add(getBoard().getCell(getRow() + 1, getCol()));
            if (getCol() > 0) cells.add(getBoard().getCell(getRow() + 1, getCol() - 1));
            if (getCol() < 7) cells.add(getBoard().getCell(getRow() + 1, getCol() + 1));
        }
        if (getCol() > 0) cells.add(getBoard().getCell(getRow(), getCol() - 1));
        if (getCol() < 7) cells.add(getBoard().getCell(getRow(), getCol() + 1));
        return cells;
    }

    /**
     * Calculates all the pseudolegal moves in the current position by seeing if there isn't a piece of the same colour there
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
     * @param row must be between 0 and 7 inclusive
     * @param col must be between 0 and 7 inclusive
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

    /**
     * moves the king by recalculating all the theoretically reachable squares and then calculating all the valid moves from the new position.
     * prevents any castling after the move
     * @param newRow must be between 0 and 7 inclusive
     * @param newCol must be between 0 and 7 inclusive
     */
    @Override
    public void move(int newRow, int newCol) {
        canCastle = false;
        super.move(newRow, newCol);
    }

    /**
     * moves the king by recalculating all the theoretically reachable squares and then calculating all the valid moves from the new position.
     * also, sets if the king can castle or not.
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

    public void setCanCastle(boolean canCastle) {
        this.canCastle = canCastle;
    }
}
