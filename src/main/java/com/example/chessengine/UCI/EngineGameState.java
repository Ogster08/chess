package com.example.chessengine.UCI;

import com.example.chessengine.ChessController;

import java.util.Collections;
import java.util.List;

public class EngineGameState extends GameState{
    private final EngineThread engineThread;
    private final Colour playerColour;

    public EngineGameState(ChessController controller, Colour playerColour) {
        super(controller);
        engineThread = new EngineThread(new Engine(board, playerColour == Colour.WHITE ? Colour.BLACK: Colour.WHITE));
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

    @Override
    protected void gameEndMessage(){
        gameEnd = true;
        if (board.isInCheck()){
            controller.gameOverMessage(board.getColourToMove() == playerColour ? "Player wins": "Engine wins");
        }
        else {
            controller.gameOverMessage("draw");
        }
    }

    private void doNextEngineMove(){
        engineThread.requestMove(move -> {
            if (move != null){
                board.movePiece(move, false);
                updateGUI();
                if ((legalMoves.isEmpty() || board.getFiftyMoveCounter() >= 100 || board.positionHistory.containsValue((short) 3)) && !gameEnd) {
                    gameEndMessage();
                }
            }
            else if (!gameEnd){
                gameEndMessage();
            }
        });
    }

    private void outputCount(int depth){
        engineThread.getCountMoves(System.out::println, depth);
    }

    public void stopEngineThread(){
        engineThread.stopEngine();
    }
}
