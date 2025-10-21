package com.example.chessengine.UCI;

import java.util.ArrayList;
import java.util.List;

public class Cell {
    private final int row, col;
    private Piece piece = null;
    private final List<CellListener> Listener = new ArrayList<>();

    public Cell(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public Piece getPiece() {
        return piece;
    }

    public void setPiece(Piece newPiece){
        Piece  oldPiece = this.piece;
        piece = newPiece;
        Colour oldColour = oldPiece != null ? oldPiece.getColour() : null;
        Colour newColour = newPiece != null ? newPiece.getColour() : null;
        notifyListeners(oldColour, newColour);

    }

    public int getRow() {
        return row;
    }
    public int getCol() {
        return col;
    }

    public void addListener(CellListener l){
        Listener.add(l);
    }

    public void removeListener(CellListener l){
        Listener.remove(l);
    }

    public void notifyListeners(Colour oldColour, Colour newColour) {
        for(CellListener l : Listener){
            l.CellChanged(row, col, oldColour, newColour);
        }
    }
}
