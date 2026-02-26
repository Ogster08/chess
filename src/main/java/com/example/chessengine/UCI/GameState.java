package com.example.chessengine.UCI;

import com.example.chessengine.Board.Board;
import com.example.chessengine.Board.Colour;
import com.example.chessengine.Board.Moves.Move;
import com.example.chessengine.Board.Moves.PromotionMove;
import com.example.chessengine.GUI.ChessController;
import com.example.chessengine.GUI.MoveHandler;

import java.util.ArrayList;
import java.util.List;

public class GameState implements MoveHandler {
    protected final Board board;
    protected final ChessController controller;
    protected final List<Move> legalMoves = new ArrayList<>();
    protected boolean gameEnd = false;

    protected void gameEndMessage(){
        gameEnd = true;
        if (board.isInCheck()){
            controller.gameOverMessage(board.getColourToMove() == Colour.WHITE ? "Black wins": "White wins");
        }
        else {
            controller.gameOverMessage("draw");
        }
    }

    public GameState(ChessController controller){
        this.controller = controller;
        controller.setMoveHandler(this);

        board = Board.getStartPosition();
        updateGUI();
    }

    /**
     * @param sourceRow
     * @param sourceColumn
     * @param targetRow
     * @param targetColumn
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

    /**
     * @param row
     * @param col
     * @return
     */
    @Override
    public List<Move> getLegalMoves(int row, int col) {
        List<Move> moves = new ArrayList<>();
        for (Move move: legalMoves){
            if (move.p().getRow() == row && move.p().getCol() == col) moves.add(move);
        }
        return moves;
    }

    public void updateGUI(){
        legalMoves.clear();
        for (Move move: board.getPseudolegalMoves()){
            if (board.checkLegalMoves(move)) {
                legalMoves.add(move);
            }
        }
        controller.updatePosition(board);
    }
}
