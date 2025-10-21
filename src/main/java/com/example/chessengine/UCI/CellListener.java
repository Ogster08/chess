package com.example.chessengine.UCI;

public interface CellListener {
    void CellChanged(int row, int col, Colour oldColour, Colour newColour);
}
