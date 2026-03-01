package com.example.chessengine.Engine;

import com.example.chessengine.Board.*;
import com.example.chessengine.Board.Moves.Move;
import com.example.chessengine.Board.Pieces.*;
import com.example.chessengine.Book.Book;
import com.example.chessengine.Book.BookCreator;
import com.example.chessengine.Tablebase.LichessAPI;

import java.util.*;

/**
 * The Engine class can find the next move to do, or count the number of positions after a given depth.
 */
public class Engine{
    /**
     * The Board object, the game is happening on
     */
    private final Board board;

    /**
     * The colour the engine playing is
     */
    private final Colour engineColour;

    /**
     * The best move in the current position, overwritten if a new best move is found.
     */
    private Move bestMove;

    /**
     * used for debug to see the number of positions looked at.
     */
    private int count = 0;

    /**
     * the score associated with a king being mated, being sufficiently higher than a position score ever could
     */
    private final int mateScore = 100_000;

    /**
     * If the engine is still trying to use the openings book to get moves.
     */
    private boolean usingBook = true;

    /**
     * The openings book used to find moves.
     */
    private final Book book;

    /**
     * Constructor to create a new engine
     * @param board The Board object the game is being played on.
     * @param engineColour The colour the engine is playing as.
     */
    public Engine(Board board, Colour engineColour) {
        this.board = board;
        this.engineColour = engineColour;
        book = BookCreator.LoadBook();
    }

    /**
     * Finds the next move to play, using either the openings book, a tablebase or searching through position evaluations.
     * @return The next move the engine is playing
     */
    public Move getNextMove(){
        if (usingBook){
            if (!book.positionInBook(board.getZobristKey())) usingBook = false;
            else {
                if ("true".equals(System.getenv("LOGS"))) System.out.println("getting book move");
                return book.getRandomWeightedMove(board);
            }
        }
        if (board.getPieceCount() <= 7) {
            Move move = LichessAPI.getMove(board);
            if (move != null) {
                if ("true".equals(System.getenv("LOGS"))) System.out.println("Tablebase move");
                return move;
            }
        }
        count = 0;
        bestMove = null;
        if ("true".equals(System.getenv("LOGS"))) System.out.println("-----normal move-----");
        int eval = search(4, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
        if ("true".equals(System.getenv("LOGS"))){
            System.out.println(eval);
            System.out.println(count);
        }
        return bestMove;
    }

    /**
     * Performs an alpha-beta pruning algorithm to find the best move by searching upto the maxDepth.
     * It creates the search tree as it searches it, to prevent wasted calculations.
     * It updates bestMove when a new best move at a depth of 0 is found.
     * @param maxDepth The max ply the search is going to.
     * @param currentDepth The current ply the search is at.
     * @param alpha The highest number the maximising player can guaranty.
     * @param beta The lowest number the minimising player can guaranty.
     * @param maximising If at the current depth the search should be maximising or minimising the score.
     * @return The score of the position the search is currently evaluating.
     */
    private int search(int maxDepth, int currentDepth, int alpha, int beta, boolean maximising){
        if (maxDepth == 0) return evaluatePosition();

        boolean noMoves = true;

        if (maximising){
            for (Move move: board.getPseudolegalMoves()){
                if (board.checkLegalMoves(move)){
                    noMoves = false;
                    count++;
                    board.movePiece(move, true);
                    int score = search(maxDepth - 1, currentDepth + 1, alpha, beta, false);
                    board.undoMove();

                    if (score > alpha){
                        alpha = score;
                        if (currentDepth == 0){
                            bestMove = move;
                        }
                    }

                    if (alpha >= beta){
                        break;
                    }
                }
            }
            if (noMoves){
                if (board.isInCheck()) {
                    return currentDepth-mateScore;
                } else {
                    return 0;
                }
            }
            return alpha;
        }
        else {
            for (Move move: board.getPseudolegalMoves()){
                if (board.checkLegalMoves(move)){
                    noMoves = false;
                    board.movePiece(move, true);
                    int score = search(maxDepth - 1, currentDepth + 1, alpha, beta, true);
                    board.undoMove();

                    if (score < beta){
                        beta = score;
                    }

                    if (alpha >= beta){
                        break;
                    }
                }
            }
            if (noMoves){
                if (board.isInCheck()) return mateScore - currentDepth;
                else return 0;
            }
            return beta;
        }
    }

    /**
     * A debug function to count the number of moves upto the given depth.
     * @param depth The depth the counting stops at
     * @return The number of positions at the depth given.
     */
    public int countMoves(int depth){
        if (depth == 0) return 1;

        int count = 0;
        for (Move move: board.getPseudolegalMoves()){
            if (board.checkLegalMoves(move)){
                board.movePiece(move, true);
                count += countMoves(depth - 1);
                board.undoMove();
            }
        }
        return count;
    }

    /**
     * A hashmap of each piece class to its corresponding material value.
     */
    private static final Map<Class<?>, Integer> pieceScores = new HashMap<>(){
        {
            put(Pawn.class, 100);
            put(Knight.class, 325);
            put(Bishop.class, 325);
            put(Rook.class, 525);
            put(Queen.class, 1000);
            put(King.class, 0);
        }
    };

    /**
     * Evaluates the current position, by taking into account the material value and position of the piece on each side.
     * Tries to encourage structural improvements in the opening, to focus on pawn structure.
     * @return The evaluation score of the current position.
     */
    private int evaluatePosition(){
        int evaluation = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece p = board.getCell(i, j).getPiece();
                if (p != null){
                    int score = 0;
                    score += pieceScores.get(p.getClass());
                    if (p.getClass() == Pawn.class) {
                        score += readTable(pawnTable, p);
                    } else if (p.getClass() == Knight.class) {
                        score += readTable(knightTable, p);
                    } else if (p.getClass() == Bishop.class) {
                        score += readTable(bishopTable, p);
                    } else if (p.getClass() == Rook.class) {
                        score += readTable(rookTable, p);
                    } else if (p.getClass() == Queen.class) {
                        score += readTable(queenTable, p);
                    } else if (p.getClass() == King.class) {
                        score += readTable(kingMiddleTable, p);
                    }

                    if (p.getColour() == engineColour) {
                        evaluation += score;
                    } else {
                        evaluation -= score;
                    }
                }
            }
        }

