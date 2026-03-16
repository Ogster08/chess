package com.example.chessengine.Board.Pieces;

import com.example.chessengine.Board.Board;
import com.example.chessengine.Board.Cell;
import com.example.chessengine.Board.Colour;

import java.util.ArrayList;
import java.util.List;

public abstract class SlidingPiece extends Piece {
    /**
     * A list of every moves list for the sliding piece
     */
    public final List<List<Cell>> movesListsFromDirections = new ArrayList<>();
    /**
     * The constructor for a new sliding piece being added to a chessboard
     *
     * @param board  The board the piece is being added to
     * @param row    must be between 0 and 7 inclusive
     * @param col    must be between 0 and 7 inclusive
     * @param colour The colour of the new piece
     * @param pieceNum The number for the piece type
     */
    public SlidingPiece(Board board, int row, int col, Colour colour, int pieceNum) {
        super(board, row, col, colour, pieceNum);
    }

}
