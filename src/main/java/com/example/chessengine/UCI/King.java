package com.example.chessengine.UCI;

import java.util.ArrayList;
import java.util.List;

/**
 * The class for the king chess piece
 */
public class King extends Piece {
    /**
     * If the king can still castle to the left
     */
    boolean castleLeft;
    /**
     * If the king can still castle to the right
     */
    boolean castleRight;

    /**
     * The constructor for a new king being added to a chessboard
     * @param board The board the piece is being added to
     * @param row must be between 0 and 7 inclusive
     * @param col must be between 0 and 7 inclusive
     * @param colour The colour of the new piece
     * @param castleLeft If the king can still castle to the left
     * @param castleRight If the king can still castle to the right
     */
    public King(Board board, int row, int col, Colour colour, boolean castleLeft, boolean castleRight) {
        super(board, row, col, colour);
        this.castleLeft = castleLeft;
        this.castleRight = castleRight;
    }

    /**
     * Calculates all the squares the king can move to apart from for castling as that is done by the board
     * @return A list of all the cells on the board that the king could reach if the board was empty
     */
    @Override
    protected List<Cell> TheoreticalReachableCells() {
        List<Cell> cells = new ArrayList<Cell>();
        if (getRow() > 0) cells.add(new Cell(getRow() - 1, getCol()));
        if (getRow() < 7) cells.add(new Cell(getRow() + 1, getCol()));
        if (getCol() > 0) cells.add(new Cell(getRow(), getCol() - 1));
        if (getCol() < 7) cells.add(new Cell(getRow(), getCol() + 1));
        return cells;
    }

    /**
     * Calculates all the pseudolegal moves in the current position by seeing if there isn't a piece of the same colour there
     */
    @Override
    protected void CalculateValidMoves() {
        movesList.clear();
        for (Cell cell : cellsList) {
            if (cell.getPiece() != null && cell.getPiece().getColour() != getColour()) {
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
        castleLeft = false;
        castleRight = false;
        super.move(newRow, newCol);
    }
}
