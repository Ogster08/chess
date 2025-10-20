package com.example.chessengine.UCI;

public class Board{
    private final Cell[][] cells = new  Cell[8][8];
    private long pieceBitBoard = 0;
    private long whiteBitBoard = 0;
    private long blackBitBoard = 0;

    public Board() {
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                cells[i][j] = new Cell(i, j);
            }
        }
    }

    public Cell getCell(int i, int j){
        return cells[i][j];
    }

    public void addPiece(Piece p){
        int row = p.getRow();
        int col = p.getCol();

        long mask = (1L << (col + 8 * row));
        pieceBitBoard |= mask;

        if (p.getColour() == Colour.WHITE){ whiteBitBoard |= mask; }
        else if (p.getColour() == Colour.BLACK){ blackBitBoard |= mask; }

        cells[row][col].setPiece(p);
    }

    public void removePiece(Piece p){
        int row = p.getRow();
        int col = p.getCol();

        long mask = ~(1L << (col + 8 * row));
        pieceBitBoard &= mask;

        if (p.getColour() == Colour.WHITE){ whiteBitBoard &= mask; }
        else if (p.getColour() == Colour.BLACK){ blackBitBoard &= mask; }

        cells[row][col].setPiece(null);
    }

    public void movePiece(int oldRow, int oldCol, int newRow, int newCol){
        Piece p = cells[oldRow][oldCol].getPiece();

        long mask = (1L << (newCol + 8 * newCol));
        pieceBitBoard |= mask;

        if (p.getColour() == Colour.WHITE){ whiteBitBoard |= mask; }
        else if (p.getColour() == Colour.BLACK){ blackBitBoard |= mask; }

        cells[newRow][newCol].setPiece(p);

        mask = ~(1L << (oldCol + 8 * oldRow));
        pieceBitBoard &= mask;

        if (p.getColour() == Colour.WHITE){ whiteBitBoard &= mask; }
        else if (p.getColour() == Colour.BLACK){ blackBitBoard &= mask; }

        cells[oldRow][oldCol].setPiece(null);

        p.move(newRow, newCol);
    }
}
