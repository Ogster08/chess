package com.example.chessengine.Engine;

import com.example.chessengine.Board.*;
import com.example.chessengine.Board.Moves.Move;
import com.example.chessengine.Board.Moves.PromotionMove;
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

    private boolean stopSearch = false;

    private Move bestMoveInCurrentSearch;

    private int bestEval;
    private int bestEvalThisSearch;
    private int fullCount = 0;
    private int latestFinishedDepth;

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

        iterativeDeepening();
        if ("true".equals(System.getenv("LOGS"))){
            System.out.println("-----normal move-----");
            System.out.println(bestEval);
            System.out.println(fullCount);
            System.out.println(latestFinishedDepth);
        }
        return bestMove;
    }

    private void iterativeDeepening(){
        stopSearch = false;
        bestMove = null;
        bestEval = 0;
        fullCount = 0;

        int depth = 1;
        while (!stopSearch){
            count = 0;
            bestMoveInCurrentSearch = null;
            bestEvalThisSearch = search(depth, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, true);

            if (!stopSearch) {
                bestMove = bestMoveInCurrentSearch;
                bestEval = bestEvalThisSearch;
                System.out.println(depth + ": " + bestEval + ", " + bestMove + ", " + count);
                fullCount = count;
                latestFinishedDepth = depth;
            }

            if (Math.abs(bestEval) + depth >= mateScore) return;

            depth++;
        }
    }

    public void stopCurrentSearch(){
        stopSearch = true;
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
        if (stopSearch){
            return 0;
        }

        if (maxDepth == 0) {
            return quiescenceSearch(alpha, beta, maximising);
        }

        boolean noMoves = true;
        List<Move> moveList = board.getPseudolegalMoves();
        orderMoves(moveList);
        if (currentDepth == 0){
            if (moveList.remove(bestMove)){
                moveList.addFirst(bestMove);
            }
        }

        if (maximising){
            for (Move move: moveList){
                if (board.checkLegalMoves(move, false)){
                    noMoves = false;
                    count++;
                    int score = search(maxDepth - 1, currentDepth + 1, alpha, beta, false);
                    board.undoMove();

                    if (score > alpha){
                        alpha = score;
                        if (currentDepth == 0){
                            bestMoveInCurrentSearch = move;
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
            for (Move move: moveList){
                if (board.checkLegalMoves(move, false)){
                    noMoves = false;
                    count++;
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
     * @param alpha The best score the current player can guarantee
     * @param beta The lowest score the opponent can guarantee
     * @return The score of the position the search is currently evaluating
     */
    private int quiescenceSearch(int alpha, int beta, boolean maximising){
        int bestScore = evaluatePosition();


        if (maximising) {
            if (bestScore > alpha){
                alpha = bestScore;
            }
        } else {
            if (bestScore < beta){
                beta = bestScore;
            }
        }

        if (bestScore >= beta){
            return bestScore; // The opponent won't allow this move
        }

        List<Move> moveList = board.getPseudolegalMoves().stream().filter(move -> move.cell().isHasPiece()).toList();
        //orderMoves(moveList);

        if (maximising) {
            for (Move move: moveList){
                if (board.checkLegalMoves(move, false)){
                    count++;
                    int score = quiescenceSearch(alpha, beta, false);
                    board.undoMove();

                    if (score >= beta){
                        return score;
                    }
                    if (score > alpha){
                        alpha = score;
                    }
                }
            }
            return alpha;
        }
        else {
            for (Move move: moveList){
                if (board.checkLegalMoves(move, false)){
                    count++;
                    int score = quiescenceSearch(alpha, beta, true);
                    board.undoMove();

                    if (score >= beta){
                        return score;
                    }
                    if (score < beta){
                        beta = score;
                    }
                }
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
            if (board.checkLegalMoves(move, false)){
                count += countMovesX(depth - 1);
                board.undoMove();
            }
        }
        return count;
    }

    private int countMovesX(int depth){
        if (depth == 0) return 1;

        int count = 0;
        for (Move move: board.getStoredPseudoLegalMoves()){
            if (board.checkLegalMoves(move, false)){
                count += countMovesX(depth - 1);
                board.undoMove();
            }
        }
        return count;
    }

    /**
     * A hashmap of each piece class to its corresponding material value.
     */
    private static final int[] pieceScores = {
        100,
        325,
        325,
        525,
        1000,
        0
    };

    private void orderMoves(List<Move> moveList){
        int[] scores = new int[moveList.size()];
        for (int i = 0; i < moveList.size(); i++) {
            int score = 0;
            Move move = moveList.get(i);
            Cell cell = move.cell();
            Piece p = move.p();

            if (cell.isHasPiece()){
                score = 10 * pieceScores[cell.getPiece().pieceNum] - pieceScores[p.pieceNum];
            }

            if (move instanceof PromotionMove){
                Class<?> promotionClass = ((PromotionMove) move).promotionClass;
                if (promotionClass == Knight.class) score += pieceScores[1];
                else if (promotionClass == Bishop.class) score += pieceScores[2];
                else if (promotionClass == Rook.class) score += pieceScores[3];
                else if (promotionClass == Queen.class) score += pieceScores[4];
            }
            scores[i] = score;
        }

        sort(moveList, scores);
    }
    
    private void sort(List<Move> moveList, int[] scores){
        for (int i = 0; i < moveList.size(); i++) {
            if (scores[i] > 0){
                Move move = moveList.get(i);
                int score = scores[i];
                //perform insertion sort on this element
                int j = i - 1;
                while (j >= 0 && scores[j] < score){
                    moveList.set(j + 1, moveList.get(j));
                    scores[j + 1] = scores[j];
                    j--;
                }
                moveList.set(j + 1, move);
                scores[j + 1] = score;
            }
        }
    }

    /**
     * Evaluates the current position, by taking into account the material value and position of the piece on each side.
     * Tries to encourage structural improvements in the opening, to focus on pawn structure.
     * @return The evaluation score of the current position.
     */
    public int evaluatePosition(){
        int evaluation = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece p = board.getCell(i, j).getPiece();
                if (p != null){
                    int score = 0;
                    score += pieceScores[p.pieceNum];
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

/*
        if (board.getFullMoveCounter() <= 8){
            Set<Piece> movedPieces = new HashSet<>(8);
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
*/

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
