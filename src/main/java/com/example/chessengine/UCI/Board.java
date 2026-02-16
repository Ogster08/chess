package com.example.chessengine.UCI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    private final List<Move> enPassantMoves = new ArrayList<>();

    /**
     * A list of all the castling moves in the current position by the player whose turn it is, so they can more easily be validated if they are legal or not
     */
    private final List<CastlingMove> castlingMoves = new ArrayList<>();

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
        enPassantMoves.clear();
        Piece p = move.p();

        if (p.getClass() == Pawn.class){
            if (Math.abs(p.getRow() - move.cell().getRow()) == 2) {
                if (move.cell().getCol() >= 1 && getCell(move.cell().getRow(), move.cell().getCol() - 1).getPiece() != null && getCell(move.cell().getRow(), move.cell().getCol() - 1).getPiece().getClass() == Pawn.class && getCell(move.cell().getRow(), move.cell().getCol() - 1).getPiece().getColour() != p.getColour()) {
                    enPassantMoves.add(new EnPassantMove((Pawn) getCell(move.cell().getRow(), move.cell().getCol() - 1).getPiece(), getCell((move.cell().getRow() + move.p().getRow()) / 2, move.cell().getCol())));
                }
                if (move.cell().getCol() <= 6 && getCell(move.cell().getRow(), move.cell().getCol() + 1).getPiece() != null && getCell(move.cell().getRow(), move.cell().getCol() + 1).getPiece().getClass() == Pawn.class && getCell(move.cell().getRow(), move.cell().getCol() + 1).getPiece().getColour() != p.getColour()){
                    enPassantMoves.add(new EnPassantMove((Pawn) getCell(move.cell().getRow(), move.cell().getCol() + 1).getPiece(), getCell((move.cell().getRow() + move.p().getRow()) / 2, move.cell().getCol())));
                }
            }
        }
        move.cell().setPiece(p);
        cells[p.getRow()][p.getCol()].setPiece(null);
        p.move(move.cell().getRow(), move.cell().getCol());
        if (move.getClass() == EnPassantMove.class){
            EnPassantMove enPassantMove = (EnPassantMove) move;
            enPassantMove.targetPawnCell().setPiece(null);
        } else if (move.getClass() == CastlingMove.class) {
            CastlingMove castlingMove = (CastlingMove) move;
            Rook rook = castlingMove.getR();
            castlingMove.getRookCell().setPiece(rook);
            cells[rook.getRow()][rook.getCol()].setPiece(null);
            rook.move(castlingMove.getRookCell().getRow(), castlingMove.getRookCell().getCol());
        }
    }

    /**
     * Collates all the pseudolegal moves from the piece son the board that are the same colour as the current colour to move.
     * It also adds all the castling moves into another list as well, so it is easier to check their legality later.
     * @return The list of pseudolegal moves of the current colour to move on the board
     */
    public List<Move> getPseudolegalMoves() {
        List<Move> moves = new ArrayList<>();
        for (Cell[] row : cells) {
            for (Cell cell : row) {
                if (cell.getPiece() != null && cell.getPiece().getColour() == getColourToMove()) {
                    moves.add(new Move(cell.getPiece(), cell));
                    if (cell.getPiece().getClass() == King.class){
                        King king = (King) cell.getPiece();
                        if (king.canCastle) {
                            Arrays.stream(cells).forEach(r -> {
                                Arrays.stream(r).filter(c -> {
                                    if (c.getPiece() != null && c.getPiece().getColour() == getColourToMove() && c.getPiece().getClass() == Rook.class){
                                        Rook rook = (Rook) c.getPiece();
                                        return rook.isCanCastle();
                                    }
                                    return false;
                                }).forEach(c -> {
                                    castlingMoves.add(new CastlingMove(king, (Rook) c.getPiece()));
                                    moves.add(new CastlingMove(king, (Rook) c.getPiece()));
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
}
