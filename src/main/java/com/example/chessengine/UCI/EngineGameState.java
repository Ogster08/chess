package com.example.chessengine.UCI;

import com.example.chessengine.Board.Colour;
import com.example.chessengine.Board.Moves.Move;
import com.example.chessengine.Engine.Engine;
import com.example.chessengine.Engine.EngineThread;
import com.example.chessengine.GUI.ChessController;

import java.util.Collections;
import java.util.List;

/**
 * The EngineGameState class is the GameState class for a user playing against the engine. It acts as an interface between the virtual board and GUI.
 */
public class EngineGameState extends GameState{
    /**
     * The EngineThread object, that the engine is in.
     */
    private final EngineThread engineThread;

    /**
     * The Colour that the player is playing as.
     */
    private final Colour playerColour;

    /**
     * Creates a new EngineThread, and starts the new thread.
     * Sets the MoveHandler in the controller to this object, so they can communicate.
     * If the engine is white, then does the next engine move.
     * @param controller The ChessController object for this to communicate with.
     * @param playerColour The Colour that the player is playing as.
     */
    public EngineGameState(ChessController controller, Colour playerColour) {
        super(controller);
        engineThread = new EngineThread(new Engine(board, playerColour == Colour.WHITE ? Colour.BLACK: Colour.WHITE));
        engineThread.start();
        this.playerColour = playerColour;
        if (playerColour == Colour.BLACK){
            doNextEngineMove();
        }
    }

    /**
     * Checks if the player Colour is the same as the Colour to move on the board.
     * @return If it is the player's turn to move.
     */
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
            controller.gameOverMessage(board.getColourToMove() == playerColour ? "Engine wins": "Player wins");
        }
        else {
            controller.gameOverMessage("draw");
        }
    }

    /**
     * Requests the next move from the engine Thread.
     * If the move isn't null, then it performs the move, updates the GUI, and checks if the game is over.
     * If the move is null, then it calls GameEndMessage.
     */
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

    /**
     * Debug function for testing, to count the number of positions at the given depth, in the current position.
     * @param depth The ply to count the number of positions at.
     */
    private void outputCount(int depth){
        engineThread.getCountMoves(System.out::println, depth);
    }

    /**
     * Stops the engine thread running, for when the program shuts, or goes back to the menu.
     */
    public void stopEngineThread(){
        engineThread.stopEngine();
    }
}
