package com.example.chessengine.Book;

import java.util.HashMap;

/**
 * The bookMoves class contains all the book moves, and their number of times played for a specific position
 */
public class BookMoves {
    /**
     * A hashmap of each move at the position, with the number of times it was played in the PGN games file at that position.
     */
    public final HashMap<BookMove, Integer> MovesWithNumPlayed = new HashMap<>();

    /**
     * Adds a bookMove to the MovesWithNumPlayed hashmap, incrementing the number by 1 if it already exists.
     * @param bookMove The book move being added.
     */
    public void addMove(BookMove bookMove){
        MovesWithNumPlayed.merge(bookMove, 1, Integer::sum);
    }

    /**
     * Adds a bookMove to the MovesWithNumPlayed hashmap, incrementing the number by numPlayed if it already exists.
     * @param bookMove The book move being added.
     * @param numPlayed The number of times that move has been played in this position
     */
    public void addMove(BookMove bookMove, int numPlayed){
        MovesWithNumPlayed.merge(bookMove, numPlayed, Integer::sum);
    }
}
