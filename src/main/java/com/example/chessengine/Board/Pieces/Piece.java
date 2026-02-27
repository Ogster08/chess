package com.example.chessengine.Board.Pieces;

import com.example.chessengine.Board.Board;
import com.example.chessengine.Board.Cell;
import com.example.chessengine.Board.CellListener;
import com.example.chessengine.Board.Colour;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Abstract piece class to define the functionality of every piece.
 */
public abstract class Piece implements CellListener {
    /**
     * The board at which the piece is on
     */
    private final Board board;
    /**
     * The row and column representing the square on the board the piece occupies.
     */
    private int row, col;
    /**
     * The list of square the piece could reach if the board was empty, where it has subscribed as a listener
     */
    protected final List<Cell> cellsList =  new ArrayList<>();

    public List<Cell> getMovesList() {
        return movesList;
    }

    /**
     * The list of all pseudolegal moves the piece can do based on the current position on the board
     */
    protected final List<Cell> movesList = new ArrayList<>();
    /**
     * The colour of the Piece (WHITE or BLACK)
     */
    private final Colour colour;

    /**
     * The constructor for a new piece being added to a chessboard
     * @param board The board the piece is being added to
     * @param row must be between 0 and 7 inclusive
     * @param col must be between 0 and 7 inclusive
     * @param colour The colour of the new piece
     */
    public Piece(Board board, int row, int col, Colour colour){
        this.board = board;
        this.row = row;
        this.col = col;
        this.colour = colour;
    }

    protected void init(){
        for(Cell c : TheoreticalReachableCells()){
            c.addListener(this);
            cellsList.add(c);
        }

        CalculateValidMoves();
    }

    /**
     * moves a piece by recalculating all the theoretically reachable squares and then calculating all the valid moves from the new position
     * @param newRow must be between 0 and 7 inclusive
     * @param newCol must be between 0 and 7 inclusive
     */
    public void move(int newRow, int newCol){
        setRow(newRow);
        setCol(newCol);

        for(Cell c : cellsList){
            c.removeListener(this);
        }
        cellsList.clear();

        for(Cell c : TheoreticalReachableCells()){
            c.addListener(this);
            cellsList.add(c);
        }

        CalculateValidMoves();

    }

    /**
     * When the piece is being removed from the board this is called to remove it as a listener from all the cells it is subscribed to
     */
    public void removePiece(){
        for(Cell c : cellsList){
            c.removeListener(this);
        }
    }

    /**
     * Recalculates the valid moves based on the cell that changed
     * @param row       The row of the changed cell
     * @param col       The column of the changed cell
     * @param oldColour The colour of the piece that used to be in the cell
     * @param newColour The colour of the new pice in the cell
     */
    @Override
    public void CellChanged(int row, int col, Colour oldColour, Colour newColour) {
        ReCalculateValidMoves(row, col, oldColour, newColour);
    }

    /**
     * All pieces will override this method as they each have a different movement
     * @return A list of all the cells on the board that the piece could reach if the board was empty
     */
    protected abstract List<Cell> TheoreticalReachableCells();

    /**
     * Calculates all the pseudolegal moves in the current position
     *All pieces will override this method as they each have a different movement
     */
    protected abstract void CalculateValidMoves();

    /**
     * updates the pseudolegal moves based on the change
     * @param row must be between 0 and 7 inclusive
     * @param col must be between 0 and 7 inclusive
     * @param oldColour the colour of the old piece
     * @param newColour the colour of the new piece
     */
    protected abstract void ReCalculateValidMoves(int row, int col, Colour oldColour, Colour newColour);

    /**
     * @return The row of the cell (between  0 and 8 inclusive)
     */
    public int getRow() {
        return row;
    }

    /**
     * @return The column of the cell (between  0 and 8 inclusive)
     */
    public int getCol() {
        return col;
    }

    /**
     * @return The colour of the piece (WHITE or BLACK)
     */
    public Colour getColour() {
        return colour;
    }

    /**
     * @return The board the piece is on
     */
    public Board getBoard() {
        return board;
    }

    /**
     * @param row The new row of the moved piece (0 to 7 inclusive)
     */
    protected void setRow(int row){
        this.row = row;
    }

    /**
     * @param col The new column of the moved piece (0 to 7 inclusive)
     */
    protected void setCol(int col){
        this.col = col;
    }

    /**
     * @return The string representation of the piece, with its class and location
     */
    @Override
    public String toString(){
        return "Piece " + getClass().getSimpleName() + " at [" + row + ", " + col + "]";
    }

    /**
     * @param o the reference object with which to compare.
     * @return if the Object o is equal to this piece.
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Piece piece = (Piece) o;
        return row == piece.row && col == piece.col && Objects.equals(board, piece.board) && Objects.equals(cellsList, piece.cellsList) && Objects.equals(movesList, piece.movesList) && colour == piece.colour;
    }
}