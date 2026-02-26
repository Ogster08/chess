package com.example.chessengine.UCI;

import com.example.chessengine.ChessController;

import java.util.ArrayList;
import java.util.List;

public class GameState implements MoveHandler{
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

    private boolean checkLegalMoves(Move move){
        Cell stepOverCell = null;
        if (move.getClass() == CastlingMove.class)stepOverCell = board.getCell(move.cell().getRow(), (move.cell().getCol() == 2) ? 3: 5);

        board.movePiece(move, true);

        Cell kingCell = null;
        boolean breakLoop = false;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Cell cell = board.getCell(i, j);
                if (cell.getPiece() != null && cell.getPiece().getClass() == King.class && cell.getPiece().getColour() != board.getColourToMove()){
                    kingCell = cell;
                    breakLoop = true;
                    break;
                }
            }
            if (breakLoop) break;
        }
        for (Move nextMove: board.getPseudolegalMoves()){
            if (nextMove.cell() == kingCell) {
                board.undoMove();
                return false;
            }
        }
        if (move.getClass() == CastlingMove.class){
            for (Move nextMove: board.getPseudolegalMoves()){
                if (nextMove.cell() == stepOverCell) {
                    board.undoMove();
                    return false;
                }
            }
        }
        board.undoMove();
        return true;
    }

    public void updateGUI(){
        legalMoves.clear();
        for (Move move: board.getPseudolegalMoves()){
            if (checkLegalMoves(move)) {
                legalMoves.add(move);
            }
        }
        controller.updatePosition(board);
    }
}
