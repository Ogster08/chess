package com.example.chessengine.Book;

import java.util.HashMap;

public class BookMoves {
    public final HashMap<BookMove, Integer> MovesWithNumPlayed = new HashMap<>();

    public void addMove(BookMove bookMove){
        MovesWithNumPlayed.merge(bookMove, 1, Integer::sum);
    }

    public void addMove(BookMove bookMove, int numPlayed){
        MovesWithNumPlayed.merge(bookMove, numPlayed, Integer::sum);
    }
}
