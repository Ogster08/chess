package com.example.chessengine.Book;

import com.example.chessengine.UCI.Board;
import com.example.chessengine.UCI.Move;

import java.util.HashMap;
import java.util.Random;

public class Book {
    public final HashMap<Long, BookMoves> bookPositions = new HashMap<>();

    public void updateBook(BookMove bookMove, long position){
        if (!bookPositions.containsKey(position)){
            bookPositions.put(position, new BookMoves());
        }
        bookPositions.get(position).addMove(bookMove);
    }

    public void updateBook(BookMove bookMove, long position, int numPlayed){
        if (!bookPositions.containsKey(position)){
            bookPositions.put(position, new BookMoves());
        }
        bookPositions.get(position).addMove(bookMove, numPlayed);
    }

    public boolean positionInBook(long zobristKey){
        return bookPositions.containsKey(zobristKey);
    }

    public BookMoves getBookMoves(long zobristKey){
        return bookPositions.get(zobristKey);
    }

    public Move getRandomMove(Board board){
        BookMove[] bookMoveArray = bookPositions.get(board.getZobristKey()).MovesWithNumPlayed.keySet().toArray(new BookMove[0]);
        Random random = new Random();
        BookMove bookMove = bookMoveArray[random.nextInt(bookMoveArray.length)];
        for (Move move: board.getPseudolegalMoves()){
            if (!board.checkLegalMoves(move)) continue;
            if (move.p().getRow() == bookMove.pieceRow() &&
                    move.p().getCol() == bookMove.pieceCol() &&
                    move.cell().getRow() == bookMove.cellRow() &&
                    move.cell().getCol() == bookMove.cellCol()) {
                return move;
            }
        }
        throw new RuntimeException("not found move for: " + bookMove);
    }

    public Move getRandomWeightedMove(Board board){
        BookMoves bookMoves = bookPositions.get(board.getZobristKey());
        BookMove[] bookMoveArray = bookMoves.MovesWithNumPlayed.keySet().toArray(new BookMove[0]);
        float[] weights = new float[bookMoveArray.length];
        for (int i = 0; i < bookMoveArray.length; i++) {
            weights[i] = bookMoves.MovesWithNumPlayed.get(bookMoveArray[i]);
        }

         float sum = smoothWeights(weights, 0.1f);

        float[] cumulProbs = new float[weights.length];
        float cumulProb = 0;
        for (int i = 0; i < weights.length; i++) {
            cumulProb += weights[i] / sum;
            cumulProbs[i] = cumulProb;
        }

        BookMove bookMove = null;
        Random random = new Random();
        float p = random.nextFloat();
        for (int i = 0; i < bookMoveArray.length; i++) {
            if (p <= cumulProbs[i]) {
                bookMove = bookMoveArray[i];
                break;
            }
        }
        assert bookMove != null;

        for (Move move: board.getPseudolegalMoves()){
            if (!board.checkLegalMoves(move)) continue;
            if (move.p().getRow() == bookMove.pieceRow() &&
                    move.p().getCol() == bookMove.pieceCol() &&
                    move.cell().getRow() == bookMove.cellRow() &&
                    move.cell().getCol() == bookMove.cellCol()) {
                return move;
            }
        }
        throw new RuntimeException("not found move for: " + bookMove);
    }

    private static float smoothWeights(float[] weights, float strength){
        float sum = 0;
        for (float x: weights) sum += x;
        float average = sum / weights.length;

        for (int i = 0; i < weights.length; i++) {
            weights[i] += (average - weights[i]) * strength;
        }
        return sum;
    }
}
