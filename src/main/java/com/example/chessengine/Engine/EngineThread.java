package com.example.chessengine.Engine;

import com.example.chessengine.Board.Moves.Move;
import javafx.application.Platform;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

/**
 * The EngineThread class runs an Engine object on a separate thread.
 */
public class EngineThread extends Thread{
    /**
     * The Engine object being run on a separate thread.
     */
    private final Engine engine;

    /**
     * The queue of commands for the engine to execute.
     */
    private final BlockingQueue<EngineCommand> commandQueue = new LinkedBlockingQueue<>();

    /**
     * A boolean for if the thread is running.
     */
    private volatile boolean running = true;

    /**
     * @param engine The engine to be run on a separate thread.
     */
    public EngineThread(Engine engine) {
        this.engine = engine;
        setDaemon(true);
    }

    /**
     * Run the new thread.
     * While the thread is running, execute the commands in the queue.
     */
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

    /**
     * Adds a command to the queue to get the next engine move.
     * @param callBack The function run, when move is returned.
     */
    public void requestMove(Consumer<Move> callBack){
        commandQueue.offer(() -> {
            Move move = engine.getNextMove();
            Platform.runLater(() -> callBack.accept(move));
        });
    }

    /**
     * Adds a command to the queue to get the position count at the given depth.
     * @param callBack The function run when the count is returned.
     * @param depth The depth the counting happens at.
     */
    public void getCountMoves(Consumer<Integer> callBack, int depth){
        commandQueue.offer(() ->{
            int movesCount = engine.countMoves(depth);
            Platform.runLater(() -> callBack.accept(movesCount));
        });
    }

    /**
     * Stop the engine thread.
     */
    public void stopEngine(){
        running = false;
        interrupt();
    }

    /**
     * The interface to define the function signature for executing commands.
     */
    @FunctionalInterface
    private interface EngineCommand{
        void execute();
    }
}
