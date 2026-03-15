package com.example.chessengine.Engine;

import com.example.chessengine.Board.Board;
import com.example.chessengine.Board.Colour;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The test class for testing the move generation
 */
class Perft {

    /**
     * The test function to test the countMoves function to test the move generation, by performing a perft test to different depths
     */
    @Test
    @DisplayName("count different positions at different depths.")
    void countMoves() {
        Engine engine = new Engine(Board.getStartPosition(), Colour.WHITE);
        assertAll(
                () -> assertEquals(20, engine.countMoves(1)),
                () -> assertEquals(400, engine.countMoves(2)),
                () -> assertEquals(8902, engine.countMoves(3)),
                () -> assertEquals(197_281, engine.countMoves(4)),
                () -> assertEquals(4_865_609, engine.countMoves(5))
        );
    }
}