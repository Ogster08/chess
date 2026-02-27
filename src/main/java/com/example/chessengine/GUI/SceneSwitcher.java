package com.example.chessengine.GUI;

import com.example.chessengine.Board.Colour;

import java.io.IOException;


/**
 * Interface to define the methods that allow page controllers to switch to different pages.
 */
public interface SceneSwitcher {
    /**
     * Switches to the game page, and uses the GameStateClass and colour to set up the game.
     * @param GameStateClass The class of the game state to be used for the chess game.
     * @param colour The colour of the player, if to be used if the user is against an engine.
     * @throws IOException Error if the switch is unsuccessful.
     */
    void gameSwitcher(Class<?> GameStateClass, Colour colour) throws IOException;

    /**
     * Switches to the menu page.
     * @throws IOException Error if the switch is unsuccessful.
     */
    void menuSwitcher() throws IOException;
}
