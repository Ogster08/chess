package com.example.chessengine.Board;

import com.example.chessengine.Board.Pieces.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Zobrist {
    public final long[][][] pieces = new long[6][2][64];
    public final long[] castlingRights = new long[16];
    public final long[] enPassantFile = new long[9]; // 0 for no en passant
    public final long blackToMove;



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
