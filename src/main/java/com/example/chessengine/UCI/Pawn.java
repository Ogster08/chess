package com.example.chessengine.UCI;

import java.util.ArrayList;
import java.util.List;

/**
 * The class for the pawn chess piece
 */
public class Pawn extends Piece {
    /**
     * If the pawn is still on its first rank, allowing it to move 2 squares forward
     */
    boolean firstRank;

    /**
     * The constructor for a pawn piece being added to a chessboard, also checking if the pawn is on its first rank
     * @param board The board the piece is being added to
     * @param row must be between 0 and 7 inclusive
     * @param col must be between 0 and 7 inclusive
     * @param colour The colour of the new piece
     */
    public Pawn(Board board, int row, int col, Colour colour) {
        super(board, row, col, colour);
        firstRank = (colour == Colour.WHITE && row == 1) || (colour == Colour.BLACK && row == 6);
        init();
    }

    /**
     * Calculates all the squares the pawn can move to apart from en passant as that is done by the board
     * @return A list of all the cells on the board that the pawn could reach if the board was empty
     */
    @Override
    protected List<Cell> TheoreticalReachableCells() {
        List<Cell> cells = new ArrayList<Cell>();
        if (getColour() == Colour.WHITE) {
            cells.add(getBoard().getCell(getRow() + 1, getCol()));
            if (firstRank) {cells.add(getBoard().getCell(3, getCol()));}
            if (getCol() > 0) {cells.add(getBoard().getCell(getRow() + 1, getCol() - 1));}
            if (getCol() < 7) {cells.add(getBoard().getCell(getRow() + 1, getCol() + 1));}
        }
        else if (getColour() == Colour.BLACK) {
            cells.add(getBoard().getCell(getRow() - 1, getCol()));
            if (firstRank) {cells.add(getBoard().getCell(4, getCol()));}
            if (getCol() > 0) {cells.add(getBoard().getCell(getRow() - 1, getCol() - 1));}
            if (getCol() < 7) {cells.add(getBoard().getCell(getRow() - 1, getCol() + 1));}
        }
        return cells;
    }

    /**
     * Calculates all the pseudolegal moves in the current position by seeing if there is a piece to take there or the pawn can move there, apart from en passant as the board deals with that
     */
    @Override
    protected void CalculateValidMoves() {
        movesList.clear();
        for (Cell cell : cellsList) {
            Piece piece = cell.getPiece();

            if (Math.abs(getCol() - cell.getCol()) == 1){
                if (piece != null && piece.getColour() !=  getColour()) {
                    movesList.add(cell); // For diagonal captures
                }
            } else if (piece == null) {
                if (!firstRank){
                    movesList.add(cell); // For moving forward 1 square when not on the first rank
                } else {
                    if (Math.abs(getRow() - cell.getRow()) == 1){
                        System.out.println("forward first rank");
                        movesList.add(cell); // For moving forward 1 square when on the first rank
                    }
                    else {
                        if (getColour() == Colour.WHITE){
                            System.out.println("test");
                            if(getBoard().getCell(getRow() + 1, getCol()).getPiece() == null){
                                System.out.println("forward 2 first rank white");
                                movesList.add(cell); // For moving forward 2 squares when on the first rank
                            }
                        }
                        else {
                            if(getBoard().getCell(getRow() - 1, getCol()).getPiece() == null){
                                System.out.println("forward 2 first rank black");
                                movesList.add(cell); // For moving forward 2 squares when on the first rank
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * updates the pseudolegal moves based on the change
     * @param row  must be between 0 and 7 inclusive
     * @param col must be between 0 and 7 inclusive
     * @param oldColour the colour of the old piece
     * @param newColour the colour of the new piece
     */
    @Override
    protected void ReCalculateValidMoves(int row, int col, Colour oldColour, Colour newColour) {
        Cell cell = getBoard().getCell(row, col);
        movesList.remove(cell);
        if (getCol() == col && newColour == null){
            if (!firstRank){
                movesList.add(cell); // For moving forward 1 square while not on the first rank
                return;
            }
            if (Math.abs(getRow() - row) == 1){
                movesList.add(cell); // For moving forward 1 square while on the first rank
                if(getColour() == Colour.WHITE && getBoard().getCell(3, col).getPiece() == null){
                    cellsList.add(getBoard().getCell(3, col)); // For moving forward 2 squares while on the first rank as the square in between is now clear
                } else if (getBoard().getCell(4, col).getPiece() == null) {
                    cellsList.add(getBoard().getCell(4, col)); // For moving forward 2 squares while on the first rank as the square in between is now clear
                }
                return;
            }
            if(getBoard().getCell(getRow() + getRow() - row, col).getPiece() == null){
                movesList.add(cell); // For moving forward 2 squares while on the first rank
            }
        }else if (Math.abs(getCol() - col) == 1 &&
                newColour != getColour() &&
                newColour != null){
            movesList.add(cell); // For capturing diagonally
        }
    }

    /**
     * updates whether the pawn is on the first rank, while the row is updated
     * @param row The new row of the moved piece (0 to 7 inclusive)
     */
    @Override
    protected void setRow(int row) {
        super.setRow(row);
        firstRank = (getColour() == Colour.WHITE && row == 1) || (getColour() == Colour.BLACK && row == 6);
    }
}
