package com.example.chessengine.UCI;

import com.example.chessengine.HelloController;

public class GameState implements MoveHandler{
    private final Board board;
    private final HelloController controller;

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
        for (Move move: board.getPseudolegalMoves()){
            if (move.p().getRow() == sourceRow && move.p().getCol() == sourceColumn && move.cell().getRow() == targetRow && move.cell().getCol() == targetColumn){
                board.movePiece(move);
                controller.updatePosition(board);
                return;
            }
        }
    }
}
