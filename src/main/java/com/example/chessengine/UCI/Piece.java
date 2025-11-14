package com.example.chessengine.UCI;

import java.util.List;

public abstract class Piece implements CellListener {
    private final Board board;
    private int row, col;
    protected final List<Cell> cellsList;
    protected List<Cell> movesList;
    private final Colour colour;

    public Piece(Board board, int row, int col, Colour colour){
        this.board = board;
        this.row = row;
        this.col = col;
        this.colour = colour;
        cellsList = TheoreticalReachableCells();

    }

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

    @Override
    public void CellChanged(int row, int col, Colour oldColour, Colour newColour) {
        ReCalculateValidMoves(row, col, oldColour, newColour);
    }

    protected abstract List<Cell> TheoreticalReachableCells();
    protected abstract void CalculateValidMoves();
    protected abstract void ReCalculateValidMoves(int row, int col, Colour oldColour, Colour newColour);

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public Colour getColour() {
        return colour;
    }

    public Board getBoard() {
        return board;
    }

    protected void setRow(int row){
        this.row = row;
    }

    protected void setCol(int col){
        this.col = col;
    }

    public List<Cell> getMovesList() {
        return movesList;
    }

    protected void setMovesList(List<Cell> movesList) {
        this.movesList = movesList;
    }
}
