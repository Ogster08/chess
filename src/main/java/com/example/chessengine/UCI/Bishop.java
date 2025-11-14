package com.example.chessengine.UCI;

import java.util.ArrayList;
import java.util.List;

public class Bishop extends Piece {
    private final List<Cell> upLeftMovesList = new ArrayList<>();
    private final List<Cell> upRightMovesList = new ArrayList<>();
    private final List<Cell> downLeftMovesList = new ArrayList<>();
    private final List<Cell> downRightMovesList = new ArrayList<>();

    /**
     * @param board
     * @param row
     * @param col
     * @param colour
     */
    public Bishop(Board board, int row, int col, Colour colour) {
        super(board, row, col, colour);
    }

    /**
     * @return
     */
    @Override
    protected List<Cell> TheoreticalReachableCells() {
        return List.of();
    }

    /**
     *
     */
    @Override
    protected void CalculateValidMoves() {

    }

    /**
     * @param row
     * @param col
     * @param oldColour
     * @param newColour
     */
    @Override
    protected void ReCalculateValidMoves(int row, int col, Colour oldColour, Colour newColour) {

    }
}
