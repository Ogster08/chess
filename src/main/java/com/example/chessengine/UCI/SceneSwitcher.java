package com.example.chessengine.UCI;

import java.io.IOException;


public interface SceneSwitcher {
    void gameSwitcher(Class<?> GameStateClass, Colour colour) throws IOException;
    void menuSwitcher() throws IOException;
}
