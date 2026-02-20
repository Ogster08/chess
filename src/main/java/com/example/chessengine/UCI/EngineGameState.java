package com.example.chessengine.UCI;

import com.example.chessengine.ChessController;

import java.util.Collections;
import java.util.List;

public class EngineGameState extends GameState{
    private final Engine engine;
    private final Colour playerColour;

    public EngineGameState(ChessController controller, Colour playerColour) {
        super(controller);
        this.engine = new Engine(board);
        this.playerColour = playerColour;
    }

    private boolean playerTurn(){
        return playerColour == board.getColourToMove();
    }

    @Override
    public boolean handleMove(int sourceRow, int sourceColumn, int targetRow, int targetColumn) {
        if (playerTurn()) {
            if (super.handleMove(sourceRow, sourceColumn, targetRow, targetColumn)){
                board.movePiece(engine.getNextMove());
                updateGUI();
                return true;
            }
        }
        return false;
    }

    @Override
    public List<Move> getLegalMoves(int row, int col) {
        if (playerTurn())  return super.getLegalMoves(row, col);
        return Collections.emptyList();
    }
}
