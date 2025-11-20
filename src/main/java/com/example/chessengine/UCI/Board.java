package com.example.chessengine.UCI;

/**
 * The board class will hold the positions of all the pieces, and be able to add, move and remove them.
 * It will also generate all the legal moves in the current position
 */
public class Board{
    /**
     * The array representation of the chessboard, where each cell can contain a piece
     */
    private final Cell[][] cells = new  Cell[8][8];
    private long pieceBitBoard = 0;
    private long whiteBitBoard = 0;
    private long blackBitBoard = 0;

    /**
     * Constructor to create a new empty baord
     */
    public Board() {
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                cells[i][j] = new Cell(i, j);
            }
        }
    }

    /**
     * @param i The row of the cell starting from the left (0 to 7 inclusive)
     * @param j The column of the cell starting from the left (0 to 7 inclusive)
     * @return The cell at that position
     */
    public Cell getCell(int i, int j){
        return cells[i][j];
    }

    /**
     * sets the cell to contain the piece at the pieces position
     * @param p The piece to add to the board (The row and column it goes into should already be set)
     */
    public void addPiece(Piece p){
        int row = p.getRow();
        int col = p.getCol();

        long mask = (1L << (col + 8 * row));
        pieceBitBoard |= mask;

        if (p.getColour() == Colour.WHITE){ whiteBitBoard |= mask; }
        else if (p.getColour() == Colour.BLACK){ blackBitBoard |= mask; }

        cells[row][col].setPiece(p);
    }

    /**
     * @param p the piece to be removed
     */
    public void removePiece(Piece p){
        int row = p.getRow();
        int col = p.getCol();

        long mask = ~(1L << (col + 8 * row));
        pieceBitBoard &= mask;

        if (p.getColour() == Colour.WHITE){ whiteBitBoard &= mask; }
        else if (p.getColour() == Colour.BLACK){ blackBitBoard &= mask; }

        p.removePiece();
        cells[row][col].setPiece(null);
    }

    /**
     * moves a piece from one square to another using its position
     * @param oldRow the row where it used to be (0 to 7 inclusive)
     * @param oldCol the column where it used to be (0 to 7 inclusive)
     * @param newRow the row where it is moving to (0 to 7 inclusive)
     * @param newCol the column where it is moving to(0 to 7 inclusive)
     */
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
