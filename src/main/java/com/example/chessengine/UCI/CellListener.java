package com.example.chessengine.UCI;

public interface CellListener {
    void CellChanged(int row, int col, Piece oldPiece, Piece newPiece);
}
