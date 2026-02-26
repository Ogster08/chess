package com.example.chessengine.Board;

import com.example.chessengine.Board.Pieces.Piece;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * Cell is the class for each square on a chess board.
 * It can hold a piece, and holds a list of pieces that listen to updates to the cell.
 * It will notify the listeners when a change is made
 */
public class Cell {
    /**
     * The row and column of where the cell is on the chessboard, starting from the bottom left
     */
    private final int row, col;
    /**
     * The piece currently held in the cell
     */
    private Piece piece = null;
    /**
     * A list of all the pieces that listen for changes to the cell
     */
    private final List<CellListener> Listener = new ArrayList<>();

    /**
     * The constructor to create a new cell for a chessboard
     * @param row must be between 0 and 7 inclusive
     * @param col must be between 0 and 7 inclusive
     */
    public Cell(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * @return The current piece in the cell (will be null if there isn't a piece)
     */
    public Piece getPiece() {
        return piece;
    }

    /**
     * Updates the current piece held and then notifies all the pieces listening about the nature of the change, so they can update their valid moves
     * @param newPiece The new piece being moved to the cell (will be null if a piece is moving out of the cell)
     */
    public void setPiece(Piece newPiece){
        Piece  oldPiece = this.piece;
        piece = newPiece;
        if (oldPiece != null) oldPiece.removePiece();
        Colour oldColour = oldPiece != null ? oldPiece.getColour() : null;
        Colour newColour = newPiece != null ? newPiece.getColour() : null;
        notifyListeners(oldColour, newColour);
    }

    /**
     * @return The row of the cell (between  0 and 7 inclusive)
     */
    public int getRow() {
        return row;
    }

    /**
     * @return The column of the cell (between  0 and 7 inclusive)
     */
    public int getCol() {
        return col;
    }

    /**
     * Adds the piece to the list of listeners, so it can be notified of any changes (this will be called when a new piece is made, or it is moved)
     * @param l The piece wanting to be notified of changes
     */
    public void addListener(CellListener l){
        Listener.add(l);
    }

    /**
     * Removes a piece from the list of listeners when it moves or is removed from the board
     * @param l The piece no longer needing to listen to this cell
     */
    public void removeListener(CellListener l){
        Listener.remove(l);
    }

    /**
     * This method calls the CellChanged method from all the listeners, so they can update the valid moves
     * @param oldColour the colour of the piece that used to be in this cell (null if there wasn't one)
     * @param newColour the colour of the new piece in this cell (null if it is now empty)
     */
    public void notifyListeners(Colour oldColour, Colour newColour) {
        for(CellListener l : Listener){
            l.CellChanged(row, col, oldColour, newColour);
        }
    }

    /**
     * Makes a string out of the cell object, with its row and column
     * @return The string representation of the object
     */
    @Override
    public String toString(){
        return "Cell[" + row + ", " + col + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Cell cell = (Cell) o;
        return row == cell.row && col == cell.col && Objects.equals(piece, cell.piece) && Objects.equals(Listener, cell.Listener);
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }
}
