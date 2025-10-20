package com.example.chessengine.UCI;

import java.util.List;

public abstract class Piece implements CellListener {
    private final Board board;
    private int row, col;
    private final List<Cell> cellsList;
    private List<Cell> movesList;
    private final Colour colour;

    public Piece(Board board, int row, int col, Colour colour){
        this.board = board;
        this.row = row;
        this.col = col;
        this.colour = colour;
        cellsList = TheoreticalReachableCells();

    }

    public void move(int newRow, int newCol){
        row = newRow;
        col = newCol;

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
    public void CellChanged(int row, int col, Piece oldPiece, Piece newPiece) {
        CalculateValidMoves();
    }

    protected abstract List<Cell> TheoreticalReachableCells();
    protected abstract void CalculateValidMoves();

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
}
