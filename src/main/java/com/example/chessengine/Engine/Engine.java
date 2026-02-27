package com.example.chessengine.Engine;

import com.example.chessengine.Board.*;
import com.example.chessengine.Board.Moves.Move;
import com.example.chessengine.Board.Pieces.*;
import com.example.chessengine.Book.Book;
import com.example.chessengine.Book.BookCreator;
import com.example.chessengine.Tablebase.LichessAPI;

import java.util.*;

public class Engine{
    private final Board board;
    private final Colour engineColour;
    private Move bestMove;
    private int count = 0;
    private final int mateScore = 100_000;
    private boolean usingBook = true;
    private final Book book;

    public Engine(Board board, Colour engineColour) {
        this.board = board;
        this.engineColour = engineColour;
        book = BookCreator.LoadBook();
    }

    public Move getNextMove(){
        if (usingBook){
            if (!book.positionInBook(board.getZobristKey())) usingBook = false;
            else {
                if ("true".equals(System.getenv("LOGS"))) System.out.println("getting book move");
                return book.getRandomWeightedMove(board);
            }
        }
        if (board.getPieceCount() <= 7) {
            Move move = LichessAPI.getMove(board);
            if (move != null) {
                if ("true".equals(System.getenv("BUILD_BOOK"))) System.out.println("Tablebase move");
                return move;
            }
        }
        count = 0;
        bestMove = null;
        if ("true".equals(System.getenv("BUILD_BOOK"))) System.out.println("-----normal move-----");
        int eval = search(4, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
        if ("true".equals(System.getenv("BUILD_BOOK"))){
            System.out.println(eval);
            System.out.println(count);
        }
        return bestMove;
    }

    private int search(int maxDepth, int currentDepth, int alpha, int beta, boolean maximising){
        if (maxDepth == 0) return evaluatePosition();

        boolean noMoves = true;

        if (maximising){
            for (Move move: board.getPseudolegalMoves()){
                if (board.checkLegalMoves(move)){
                    noMoves = false;
                    count++;
                    board.movePiece(move, true);
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
            if (noMoves){
                if (board.isInCheck()) {
                    return currentDepth-mateScore;
                } else {
                    return 0;
                }
            }
            return alpha;
        }
        else {
            for (Move move: board.getPseudolegalMoves()){
                if (board.checkLegalMoves(move)){
                    noMoves = false;
                    board.movePiece(move, true);
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
            if (noMoves){
                if (board.isInCheck()) return mateScore - currentDepth;
                else return 0;
            }
            return beta;
        }
    }

    public int countMoves(int depth){
        if (depth == 0) return 1;

        int count = 0;
        for (Move move: board.getPseudolegalMoves()){
            if (board.checkLegalMoves(move)){
                board.movePiece(move, true);
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

        if (board.getFullMoveCounter() <= 10){
            Set<Piece> movedPieces = new HashSet<>(5);
            for (UndoMoveInfo umi: board.undoMoveInfoList){
                Piece p = umi.move.p();
                if (p.getColour() == engineColour){
                    if (movedPieces.contains(p)) evaluation -= 20;
                    if (p.getClass() == Queen.class) evaluation -= 20;
                    else movedPieces.add(p);
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
