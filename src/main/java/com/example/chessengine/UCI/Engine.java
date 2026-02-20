package com.example.chessengine.UCI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Engine {
    private final Board board;

    public Engine(Board board) {
        this.board = board;
    }

    public Move getNextMove(){
        Random random = new Random();
        List<Move> legalMoves = new ArrayList<>();
        for (Move move: board.getPseudolegalMoves()){
            if (checkLegalMoves(move)) legalMoves.add(move);
        }
        System.out.println("got next move");
        return legalMoves.get(random.nextInt(legalMoves.size()));
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
