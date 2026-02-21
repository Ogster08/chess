package com.example.chessengine.UCI;

import javafx.application.Platform;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

public class EngineThread extends Thread{
    private final Engine engine;
    private final BlockingQueue<EngineCommand> commandQueue = new LinkedBlockingQueue<>();
    private volatile boolean running = true;

    public EngineThread(Engine engine) {
        this.engine = engine;
        setDaemon(true);
    }

    @Override
    public void run(){
        while (running){
            try{
                commandQueue.take().execute();
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    public void requestMove(Consumer<Move> callBack){
        commandQueue.offer(() -> {
            Move move = engine.getNextMove();
            Platform.runLater(() -> callBack.accept(move));
        });
    }

    public void getCountMoves(Consumer<Integer> callBack, int depth){
        commandQueue.offer(() ->{
            int movesCount = engine.countMoves(depth);
            Platform.runLater(() -> callBack.accept(movesCount));
        });
    }

    public void stopEngine(){
        running = false;
        interrupt();
    }

    @FunctionalInterface
    private interface EngineCommand{
        void execute();
    }
}
