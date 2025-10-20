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
        notifyListeners(piece, newPiece);
        piece = newPiece;
    }

    public void addListener(CellListener l){
        Listener.add(l);
    }

    public void removeListener(CellListener l){
        Listener.remove(l);
    }

    public void notifyListeners(Piece oldPiece, Piece newPiece) {
        for(CellListener l : Listener){
            l.CellChanged(row, col, oldPiece, newPiece);
        }
    }
}
