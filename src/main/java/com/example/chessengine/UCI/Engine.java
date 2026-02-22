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
        System.out.println("-----New move-----");
        System.out.println(search(4, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, true));
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
        int evaluation = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece p = board.getCell(i, j).getPiece();
                if (p != null){
                    int score = 0;
                    score += pieceScores.get(p.getClass());
                    if (p.getClass() == Pawn.class) {
                        score += readTable(pawnTable, p);
                    } else if (p.getClass() == Knight.class) {
                        score += readTable(knightTable, p);
                    } else if (p.getClass() == Bishop.class) {
                        score += readTable(bishopTable, p);
                    } else if (p.getClass() == Rook.class) {
                        score += readTable(rookTable, p);
                    } else if (p.getClass() == Queen.class) {
                        score += readTable(queenTable, p);
                    } else if (p.getClass() == King.class) {
                        score += readTable(kingMiddleTable, p);
                    }

                    if (p.getColour() == engineColour) {
                        evaluation += score;
                    } else {
                        evaluation -= score;
                    }
                }
            }
        }
        return evaluation;
    }

    private int readTable(int[] table, Piece piece){
        int index;
        if (piece.getColour() == Colour.WHITE) index = 8 * (7 - piece.getRow()) + piece.getCol();
        else index = 8 * piece.getRow() + piece.getCol();
        return table[index];
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

    private static final int[] pawnTable = {
            0,  0,  0,  0,  0,  0,  0,  0,
            50, 50, 50, 50, 50, 50, 50, 50,
            10, 10, 20, 30, 30, 20, 10, 10,
            5,  5, 10, 25, 25, 10,  5,  5,
            0,  0,  0, 20, 20,  0,  0,  0,
            5, -5,-10,  0,  0,-10, -5,  5,
            5, 10, 10,-20,-20, 10, 10,  5,
            0,  0,  0,  0,  0,  0,  0,  0
    };

    private static final int[] knightTable = {
            -50,-40,-30,-30,-30,-30,-40,-50,
            -40,-20,  0,  0,  0,  0,-20,-40,
            -30,  0, 10, 15, 15, 10,  0,-30,
            -30,  5, 15, 20, 20, 15,  5,-30,
            -30,  0, 15, 20, 20, 15,  0,-30,
            -30,  5, 10, 15, 15, 10,  5,-30,
            -40,-20,  0,  5,  5,  0,-20,-40,
            -50,-40,-30,-30,-30,-30,-40,-50,
    };

    private static final int[] bishopTable = {
            -20,-10,-10,-10,-10,-10,-10,-20,
            -10,  0,  0,  0,  0,  0,  0,-10,
            -10,  0,  5, 10, 10,  5,  0,-10,
            -10,  5,  5, 10, 10,  5,  5,-10,
            -10,  0, 10, 10, 10, 10,  0,-10,
            -10, 10, 10, 10, 10, 10, 10,-10,
            -10,  5,  0,  0,  0,  0,  5,-10,
            -20,-10,-10,-10,-10,-10,-10,-20,
    };

    private static final int[] rookTable = {
            0,  0,  0,  0,  0,  0,  0,  0,
            5, 10, 10, 10, 10, 10, 10,  5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            0,  0,  0,  5,  5,  0,  0,  0
    };

    private static final int[] queenTable = {
            -20,-10,-10, -5, -5,-10,-10,-20,
            -10,  0,  0,  0,  0,  0,  0,-10,
            -10,  0,  5,  5,  5,  5,  0,-10,
            -5,  0,  5,  5,  5,  5,  0, -5,
            0,  0,  5,  5,  5,  5,  0, -5,
            -10,  5,  5,  5,  5,  5,  0,-10,
            -10,  0,  5,  0,  0,  0,  0,-10,
            -20,-10,-10, -5, -5,-10,-10,-20
    };

    private static final int[] kingMiddleTable = {
            -30,-40,-40,-50,-50,-40,-40,-30,
            -30,-40,-40,-50,-50,-40,-40,-30,
            -30,-40,-40,-50,-50,-40,-40,-30,
            -30,-40,-40,-50,-50,-40,-40,-30,
            -20,-30,-30,-40,-40,-30,-30,-20,
            -10,-20,-20,-20,-20,-20,-20,-10,
            20, 20,  0,  0,  0,  0, 20, 20,
            20, 30, 10,  0,  0, 10, 30, 20
    };

    private static final int[] kingEndTable = {
            -50,-40,-30,-20,-20,-30,-40,-50,
            -30,-20,-10,  0,  0,-10,-20,-30,
            -30,-10, 20, 30, 30, 20,-10,-30,
            -30,-10, 30, 40, 40, 30,-10,-30,
            -30,-10, 30, 40, 40, 30,-10,-30,
            -30,-10, 20, 30, 30, 20,-10,-30,
            -30,-30,  0,  0,  0,  0,-30,-30,
            -50,-30,-30,-30,-30,-30,-30,-50
    };
}
