package com.example.chessengine.UCI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * The board class will hold the positions of all the pieces, and be able to add, move and remove them.
 * It will also generate all the legal moves in the current position
 */
public class Board{
    /**
     * The array representation of the chessboard, where each cell can contain a piece
     */
    private final Cell[][] cells = new  Cell[8][8];

    /**
     * @return The current colour of which player's turn it is to move
     */
    public Colour getColourToMove() {
        return colourToMove;
    }

    /**
     * @param colourToMove The colour of what the current player's turn is being changed to
     */
    private void setColourToMove(Colour colourToMove) {
        this.colourToMove = colourToMove;
    }

    /**
     * Holds the current colour of which player's turn it is to move
     */
    private Colour colourToMove;

    /**
     * A list of all pseudolegal en passant moves in the current position, so they can easily be cleared once a move has been made
     */
    private final List<EnPassantMove> enPassantMoves = new ArrayList<>();

    public List<CastlingMove> getCastlingMoves() {
        return castlingMoves;
    }

    /**
     * A list of all the castling moves in the current position by the player whose turn it is, so they can more easily be validated if they are legal or not
     */
    private final List<CastlingMove> castlingMoves = new ArrayList<>();

    private final List<UndoMoveInfo> undoMoveInfoList = new ArrayList<>();

