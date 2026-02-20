package com.example.chessengine.UCI;

import java.io.IOException;

@FunctionalInterface
public interface SceneSwitcher {
    void switchScene(Class<?> GameStateClass) throws IOException;
}
