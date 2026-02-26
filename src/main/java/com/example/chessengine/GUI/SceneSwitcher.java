package com.example.chessengine.GUI;

import com.example.chessengine.Board.Colour;

import java.io.IOException;


public interface SceneSwitcher {
    void gameSwitcher(Class<?> GameStateClass, Colour colour) throws IOException;
    void menuSwitcher() throws IOException;
}