    /**
     * Constructor to create a new empty board, where white starts first
     */
    public Board() {
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                cells[i][j] = new Cell(i, j);
            }
        }
        colourToMove = Colour.WHITE;
    }

    /**
     * Constructor to create a new empty board, choosing who goes first
     */
    public Board(Colour colourToMove) {
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                cells[i][j] = new Cell(i, j);
            }
        }
        this.colourToMove = colourToMove;
    }

    /**
     * @param i The row of the cell starting from the left (0 to 7 inclusive)
     * @param j The column of the cell starting from the left (0 to 7 inclusive)
     * @return The cell at that position
     */
    public Cell getCell(int i, int j){
        return cells[i][j];
    }

    /**
     * sets the cell to contain the piece at the pieces position
     * @param p The piece to add to the board (The row and column it goes into should already be set)
     */
    public void addPiece(Piece p){
        int row = p.getRow();
        int col = p.getCol();

        cells[row][col].setPiece(p);
    }

    /**
     * @param p the piece to be removed
     */
    public void removePiece(Piece p){
        cells[p.getRow()][p.getCol()].setPiece(null);
    }

    /**
     * @param c the cell where the piece being removed is
     */
    public void removePiece(Cell c){
        c.setPiece(null);
    }

    /**
     * Moves a piece from one square to another using its position.
     * @param move The move containing the piece and cell it is moving to
     */
    public void movePiece(Move move){
        Piece p = move.p();
        undoMoveInfoList.add(new UndoMoveInfo(move, enPassantMoves, castlingMoves, move.cell().getPiece()));

        enPassantMoves.clear();

        if (move.getClass() == PromotionMove.class){
            //System.out.println("doing promotion move");
            cells[p.getRow()][p.getCol()].setPiece(null);
            move.cell().setPiece(((PromotionMove) move).getPromotionPiece());
            //System.out.println("finished promotion");
        } else {
            if (p.getClass() == Pawn.class){
                if (Math.abs(p.getRow() - move.cell().getRow()) == 2) {
                    if (move.cell().getCol() >= 1 &&
                            getCell(move.cell().getRow(), move.cell().getCol() - 1).getPiece() != null &&
                            getCell(move.cell().getRow(), move.cell().getCol() - 1).getPiece().getClass() == Pawn.class &&
                            getCell(move.cell().getRow(), move.cell().getCol() - 1).getPiece().getColour() != p.getColour()) {
                        enPassantMoves.add(new EnPassantMove((Pawn) getCell(move.cell().getRow(), move.cell().getCol() - 1).getPiece(),
                                getCell((move.cell().getRow() + move.p().getRow()) / 2, move.cell().getCol())));
                    }
                    if (move.cell().getCol() <= 6 &&
                            getCell(move.cell().getRow(), move.cell().getCol() + 1).getPiece() != null &&
                            getCell(move.cell().getRow(), move.cell().getCol() + 1).getPiece().getClass() == Pawn.class &&
                            getCell(move.cell().getRow(), move.cell().getCol() + 1).getPiece().getColour() != p.getColour()){
                        enPassantMoves.add(new EnPassantMove((Pawn) getCell(move.cell().getRow(), move.cell().getCol() + 1).getPiece(),
                                getCell((move.cell().getRow() + move.p().getRow()) / 2, move.cell().getCol())));
                    }
                }
            }
            move.cell().setPiece(p);
            cells[p.getRow()][p.getCol()].setPiece(null);
            p.move(move.cell().getRow(), move.cell().getCol());
            if (move.getClass() == EnPassantMove.class){
                EnPassantMove enPassantMove = (EnPassantMove) move;
                enPassantMove.getTargetPawnCell().setPiece(null);
            } else if (move.getClass() == CastlingMove.class) {
                CastlingMove castlingMove = (CastlingMove) move;
                Rook rook = castlingMove.getR();
                castlingMove.getRookCell().setPiece(rook);
                cells[rook.getRow()][rook.getCol()].setPiece(null);
                rook.move(castlingMove.getRookCell().getRow(), castlingMove.getRookCell().getCol());
            }
        }

        switch (colourToMove){
            case WHITE -> colourToMove = Colour.BLACK;
            case BLACK -> colourToMove = Colour.WHITE;
        }
    }

    /**
     * Collates all the pseudolegal moves from the piece son the board that are the same colour as the current colour to move.
     * It also adds all the castling moves into another list as well, so it is easier to check their legality later.
     * @return The list of pseudolegal moves of the current colour to move on the board
     */
    public List<Move> getPseudolegalMoves() {
        List<Move> moves = new ArrayList<>();
        castlingMoves.clear();
        for (Cell[] row : cells) {
            for (Cell cell : row) {
                if (cell.getPiece() != null && cell.getPiece().getColour() == getColourToMove()) {
                    Piece p = cell.getPiece();
                    if (p.getClass() == Pawn.class){
                        for (Cell moveCell: p.movesList){
                            if (moveCell.getRow() != 0 && moveCell.getRow() != 7) {
                                moves.add(new Move(p, moveCell));
                            } else {
                                moves.add(new PromotionMove((Pawn) p, moveCell, Rook.class));
                                moves.add(new PromotionMove((Pawn) p, moveCell, Queen.class));
                                moves.add(new PromotionMove((Pawn) p, moveCell, Bishop.class));
                                moves.add(new PromotionMove((Pawn) p, moveCell, Knight.class));
                            }
                        }
                    } else {
                        for (Cell moveCell: p.movesList){
                            moves.add(new Move(p, moveCell));
                        }
                    }
                    if (cell.getPiece().getClass() == King.class){
                        King king = (King) cell.getPiece();
                        if (king.isCanCastle()) {
                            Arrays.stream(cells).forEach(r -> {
                                Arrays.stream(r).filter(c -> {
                                    if (c.getPiece() != null && c.getPiece().getColour() == getColourToMove() && c.getPiece().getClass() == Rook.class){
                                        if (((Rook)c.getPiece()).isCanCastle()){
                                            for (int i = Math.min(c.getCol(), king.getCol()) + 1; i < Math.max(c.getCol(), king.getCol()); i++) {
                                                if (cells[king.getRow()][i].getPiece() != null){
                                                    return false;
                                                }
                                            }
                                            return true;
                                        }
                                    }
                                    return false;
                                }).forEach(c -> {
                                    CastlingMove cm = new CastlingMove(king, (Rook) c.getPiece());
                                    castlingMoves.add(cm);
                                    moves.add(cm);
                                });
                            });
                        }
                    }
                }
            }
        }
        for (Move move : enPassantMoves){
            if (move.p().getColour() == getColourToMove()){
                moves.add(move);
            }
        }
        return moves;
    }

    public void undoMove(){
        if (undoMoveInfoList.isEmpty()) throw new NullPointerException("No undoMoveInfo, as no move has been performed yet");
        UndoMoveInfo undoMoveInfo = undoMoveInfoList.removeLast();

        enPassantMoves.clear();
        enPassantMoves.addAll(undoMoveInfo.enPassantMoveList);

        castlingMoves.clear();
        castlingMoves.addAll(undoMoveInfo.castlingMovesList);
        
        if (undoMoveInfo.move.getClass() == EnPassantMove.class){
            EnPassantMove enPassantMove = (EnPassantMove) undoMoveInfo.move;
            cells[undoMoveInfo.row][undoMoveInfo.col].setPiece(enPassantMove.p());
            enPassantMove.cell().setPiece(null);
            enPassantMove.p().move(undoMoveInfo.row, undoMoveInfo.col);
            enPassantMove.getTargetPawnCell().setPiece(new Pawn(this, enPassantMove.getTargetPawnCell().getRow(), enPassantMove.getTargetPawnCell().getCol(), colourToMove));
        } else {
            Piece capturedPiece;
            if (undoMoveInfo.captureClass == Pawn.class) {
                capturedPiece = new Pawn(this, undoMoveInfo.move.cell().getRow(), undoMoveInfo.move.cell().getCol(), colourToMove);
            } else if (undoMoveInfo.captureClass == Rook.class) {
                capturedPiece = new Rook(this, undoMoveInfo.move.cell().getRow(), undoMoveInfo.move.cell().getCol(), colourToMove, undoMoveInfo.captureCanCastle);
            } else if (undoMoveInfo.captureClass == Knight.class) {
                capturedPiece = new Knight(this, undoMoveInfo.move.cell().getRow(), undoMoveInfo.move.cell().getCol(), colourToMove);
            } else if (undoMoveInfo.captureClass == Bishop.class) {
                capturedPiece = new Bishop(this, undoMoveInfo.move.cell().getRow(), undoMoveInfo.move.cell().getCol(), colourToMove);
            } else if (undoMoveInfo.captureClass == Queen.class) {
                capturedPiece = new Queen(this, undoMoveInfo.move.cell().getRow(), undoMoveInfo.move.cell().getCol(), colourToMove);
            } else {
                capturedPiece = null;
            }

            undoMoveInfo.move.cell().setPiece(capturedPiece);

            Piece movedPiece = undoMoveInfo.move.p();
            if (movedPiece.getClass() == Rook.class) {
                ((Rook) movedPiece).move(undoMoveInfo.row, undoMoveInfo.col, undoMoveInfo.pieceCanCastle);
            } else if (movedPiece.getClass() == King.class) {
                ((King) movedPiece).move(undoMoveInfo.row, undoMoveInfo.col, undoMoveInfo.pieceCanCastle);
            } else {
                movedPiece.move(undoMoveInfo.row, undoMoveInfo.col);
            }


            cells[undoMoveInfo.row][undoMoveInfo.col].setPiece(movedPiece);

            if (undoMoveInfo.move.getClass() == CastlingMove.class){
                CastlingMove castlingMove = (CastlingMove) undoMoveInfo.move;
                castlingMove.getRookCell().setPiece(null);
                Rook rook = castlingMove.getR();
                rook.move(undoMoveInfo.row, undoMoveInfo.col == 2 ? 0 : 7, true);
                cells[undoMoveInfo.row][undoMoveInfo.col == 2 ? 0 : 7].setPiece(rook);
            }
        }

        switch (colourToMove){
            case WHITE -> colourToMove = Colour.BLACK;
            case BLACK -> colourToMove = Colour.WHITE;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Board board = (Board) o;
        return Objects.deepEquals(cells, board.cells) && colourToMove == board.colourToMove && Objects.equals(enPassantMoves, board.enPassantMoves) && Objects.equals(castlingMoves, board.castlingMoves) && Objects.equals(undoMoveInfoList, board.undoMoveInfoList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Arrays.deepHashCode(cells), colourToMove);
    }
}