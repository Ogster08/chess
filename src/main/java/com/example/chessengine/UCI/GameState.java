package com.example.chessengine.UCI;

import com.example.chessengine.Board.Board;
import com.example.chessengine.Board.Colour;
import com.example.chessengine.Board.Moves.Move;
import com.example.chessengine.Board.Moves.PromotionMove;
import com.example.chessengine.GUI.ChessController;
import com.example.chessengine.GUI.MoveHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * The GameState class is the base class for a standard multiplayer chess game, acting as the interface between the virtual board and GUI.
 */
public class GameState implements MoveHandler {
    /**
     * The Board object, that the chess game is being played on.
     * Starting in the standard start position.
     */
    protected final Board board = Board.getStartPosition();

    /**
     * The ChessController object for the game page, so this object can update it.
     */
    protected final ChessController controller;

    /**
     * A stored list of all the legalMoves in the current position, to prevent unnecessary recalculations.
     */
    protected final List<Move> legalMoves = new ArrayList<>();

    /**
     * If the game has finished, to prevent any moves trying to be made to preserve the board position.
     * Also used to prevent multiple game end messages being sent to the controller.
     */
    protected boolean gameEnd = false;

    /**
     * Constructor to create a new GameState object.
     * Sets the MoveHandler in the controller to this object, so they can communicate.
     * @param controller The ChessController object for this to communicate with.
     */
    public GameState(ChessController controller){
        this.controller = controller;
        controller.setMoveHandler(this);
        updateGUI();
    }

    /**
     * If the move data provided corresponds to a legal move, then the move will be performed on the virtual board.
     * Checks if the game has ended or not, tells the controller if it has.
     * @param sourceRow The row of the piece trying to be moved.
     * @param sourceColumn The column of the piece trying to be moved.
     * @param targetRow The row of the target square of the move.
     * @param targetColumn The column of the target square of the move.
     * @return If the move is legal in the current board position.
     */
    @Override
    public boolean handleMove(int sourceRow, int sourceColumn, int targetRow, int targetColumn) {
        for (Move move: legalMoves){
            if (move.p().getRow() == sourceRow && move.p().getCol() == sourceColumn && move.cell().getRow() == targetRow && move.cell().getCol() == targetColumn){
                if (move.getClass() == PromotionMove.class){
                    Class<?> promotionClass = controller.choosePromotionPiece(board.getColourToMove());
                    if (promotionClass == null) return false;
                    for (Move promotionMove: legalMoves){
                        if (promotionMove.getClass() == PromotionMove.class && ((PromotionMove) promotionMove).promotionClass == promotionClass && promotionMove.cell() == move.cell()) move = promotionMove;
                    }
                }
                board.movePiece(move, false);
                updateGUI();
                if (!gameEnd && (legalMoves.isEmpty() || board.getFiftyMoveCounter() >= 100 || board.positionHistory.containsValue((short) 3))) {
                    gameEndMessage();
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public List<Move> getLegalMoves(int row, int col) {
        List<Move> moves = new ArrayList<>();
        for (Move move: legalMoves){
            if (move.p().getRow() == row && move.p().getCol() == col) moves.add(move);
        }
        return moves;
    }

    /**
     * Updates the GUI, by telling the controller to update the position on the board,
     * and updates the legalMoves list.
     */
    public void updateGUI(){
        legalMoves.clear();
        for (Move move: board.getPseudolegalMoves()){
            if (board.checkLegalMoves(move)) {
                legalMoves.add(move);
            }
        }
        controller.updatePosition(board);
    }

    /**
     * Tells the controller to display the appropriate game over message for the current board position and game type.
     */
    protected void gameEndMessage(){
        gameEnd = true;
        if (board.isInCheck()){
            controller.gameOverMessage(board.getColourToMove() == Colour.WHITE ? "Black wins": "White wins");
        }
        else {
            controller.gameOverMessage("draw");
        }
    }
}
