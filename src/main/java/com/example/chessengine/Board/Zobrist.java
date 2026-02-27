package com.example.chessengine.Board;

import com.example.chessengine.Board.Pieces.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Zobrist is a class used to create a zobrist hash code of the current position.
 * It also contains the pseudorandom numbers to incrementally change the hash code.
 */
public class Zobrist {
    /**
     * The pseudorandom numbers for each piece of each colour in each position.
     */
    public final long[][][] pieces = new long[6][2][64];

    /**
     * The pseudorandom number for each castling right - it has 16 numbers for faster indexing.
     */
    public final long[] castlingRights = new long[16];

    /**
     * The pseudorandom numbers for each rank when an en passant is possible - 0 for none, otherwise 1-8.
     */
    public final long[] enPassantFile = new long[9]; // 0 for no en passant

    /**
     * The pseudorandom number used to show it is black to move
     */
    public final long blackToMove;


    /**
     * The constructor for a new Zobrist object.
     * It initialises all the pseudorandom numbers using the same seed each time, for consistency.
     */
    public Zobrist(){
        Random random = new Random(43587692);
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 2; j++) {
                for (int k = 0; k < 64; k++) {
                    pieces[i][j][k] = random.nextLong();
                }
            }
        }
        for (int i = 0; i < 16; i++) {
            castlingRights[i] = random.nextLong();
        }
        for (int i = 0; i < 9; i++) {
            enPassantFile[i] = random.nextLong();
        }
        blackToMove = random.nextLong();
    }

    /**
     * A hashmap, mapping each piece class to its corresponding index in the pieces array.
     */
    public final Map<Class<?>, Integer> pieceMap = new HashMap<>(){
        {
            put(Pawn.class, 0);
            put(Knight.class, 1);
            put(Bishop.class, 2);
            put(Rook.class, 3);
            put(Queen.class, 4);
            put(King.class, 5);
        }
    };

    /**
     * Creates a new zobrist hash code from scratch from the board object passed in.
     * @param board The board object the zobrist hash code is being created for.
     * @return The zobrist hash code of the current position in the board.
     */
    public long zobristKey(Board board){
        long zobrist = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board.getCell(i, j).getPiece() != null){
                    Piece p = board.getCell(i, j).getPiece();
                    zobrist ^= pieces[pieceMap.get(p.getClass())][p.getColour() == Colour.WHITE ? 0: 1][i * 8 + j];
                }
            }
        }

        zobrist ^= castlingRights[board.getCastlingState()];

        if (!board.enPassantMoves.isEmpty()) zobrist ^= enPassantFile[board.enPassantMoves.getFirst().getTargetPawnCell().getCol() + 1];

        if (board.getColourToMove() == Colour.BLACK) zobrist ^= blackToMove;

        return zobrist;
    }
}
