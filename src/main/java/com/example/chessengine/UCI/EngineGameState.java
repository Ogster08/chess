package com.example.chessengine.UCI;

import com.example.chessengine.ChessController;

import java.util.Collections;
import java.util.List;

public class EngineGameState extends GameState{
    private final EngineThread engineThread;
    private final Colour playerColour;

    public EngineGameState(ChessController controller, Colour playerColour) {
        super(controller);
        engineThread = new EngineThread(new Engine(board));
        engineThread.start();
        this.playerColour = playerColour;
        if (playerColour == Colour.BLACK){
            doNextEngineMove();
        }
    }

    private boolean playerTurn(){
        return playerColour == board.getColourToMove();
    }

    @Override
    public boolean handleMove(int sourceRow, int sourceColumn, int targetRow, int targetColumn) {
        if (playerTurn()) {
            if (super.handleMove(sourceRow, sourceColumn, targetRow, targetColumn)){
                doNextEngineMove();
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

    private void doNextEngineMove(){
        engineThread.requestMove(move -> {
            board.movePiece(move);
            updateGUI();
        });
    }

    private void outputCount(int depth){
        engineThread.getCountMoves(System.out::println, depth);
    }

    public void stopEngineThread(){
        engineThread.stopEngine();
    }
}
