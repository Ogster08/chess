package com.example.chessengine.Book;

import com.example.chessengine.Board.Board;
import com.example.chessengine.Board.Moves.Move;

import java.util.HashMap;
import java.util.Random;

/**
 * The book class is for creating an openings book, and getting random moves from it.
 */
public class Book {
    /**
     * A hashmap containing each unique position and the book moves in that position.
     */
    public final HashMap<Long, BookMoves> bookPositions = new HashMap<>();

    /**
     * Updates the openings book, to add the book move if it doesn't already exist at the given position.
     * If the book move already exists at that position, then increment the number of times played.
     * @param bookMove The move being added to the book.
     * @param position The position where the move is taken from.
     */
    public void updateBook(BookMove bookMove, long position){
        if (!bookPositions.containsKey(position)){
            bookPositions.put(position, new BookMoves());
        }
        bookPositions.get(position).addMove(bookMove);
    }

    /**
     * Updates the openings book, to add the book move if it doesn't already exist at the given position, with numPlayed.
     * If the book move already exists at that position, then increment the number of times played by numPlayed.
     * @param bookMove The move being added to the book.
     * @param position The position where the move is taken from.
     * @param numPlayed The number of times that move has been played
     */
    public void updateBook(BookMove bookMove, long position, int numPlayed){
        if (!bookPositions.containsKey(position)){
            bookPositions.put(position, new BookMoves());
        }
        bookPositions.get(position).addMove(bookMove, numPlayed);
    }

    /**
     * Checks if the position is in the openings book.
     * @param zobristKey The zobrist hash of the position being checked.
     * @return if the position is in the openings book.
     */
    public boolean positionInBook(long zobristKey){
        return bookPositions.containsKey(zobristKey);
    }

    /**
     * Randomly gets a move for the current position from the openings book, without any weightings.
     * Throws an error if no equivalent legal Move object can be found
     * @param board The board, where the random move will be used, for its position, and to check the move is legal.
     * @return The random move from the openings book, for the current board position
     * @throws RuntimeException error if the book move doesn't correspond to a legal move in the current position.
     */
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

    /**
     * Randomly gets a move for the current position from the openings book.
     * The moves are weighted based upon how often it was played compared to the others, with some smoothing.
     * Throws an error if no equivalent legal Move object can be found
     * @param board The board, where the random move will be used, for its position, and to check the move is legal.
     * @return The random move from the openings book, for the current board position
     * @throws RuntimeException error if the book move doesn't correspond to a legal move in the current position.
     */
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

    /**
     * Smooths the weights based on the strength.
     * Higher chance move have their chance reduced.
     * Lower chance moves have their chance increased.
     * @param weights The weights being smoothed
     * @param strength (0 - 1) 0 is no smoothing, 1 is all have an equal chance
     * @return The sum of the weights.
     */
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
