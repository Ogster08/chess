package com.example.chessengine.Book;

import com.example.chessengine.Board.*;
import com.example.chessengine.Board.Moves.CastlingMove;
import com.example.chessengine.Board.Moves.Move;
import com.example.chessengine.Board.Moves.PromotionMove;
import com.example.chessengine.Board.Pieces.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;

/**
 * The BookCreator class is used for creating an openings book from a text file containing PGN games.
 * It also can load the openings book data from 'book.txt' into a Book object.
 */
public class BookCreator {
    /**
     * The maximum ply (half moves) to look at for each game.
     */
    private final int maxPly = 10;

    /**
     * The openings book, the PGN games are being loaded into.
     */
    private final Book book = new Book();

    /**
     * Creates an openings book from the 'PGN games.txt' file.
     * Creates or overwrites the 'book.txt' file, with the information to reconstruct the openings book.
     */
    public void createBook() {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader("src/main/resources/book/PGN games.txt"))) {
            String line = bufferedReader.readLine();
            while (line != null){
                line = line.trim();
                if (line.startsWith("[") || line.isEmpty()) {
                    line = bufferedReader.readLine();
                    continue;
                }
                line = line.replaceAll("\\d+\\.\\s", "");
                line = line.replaceAll("\\{[^}]*}", "");
                System.out.println(line);
                doGame(line);
                line = bufferedReader.readLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        createBookFile();
    }

    /**
     * Writes all the positions and openings from the openings book object into a 'book.txt' file.
     */
    private void createBookFile() {
        try (PrintWriter printWriter = new PrintWriter("src/main/resources/book/book.txt", StandardCharsets.UTF_8)) {
            for (long position: book.bookPositions.keySet()){
                StringBuilder sb = new StringBuilder();
                sb.append(position);
                sb.append(": ");
                for (BookMove bookMove: book.bookPositions.get(position).MovesWithNumPlayed.keySet()){
                    sb.append(bookMove.toString());
                    sb.append(" ");
                    sb.append(book.bookPositions.get(position).MovesWithNumPlayed.get(bookMove));
                    sb.append(", ");
                }
                sb.delete(sb.length() - 2, sb.length());
                printWriter.println(sb);
            }
            System.out.println("done");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * A hashmap of all the file letters to the corresponding column index.
     */
    private static final HashMap<Character, Integer> fileToCol = new HashMap<>(){
        {
            put('a', 0);
            put('b', 1);
            put('c', 2);
            put('d', 3);
            put('e', 4);
            put('f', 5);
            put('g', 6);
            put('h', 7);
        }
    };

    /**
     * A hashmap of each piece character in san to the corresponding class
     */
    private static final HashMap<Character, Class<?>> pieceToClass = new HashMap<>(){
        {
            put('K', King.class);
            put('Q', Queen.class);
            put('R', Rook.class);
            put('B', Bishop.class);
            put('N', Knight.class);
        }
    };

    /**
     * Goes through the san moves from the game line, upto the maxPly.
     * Adds the next move, at the current board position to the openings book for each move.
     * @param line The game line from the PGN games.
     */
    private void doGame(String line){
        String[] lineSplit = line.split(" ");
        if (lineSplit.length <= 20) return;
        Board board = Board.getStartPosition();
        for (int i = 0; i < lineSplit.length; i++) {
            if (i == maxPly) break;

            String algebraicMove = lineSplit[i].trim();
            if (algebraicMove.contains(".") || algebraicMove.equals("1/2-1/2") || algebraicMove.equals("1-0") || algebraicMove.equals("0-1")) continue;

            algebraicMove = algebraicMove.replace("x", "").replace("#", "").replace("+", "").replace("-", "");
            Move move = getMove(board, algebraicMove);
            book.updateBook(new BookMove(move.cell().getRow(), move.cell().getCol(), move.p().getRow(), move.p().getCol()), board.getZobristKey());

            board.movePiece(move, false);
        }
    }

    /**
     * Maps the san move to the equivalent move object from the legal moves in the board.
     * Throws an error if no move can be found.
     * @param board The board, where the move should come from
     * @param algebraicMove The san move, the move is coming from
     * @return The Move object of the san move at the current board position
     * @throws RuntimeException error if the san move doesn't correspond to a legal move in the current position.
     */
    private Move getMove(Board board, String algebraicMove){
        for (Move testMove: board.getPseudolegalMoves()){
            if (!board.checkLegalMoves(testMove)) continue;
            if (algebraicMove.equals("OO") && testMove.getClass() == CastlingMove.class){
                if (testMove.cell().getCol() == 6) return testMove;
            }else if (algebraicMove.equals("OOO") && testMove.getClass() == CastlingMove.class){
                if (testMove.cell().getCol() == 2) return testMove;
            }
            // pawn move
            else if (fileToCol.containsKey(algebraicMove.charAt(0))) {
                if (testMove.p().getClass() != Pawn.class) continue;
                if (testMove.p().getCol() == fileToCol.get(algebraicMove.charAt(0))){
                    if (algebraicMove.contains("=") ){
                        if (testMove.cell().getRow() == 0 || testMove.cell().getRow() == 7) {
                            if (algebraicMove.length() == 5 && testMove.cell().getCol() != fileToCol.get(algebraicMove.charAt(1))) continue;
                            if (((PromotionMove) testMove).promotionClass == pieceToClass.get(algebraicMove.charAt(algebraicMove.length() - 1))) continue;

                            return testMove;
                        }
                    } else {
                        if (algebraicMove.length() == 2){
                            if ((testMove.cell().getRow() + 1 + "").equals(Character.toString(algebraicMove.charAt(1))) && testMove.cell().getCol() == fileToCol.get(algebraicMove.charAt(0))) return testMove;
                        } else {
                            if ((testMove.cell().getRow() + 1 + "").equals(Character.toString(algebraicMove.charAt(2))) && testMove.cell().getCol() == fileToCol.get(algebraicMove.charAt(1))) return testMove;
                        }
                    }
                }
            }
            // normal other piece move
            else {
                if (testMove.p().getClass() != pieceToClass.get(algebraicMove.charAt(0))) continue;
                if (fileToCol.get(algebraicMove.charAt(algebraicMove.length() - 2)) == testMove.cell().getCol()){
                    if (Character.toString(algebraicMove.charAt(algebraicMove.length() - 1)).equals(testMove.cell().getRow() + 1 + "")){
                        if (algebraicMove.length() == 4){
                            //check if file or rank disambiguation
                            if (fileToCol.containsKey(algebraicMove.charAt(1))){
                                if (testMove.p().getCol() != fileToCol.get(algebraicMove.charAt(1))) continue;
                            } else {
                                if (!(testMove.p().getRow() + 1 + "").equals(Character.toString(algebraicMove.charAt(1)))) continue;
                            }
                        }

                        return testMove;
                    }
                }
            }

        }
        throw new RuntimeException("No valid move found for: " + algebraicMove);
    }

    /**
     * Loads the openings book from 'book.txt' into a Book object.
     * @return The openings book from the 'book.txt' file.
     */
    public static Book LoadBook(){
        Book book = new Book();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader("src/main/resources/book/book.txt"))) {
            String line = bufferedReader.readLine();
            while (line != null){
                long position = Long.parseLong(line.split(":")[0]);
                String[] bookMoveStrings = line.split(":")[1].trim().split(",");
                for (String bookMove: bookMoveStrings){
                    bookMove = bookMove.trim();
                    if (!bookMove.isEmpty()){
                        Integer[] data = Arrays.stream(bookMove.replace("[", "").replace("]", "").split(" ")).map(Integer::parseInt).toArray(Integer[]::new);
                        book.updateBook(new BookMove(data[0], data[1], data[2], data[3]), position, data[4]);
                    }

                }
                line = bufferedReader.readLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return book;
    }
}
