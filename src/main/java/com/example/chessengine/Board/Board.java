package com.example.chessengine.Board;

import com.example.chessengine.Board.Moves.CastlingMove;
import com.example.chessengine.Board.Moves.EnPassantMove;
import com.example.chessengine.Board.Moves.Move;
import com.example.chessengine.Board.Moves.PromotionMove;
import com.example.chessengine.Board.Pieces.*;

import java.util.*;

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
     * A list of all pseudo legal en passant moves in the current position, so they can easily be cleared once a move has been made
     */
    public final List<EnPassantMove> enPassantMoves = new ArrayList<>();

    /**
     * A list of all the castling moves in the current position by the player whose turn it is, so they can more easily be validated if they are legal or not
     */

    public final List<UndoMoveInfo> undoMoveInfoList = new ArrayList<>();

    public int getFiftyMoveCounter() {
        return fiftyMoveCounter;
    }

    private int fiftyMoveCounter = 0;

    public int getFullMoveCounter() {
        return fullMoveCounter;
    }

    private int fullMoveCounter = 1;

    public final HashMap<Long, Short> positionHistory = new HashMap<>();

    private boolean[] castlingState = new boolean[4];

    private final Zobrist zobrist = new Zobrist();

    public long getZobristKey() {
        return zobristKey;
    }

    private long zobristKey = 0;
    private int enPassantFile = 0;
    private int enPassantFileForFEN = 0;

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
        updateCastlingState();
        zobristKey = zobrist.zobristKey(this);
    }

    public static Board getStartPosition(){
        Board board = new Board();
        for (int col = 0; col < 8; col++) {
            board.addPiece(new Pawn(board, 1, col, Colour.WHITE));
        }
        board.addPiece(new Rook(board, 0, 0, Colour.WHITE, true));
        board.addPiece(new Rook(board, 0, 7, Colour.WHITE, true));

        board.addPiece(new Knight(board, 0, 1, Colour.WHITE));
        board.addPiece(new Knight(board, 0, 6, Colour.WHITE));

        board.addPiece(new Bishop(board, 0, 2, Colour.WHITE));
        board.addPiece(new Bishop(board, 0, 5, Colour.WHITE));

        board.addPiece(new Queen(board, 0, 3, Colour.WHITE));
        board.addPiece(new King(board, 0, 4, Colour.WHITE, true));

        for (int col = 0; col < 8; col++) {
            board.addPiece(new Pawn(board, 6, col, Colour.BLACK));
        }
        board.addPiece(new Rook(board, 7, 0, Colour.BLACK, true));
        board.addPiece(new Rook(board, 7, 7, Colour.BLACK, true));

        board.addPiece(new Knight(board, 7, 1, Colour.BLACK));
        board.addPiece(new Knight(board, 7, 6, Colour.BLACK));

        board.addPiece(new Bishop(board, 7, 2, Colour.BLACK));
        board.addPiece(new Bishop(board, 7, 5, Colour.BLACK));

        board.addPiece(new Queen(board, 7, 3, Colour.BLACK));
        board.addPiece(new King(board, 7, 4, Colour.BLACK, true));
        return board;
    }

    /**
     * Moves a piece from one square to another using its position.
     * @param move The move containing the piece and cell it is moving to
     */
    public void movePiece(Move move, boolean inSearch){
        boolean capture = move.cell().getPiece() != null;
        Piece p = move.p();
        UndoMoveInfo undoMoveInfo = new UndoMoveInfo(move, enPassantMoves, fiftyMoveCounter, enPassantFile, castlingState, enPassantFileForFEN, zobristKey);
        undoMoveInfoList.add(undoMoveInfo);

        enPassantFileForFEN = 0;

        if (enPassantFile != 0){
            zobristKey ^= zobrist.enPassantFile[enPassantFile];
            zobristKey ^= zobrist.enPassantFile[0];
            enPassantFile = 0;
        }

        if (p.getClass() == King.class && ((King) p).isCanCastle()){
            zobristKey ^= zobrist.castlingRights[getCastlingState()];
            if (p.getColour() == Colour.WHITE) {
                if (castlingState[1]) {
                    castlingState[1] = false;
                }
                if (castlingState[0]) {
                    castlingState[0] = false;
                }

            } else {
                if (castlingState[3]) {
                    castlingState[3] = false;
                }
                if (castlingState[2]) {
                    castlingState[2] = false;
                }
            }
            zobristKey ^= zobrist.castlingRights[getCastlingState()];
        } else if (p.getClass() == Rook.class && ((Rook) p).isCanCastle()){
            zobristKey ^= zobrist.castlingRights[getCastlingState()];
            if (p.getRow() == 0){
                if (p.getCol() == 0){
                    if (castlingState[1]){
                        castlingState[1] = false;
                    }
                } else if (p.getCol() == 7) {
                    if (castlingState[0]) {
                        castlingState[0] = false;
                    }
                }
            } else if (p.getRow() == 7) {
                if (p.getCol() == 0){
                    if (castlingState[3]) {
                        castlingState[3] = false;
                    }
                } else if (p.getCol() == 7) {
                    if (castlingState[2]) {
                        castlingState[2] = false;
                    }
                }
            }
            zobristKey ^= zobrist.castlingRights[getCastlingState()];
        }

        fiftyMoveCounter++;

        enPassantMoves.clear();

        if (move.getClass() == PromotionMove.class){
            cells[p.getRow()][p.getCol()].setPiece(null);
            move.cell().setPiece(((PromotionMove) move).getPromotionPiece());
        } else {
            if (p.getClass() == Pawn.class){
                if (Math.abs(p.getRow() - move.cell().getRow()) == 2) {
                    enPassantFileForFEN = p.getCol() + 1;
                    if (move.cell().getCol() >= 1 &&
                            getCell(move.cell().getRow(), move.cell().getCol() - 1).getPiece() != null &&
                            getCell(move.cell().getRow(), move.cell().getCol() - 1).getPiece().getClass() == Pawn.class &&
                            getCell(move.cell().getRow(), move.cell().getCol() - 1).getPiece().getColour() != p.getColour()) {
                        enPassantMoves.add(new EnPassantMove((Pawn) getCell(move.cell().getRow(), move.cell().getCol() - 1).getPiece(),
                                getCell((move.cell().getRow() + move.p().getRow()) / 2, move.cell().getCol())));
                        enPassantFile = move.cell().getCol() + 1;
                        zobristKey ^= zobrist.enPassantFile[0];
                        zobristKey ^= zobrist.enPassantFile[enPassantFile];
                    }
                    if (move.cell().getCol() <= 6 &&
                            getCell(move.cell().getRow(), move.cell().getCol() + 1).getPiece() != null &&
                            getCell(move.cell().getRow(), move.cell().getCol() + 1).getPiece().getClass() == Pawn.class &&
                            getCell(move.cell().getRow(), move.cell().getCol() + 1).getPiece().getColour() != p.getColour()){
                        enPassantMoves.add(new EnPassantMove((Pawn) getCell(move.cell().getRow(), move.cell().getCol() + 1).getPiece(),
                                getCell((move.cell().getRow() + move.p().getRow()) / 2, move.cell().getCol())));
                        if (enPassantFile == 0){
                            enPassantFile = move.cell().getCol() + 1;
                            zobristKey ^= zobrist.enPassantFile[0];
                            zobristKey ^= zobrist.enPassantFile[enPassantFile];
                        }
                    }
                }
            }

            zobristKey ^= zobrist.pieces[zobrist.pieceMap.get(p.getClass())][colourToMove == Colour.WHITE ? 0: 1][p.getRow() * 8 + p.getCol()];
            if (move.cell().getPiece() != null) zobristKey ^= zobrist.pieces[zobrist.pieceMap.get(move.cell().getPiece().getClass())][colourToMove == Colour.WHITE ? 1: 0][move.cell().getRow() * 8 + move.cell().getCol()];
            zobristKey ^= zobrist.pieces[zobrist.pieceMap.get(p.getClass())][colourToMove == Colour.WHITE ? 0: 1][move.cell().getRow() * 8 + move.cell().getCol()];

            move.cell().setPiece(p);
            cells[p.getRow()][p.getCol()].setPiece(null);
            p.move(move.cell().getRow(), move.cell().getCol());
            if (move.getClass() == EnPassantMove.class){
                EnPassantMove enPassantMove = (EnPassantMove) move;
                zobristKey ^= zobrist.pieces[zobrist.pieceMap.get(Pawn.class)][colourToMove == Colour.WHITE ? 1: 0][enPassantMove.getTargetPawnCell().getRow() * 8 + enPassantMove.getTargetPawnCell().getCol()];
                enPassantMove.getTargetPawnCell().setPiece(null);
            } else if (move.getClass() == CastlingMove.class) {
                CastlingMove castlingMove = (CastlingMove) move;
                Rook rook = castlingMove.getR();
                zobristKey ^= zobrist.pieces[zobrist.pieceMap.get(Rook.class)][colourToMove == Colour.WHITE ? 0: 1][rook.getRow() * 8 + rook.getCol()];
                zobristKey ^= zobrist.pieces[zobrist.pieceMap.get(Rook.class)][colourToMove == Colour.WHITE ? 0: 1][castlingMove.getRookCell().getRow() * 8 + castlingMove.getRookCell().getCol()];
                castlingMove.getRookCell().setPiece(rook);
                cells[rook.getRow()][rook.getCol()].setPiece(null);
                rook.move(castlingMove.getRookCell().getRow(), castlingMove.getRookCell().getCol());
            }
        }

        switch (colourToMove){
            case WHITE -> colourToMove = Colour.BLACK;
            case BLACK -> {
                colourToMove = Colour.WHITE;
                fullMoveCounter++;
            }
        }

        zobristKey ^= zobrist.blackToMove;
        if (!inSearch) {
            if (p.getClass() == Pawn.class || capture){
                fiftyMoveCounter = 0;
            }
            positionHistory.merge(zobristKey, (short) 1, (i, j) -> (short) (i + j));
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
                    Piece p = cell.getPiece();
                    if (p.getClass() == Pawn.class){
                        for (Cell moveCell: p.getMovesList()){
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
                        for (Cell moveCell: p.getMovesList()){
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

        enPassantFileForFEN = undoMoveInfo.enPassantFileForFEN;
        enPassantFile = undoMoveInfo.enPassantFile;
        fiftyMoveCounter = undoMoveInfo.fiftyMoveCounter;
        enPassantMoves.clear();
        enPassantMoves.addAll(undoMoveInfo.enPassantMoveList);
        castlingState = undoMoveInfo.castlingState;
        zobristKey = undoMoveInfo.zobristKey;

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
                rook.move(undoMoveInfo.row, rook.getCol() == 3 ? 0 : 7, true);
                cells[undoMoveInfo.row][rook.getCol()].setPiece(rook);
            }
        }


        switch (colourToMove){
            case WHITE -> {
                colourToMove = Colour.BLACK;
                fullMoveCounter--;
            }
            case BLACK -> colourToMove = Colour.WHITE;
        }
    }

    public boolean isInCheck(){
        Cell kingCell = null;
        boolean breakLoop = false;

        for (Cell[] row: cells){
            for (Cell cell: row){
                if (cell.getPiece() != null && cell.getPiece().getClass() == King.class && cell.getPiece().getColour() == getColourToMove()){
                    kingCell = cell;
                    breakLoop = true;
                    break;
                }
            }
            if (breakLoop) break;
        }

        if (kingCell == null){
            throw new NullPointerException("no king cell");
        }

        for (Cell[] row: cells){
            for (Cell cell: row){
                if (cell.getPiece() != null && cell.getPiece().getColour() != colourToMove){
                    if (cell.getPiece().getMovesList().contains(kingCell)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean checkLegalMoves(Move move){
        Cell stepOverCell = null;
        if (move.getClass() == CastlingMove.class)stepOverCell = cells[move.cell().getRow()][(move.cell().getCol() == 2) ? 3: 5];

        movePiece(move, true);

        Cell kingCell = null;
        boolean breakLoop = false;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Cell cell = cells[i][j];
                if (cell.getPiece() != null && cell.getPiece().getClass() == King.class && cell.getPiece().getColour() != colourToMove){
                    kingCell = cell;
                    breakLoop = true;
                    break;
                }
            }
            if (breakLoop) break;
        }

        List<Move> pseudoLegalMoves = getPseudolegalMoves();

        for (Move nextMove: pseudoLegalMoves){
            if (nextMove.cell() == kingCell) {
                undoMove();
                return false;
            }
        }
        if (move.getClass() == CastlingMove.class){
            for (Move nextMove: pseudoLegalMoves){
                if (nextMove.cell() == stepOverCell) {
                    undoMove();
                    return false;
                }
            }
        }
        undoMove();
        return true;
    }

    private void updateCastlingState(){
        castlingState = new boolean[4];
        if (cells[0][4].getPiece() != null && cells[0][4].getPiece().getClass() == King.class && cells[0][4].getPiece().getColour() == Colour.WHITE){
            if (cells[0][0].getPiece() != null && cells[0][0].getPiece().getClass() == Rook.class && cells[0][0].getPiece().getColour() == Colour.WHITE) castlingState[1] =  true;
            if (cells[0][7].getPiece() != null && cells[0][7].getPiece().getClass() == Rook.class && cells[0][7].getPiece().getColour() == Colour.WHITE) castlingState[0] = true;
        }
        if (cells[7][4].getPiece() != null && cells[7][4].getPiece().getClass() == King.class && cells[7][4].getPiece().getColour() == Colour.BLACK){
            if (cells[7][0].getPiece() != null && cells[7][0].getPiece().getClass() == Rook.class && cells[7][0].getPiece().getColour() == Colour.BLACK) castlingState[3] = true;
            if (cells[7][7].getPiece() != null && cells[7][7].getPiece().getClass() == Rook.class && cells[7][7].getPiece().getColour() == Colour.BLACK) castlingState[2] = true;
        }
    }

    public int getCastlingState(){
        int state = 0;
        for (boolean b: castlingState){
            if (b) state++;
            state = state << 1;
        }
        state = state >> 1;
        return state;
    }

    public String getFEN(){
        StringBuilder fen = new StringBuilder();
        //board position
        for (int i = cells.length - 1; i >= 0; i--) {
            int emptyCounter = 0;
            for (Cell cell: cells[i]){
                if (cell.getPiece() == null) emptyCounter++;
                else {
                    if (emptyCounter != 0) fen.append(emptyCounter);
                    emptyCounter = 0;
                    fen.append(cell.getPiece().getColour() == Colour.WHITE ? whitePieceToNotation.get(cell.getPiece().getClass()): blackPieceToNotation.get(cell.getPiece().getClass()));

                }
            }
            if (emptyCounter != 0) fen.append(emptyCounter);
            fen.append("/");
        }
        fen.deleteCharAt(fen.length() - 1);
        fen.append("_");
        //colour to move
        fen.append(colourToMove == Colour.WHITE ? "w": "b");
        fen.append("_");
        //castling rights
        String cs = "";
        if (castlingState[0]) cs += "K";
        if (castlingState[1]) cs += "Q";
        if (castlingState[2]) cs += "k";
        if (castlingState[3]) cs += "q";
        if (cs.isEmpty()) fen.append("-");
        else fen.append(cs);
        fen.append("_");
        //en passant target square
        if (enPassantFileForFEN == 0){
            fen.append("-");
        }else {
            fen.append(fileNumberToLetter.get(enPassantFileForFEN));
            fen.append(colourToMove == Colour.BLACK ? 3: 6);
        }
        fen.append("_");
        //half move clock
        fen.append(fiftyMoveCounter);
        fen.append("_");
        //full move clock
        fen.append(fullMoveCounter);

        return fen.toString();
    }

    public static final HashMap<Integer, Character> fileNumberToLetter = new HashMap<>(){
        {
            put(1, 'a');
            put(2, 'b');
            put(3, 'c');
            put(4, 'd');
            put(5, 'e');
            put(6, 'f');
            put(7, 'g');
            put(8, 'h');
        }
    };

    public static Character getFileNumberToLetter(int i){
        return fileNumberToLetter.get(i);
    }

    private static final HashMap<Class<?>, Character> whitePieceToNotation = new HashMap<>(){
        {
            put(King.class, 'K');
            put(Queen.class, 'Q');
            put(Rook.class, 'R');
            put(Bishop.class, 'B');
            put(Knight.class, 'N');
            put(Pawn.class, 'P');
        }
    };

    public static final HashMap<Class<?>, Character> blackPieceToNotation = new HashMap<>(){
        {
            put(King.class, 'k');
            put(Queen.class, 'q');
            put(Rook.class, 'r');
            put(Bishop.class, 'b');
            put(Knight.class, 'n');
            put(Pawn.class, 'p');
        }
    };

    public static Character getBlackPieceToNotation(Class<?> pieceClass){
        return blackPieceToNotation.get(pieceClass);
    }

    public int getPieceCount(){
        int count = 0;
        for (Cell[] row: cells){
            for (Cell cell: row){
                if (cell.getPiece() != null) count++;
            }
        }
        return count;
    }

    @Override
    public int hashCode() {
        return Objects.hash(Arrays.deepHashCode(cells), colourToMove);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Board board)) return false;
        return fiftyMoveCounter == board.fiftyMoveCounter && fullMoveCounter == board.fullMoveCounter && zobristKey == board.zobristKey && Objects.equals(positionHistory, board.positionHistory);
    }
}