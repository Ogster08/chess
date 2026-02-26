package com.example.chessengine.Board;

import com.example.chessengine.Board.Moves.CastlingMove;
import com.example.chessengine.Board.Moves.EnPassantMove;
import com.example.chessengine.Board.Moves.Move;
import com.example.chessengine.Board.Pieces.King;
import com.example.chessengine.Board.Pieces.Piece;
import com.example.chessengine.Board.Pieces.Rook;

import java.util.List;

public class UndoMoveInfo {
    public final Move move;
    public final List<EnPassantMove> enPassantMoveList;
    public final Class<?> captureClass;
    public final boolean captureCanCastle;
    public final boolean pieceCanCastle;
    public final int row;
    public final int col;
    public final int fiftyMoveCounter;
    public final int enPassantFile;
    public final boolean[] castlingState;
    public final int enPassantFileForFEN;
    public final long zobristKey;

    public UndoMoveInfo(Move move, List<EnPassantMove> enPassantMoveList, int fiftyMoveCounter, int enPassantFile, boolean[] castlingState, int enPassantFileForFEN, long zobristKey) {
        this.move = move;
        this.enPassantMoveList = enPassantMoveList;
        row = move.p().getRow();
        col = move.p().getCol();
        this.fiftyMoveCounter = fiftyMoveCounter;

        pieceCanCastle = (move.p().getClass() == Rook.class && ((Rook) move.p()).isCanCastle()) ||  (move.p().getClass() == King.class && ((King) move.p()).isCanCastle());
        this.enPassantFile = enPassantFile;
        this.castlingState = castlingState;
        this.enPassantFileForFEN = enPassantFileForFEN;
        this.zobristKey = zobristKey;

        Piece capture = move.cell().getPiece();
        if (capture != null){
            captureClass = capture.getClass();
            captureCanCastle = capture.getClass() == Rook.class && ((Rook) capture).isCanCastle();
        } else{
            captureClass = null;
            captureCanCastle = false;
        }
    }
}
