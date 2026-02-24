package com.example.chessengine.UCI;

import java.util.List;

public class UndoMoveInfo {
    public final Move move;
    public final List<EnPassantMove> enPassantMoveList;
    public final List<CastlingMove> castlingMovesList;
    public final Class<?> captureClass;
    public final boolean captureCanCastle;
    public final boolean pieceCanCastle;
    public final int row;
    public final int col;
    public final int fiftyMoveCounter;
    public final int enPassantFile;
    public final boolean[] castlingState;

    public UndoMoveInfo(Move move, List<EnPassantMove> enPassantMoveList, List<CastlingMove> castlingMovesList, Piece capture, int fiftyMoveCounter, int enPassantFile, boolean[] castlingState) {
        this.move = move;
        this.enPassantMoveList = enPassantMoveList;
        this.castlingMovesList = castlingMovesList;
        row = move.p().getRow();
        col = move.p().getCol();
        this.fiftyMoveCounter = fiftyMoveCounter;

        pieceCanCastle = (move.p().getClass() == Rook.class && ((Rook) move.p()).isCanCastle()) ||  (move.p().getClass() == King.class && ((King) move.p()).isCanCastle());
        this.enPassantFile = enPassantFile;
        this.castlingState = castlingState;
        if (capture != null){
            captureClass = capture.getClass();
            captureCanCastle = capture.getClass() == Rook.class && ((Rook) capture).isCanCastle();
        } else{
            captureClass = null;
            captureCanCastle = false;
        }
    }

    @Override
    public String toString() {
        return "UndoMoveInfo{" +
                "move=" + move +
                ", enPassantMoveList=" + enPassantMoveList +
                ", castlingMovesList=" + castlingMovesList +
                ", captureClass=" + captureClass +
                ", captureCanCastle=" + captureCanCastle +
                ", pieceCanCastle=" + pieceCanCastle +
                ", row=" + row +
                ", col=" + col +
                '}';
    }
}
