package com.example.chessengine.Board;

import com.example.chessengine.Board.Moves.EnPassantMove;
import com.example.chessengine.Board.Moves.Move;
import com.example.chessengine.Board.Pieces.King;
import com.example.chessengine.Board.Pieces.Piece;
import com.example.chessengine.Board.Pieces.Rook;

import java.util.List;

/**
 * UndoMoveInfo is the class that holds all the information needed to revert
 * the board to the position before the move given.
 */
public class UndoMoveInfo {
    /**
     * The move being reversed
     */
    public final Move move;

    /**
     * The list of current en passant moves
     */
    public final List<EnPassantMove> enPassantMoveList;

    /**
     * The class of the captured piece (null if none)
     */
    public final Class<?> captureClass;

    /**
     * if the capture can castle
     */
    public final boolean captureCanCastle;

    /**
     * if the piece moved can castle
     */
    public final boolean pieceCanCastle;

    /**
     * The row the piece is moved from
     */
    public final int row;

    /**
     * The column the piece is moved from
     */
    public final int col;

    /**
     * The half move clock
     */
    public final int fiftyMoveCounter;

    /**
     * The en passant file for the zobrist key (0 for none, or 1-8)
     */
    public final int enPassantFile;

    /**
     * The current castling state
     */
    public final boolean[] castlingState;

    /**
     *  The file of the pawn just moved (0 for none, or 1-8)
     */
    public final int enPassantFileForFEN;

    /**
     * The zobrist hash code of the current position
     */
    public final long zobristKey;

    /**
     * The constructor to create a new undoMoveInfo to store the information to reverse a move.
     * @param move The move being reversed
     * @param enPassantMoveList The list of current en passant moves
     * @param fiftyMoveCounter The half move clock
     * @param enPassantFile The en passant file for the zobrist key (0 for none, or 1-8)
     * @param castlingState The current castling state
     * @param enPassantFileForFEN The file of the pawn just moved (0 for none, or 1-8)
     * @param zobristKey The zobrist hash code of the current position
     */
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
