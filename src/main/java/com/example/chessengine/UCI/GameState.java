package com.example.chessengine.UCI;

import com.example.chessengine.HelloController;

import java.util.ArrayList;
import java.util.List;

public class GameState implements MoveHandler{
    private final Board board;
    private final HelloController controller;
    private final List<Move> legalMoves = new ArrayList<>();

    public GameState(HelloController controller){
        this.controller = controller;
        controller.setMoveHandler(this);

        board = new Board();
        for (int col = 0; col < 8; col++) {
            board.addPiece(new Pawn(board, 1, col, Colour.WHITE));
        }
        board.addPiece(new Rook(board, 0, 0, Colour.WHITE, true));
        board.addPiece(new Rook(board, 0, 7, Colour.WHITE, true));

        board.addPiece(new Knight(board, 0, 1, Colour.WHITE));
        board.addPiece(new Knight(board, 0, 6, Colour.WHITE));

        board.addPiece(new Bishop(board, 0, 2, Colour.WHITE));
        board.addPiece(new Bishop(board, 0, 5, Colour.WHITE));

        board.addPiece(new Queen(board, 0, 3, Colour.WHITE));
        board.addPiece(new King(board, 0, 4, Colour.WHITE, true));

        for (int col = 0; col < 8; col++) {
            board.addPiece(new Pawn(board, 6, col, Colour.BLACK));
        }
        board.addPiece(new Rook(board, 7, 0, Colour.BLACK, true));
        board.addPiece(new Rook(board, 7, 7, Colour.BLACK, true));

        board.addPiece(new Knight(board, 7, 1, Colour.BLACK));
        board.addPiece(new Knight(board, 7, 6, Colour.BLACK));

        board.addPiece(new Bishop(board, 7, 2, Colour.BLACK));
        board.addPiece(new Bishop(board, 7, 5, Colour.BLACK));

        board.addPiece(new Queen(board, 7, 3, Colour.BLACK));
        board.addPiece(new King(board, 7, 4, Colour.BLACK, true));

        updateLegalMoves();
        controller.updatePosition(board);
    }

    /**
     * @param sourceRow
     * @param sourceColumn
     * @param targetRow
     * @param targetColumn
     */
    @Override
    public void handleMove(int sourceRow, int sourceColumn, int targetRow, int targetColumn) {

        for (Move move: legalMoves){
            if (move.p().getRow() == sourceRow && move.p().getCol() == sourceColumn && move.cell().getRow() == targetRow && move.cell().getCol() == targetColumn){
                board.movePiece(move);
                updateLegalMoves();
                controller.updatePosition(board);
                return;
            }
        }
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

    private void updateLegalMoves(){
        legalMoves.clear();
        for (Move move: board.getPseudolegalMoves()){
            if (checkLegalMoves(move)) {
                legalMoves.add(move);
            }
        }
    }

    private boolean checkLegalMoves(Move move){
        Cell stepOverCell = null;
        if (move.getClass() == CastlingMove.class)stepOverCell = board.getCell(move.cell().getRow(), (move.cell().getCol() == 2) ? 3: 5);

        board.movePiece(move);

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
}
