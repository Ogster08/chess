package com.example.chessengine.UCI;

import java.util.*;

public class Engine{
    private final Board board;
    private final Colour engineColour;
    private Move bestMove;
    private int count = 0;

    public Engine(Board board, Colour engineColour) {
        this.board = board;
        this.engineColour = engineColour;
    }

    public Move getNextMove(){
        count = 0;
        search(5, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
        System.out.println(count);
        return bestMove;
    }

    private int search(int maxDepth, int currentDepth, int alpha, int beta, boolean maximising){
        if (maxDepth == 0) return evaluatePosition();

        if (maximising){
            for (Move move: board.getPseudolegalMoves()){
                if (checkLegalMoves(move)){
                    count++;
                    board.movePiece(move);
                    int score = search(maxDepth - 1, currentDepth + 1, alpha, beta, false);
                    board.undoMove();

                    if (score > alpha){
                        alpha = score;
                        if (currentDepth == 0){
                            bestMove = move;
                        }
                    }

                    if (alpha >= beta){
                        break;
                    }
                }
            }
            return alpha;
        }
        else {
            for (Move move: board.getPseudolegalMoves()){
                if (checkLegalMoves(move)){
                    board.movePiece(move);
                    int score = search(maxDepth - 1, currentDepth + 1, alpha, beta, true);
                    board.undoMove();

                    if (score < beta){
                        beta = score;
                    }

                    if (alpha >= beta){
                        break;
                    }
                }
            }
            return beta;
        }
    }

    public int countMoves(int depth){
        if (depth == 0) return 1;

        int count = 0;
        for (Move move: board.getPseudolegalMoves()){
            if (checkLegalMoves(move)){
                board.movePiece(move);
                count += countMoves(depth - 1);
                board.undoMove();
            }
        }
        return count;
    }

    private static final Map<Class<?>, Integer> pieceScores = new HashMap<>(){
        {
            put(Pawn.class, 100);
            put(Knight.class, 325);
            put(Bishop.class, 325);
            put(Rook.class, 525);
            put(Queen.class, 1000);
            put(King.class, 0);
        }
    };

    private int evaluatePosition(){
        int score = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece p = board.getCell(i, j).getPiece();
                if (p != null){
                    if (p.getColour() == engineColour) score += pieceScores.get(p.getClass());
                    else score -= pieceScores.get(p.getClass());
                }
            }
        }
        return score;
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