        if (board.getFullMoveCounter() <= 8){
            Set<Piece> movedPieces = new HashSet<>(10);
            for (UndoMoveInfo umi: board.undoMoveInfoList){
                Piece p = umi.move.p();
                if (p.getColour() == engineColour){
                    if (movedPieces.contains(p)) {
                        evaluation -= 20;
                        if (p.getClass() == Queen.class) evaluation -= 20;
                    } else {
                        movedPieces.add(p);
                    }
                }
            }
        }

        return evaluation;
    }

    /**
     * Gets the positional score of the piece from the table given, flipping the row if the piece's colour is black.
     * @param table The table the score is taken from.
     * @param piece The piece used for the row, column of the table.
     * @return The score of the piece position from the table.
     */
    private int readTable(int[] table, Piece piece){
        int index;
        if (piece.getColour() == Colour.WHITE) index = 8 * (7 - piece.getRow()) + piece.getCol();
        else index = 8 * piece.getRow() + piece.getCol();
        return table[index];
    }

    /**
     * The positional scores for a white pawn.
     */
    private static final int[] pawnTable = {
            0,  0,  0,  0,  0,  0,  0,  0,
            50, 50, 50, 50, 50, 50, 50, 50,
            10, 10, 20, 30, 30, 20, 10, 10,
            5,  5, 10, 25, 25, 10,  5,  5,
            0,  0,  0, 20, 20,  0,  0,  0,
            5, -5,-10,  0,  0,-10, -5,  5,
            5, 10, 10,-20,-20, 10, 10,  5,
            0,  0,  0,  0,  0,  0,  0,  0
    };

    /**
     * The positional scores for a white knight.
     */
    private static final int[] knightTable = {
            -50,-40,-30,-30,-30,-30,-40,-50,
            -40,-20,  0,  0,  0,  0,-20,-40,
            -30,  0, 10, 15, 15, 10,  0,-30,
            -30,  5, 15, 20, 20, 15,  5,-30,
            -30,  0, 15, 20, 20, 15,  0,-30,
            -30,  5, 10, 15, 15, 10,  5,-30,
            -40,-20,  0,  5,  5,  0,-20,-40,
            -50,-40,-30,-30,-30,-30,-40,-50,
    };

    /**
     * The positional scores for a white bishop.
     */
    private static final int[] bishopTable = {
            -20,-10,-10,-10,-10,-10,-10,-20,
            -10,  0,  0,  0,  0,  0,  0,-10,
            -10,  0,  5, 10, 10,  5,  0,-10,
            -10,  5,  5, 10, 10,  5,  5,-10,
            -10,  0, 10, 10, 10, 10,  0,-10,
            -10, 10, 10, 10, 10, 10, 10,-10,
            -10,  5,  0,  0,  0,  0,  5,-10,
            -20,-10,-10,-10,-10,-10,-10,-20,
    };

    /**
     * The positional scores for a white rook.
     */
    private static final int[] rookTable = {
            0,  0,  0,  0,  0,  0,  0,  0,
            5, 10, 10, 10, 10, 10, 10,  5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            0,  0,  0,  5,  5,  0,  0,  0
    };

    /**
     * The positional scores for a white queen.
     */
    private static final int[] queenTable = {
            -20,-10,-10, -5, -5,-10,-10,-20,
            -10,  0,  0,  0,  0,  0,  0,-10,
            -10,  0,  5,  5,  5,  5,  0,-10,
            -5,  0,  5,  5,  5,  5,  0, -5,
            0,  0,  5,  5,  5,  5,  0, -5,
            -10,  5,  5,  5,  5,  5,  0,-10,
            -10,  0,  5,  0,  0,  0,  0,-10,
            -20,-10,-10, -5, -5,-10,-10,-20
    };

    /**
     * The positional scores for a white king not in the endgame.
     */
    private static final int[] kingMiddleTable = {
            -30,-40,-40,-50,-50,-40,-40,-30,
            -30,-40,-40,-50,-50,-40,-40,-30,
            -30,-40,-40,-50,-50,-40,-40,-30,
            -30,-40,-40,-50,-50,-40,-40,-30,
            -20,-30,-30,-40,-40,-30,-30,-20,
            -10,-20,-20,-20,-20,-20,-20,-10,
            20, 20,  0,  0,  0,  0, 20, 20,
            20, 30, 10,  0,  0, 10, 30, 20
    };

    /**
     * The positional scores for a white king in the endgame.
     */
    private static final int[] kingEndTable = {
            -50,-40,-30,-20,-20,-30,-40,-50,
            -30,-20,-10,  0,  0,-10,-20,-30,
            -30,-10, 20, 30, 30, 20,-10,-30,
            -30,-10, 30, 40, 40, 30,-10,-30,
            -30,-10, 30, 40, 40, 30,-10,-30,
            -30,-10, 20, 30, 30, 20,-10,-30,
            -30,-30,  0,  0,  0,  0,-30,-30,
            -50,-30,-30,-30,-30,-30,-30,-50
    };
}
