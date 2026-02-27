package com.example.chessengine.Tablebase;

import com.example.chessengine.Board.*;
import com.example.chessengine.Board.Moves.Move;
import com.example.chessengine.Board.Moves.PromotionMove;
import com.example.chessengine.Board.Pieces.Piece;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * The LichessAPI class is for dealing with an API call to the Lichess tablebase and parsing the json to get the first move in the mainline.
 */
public final class LichessAPI {

    /**
     * Private constructor. Instantiation isn't needed.
     */
    private LichessAPI() {}

    /**
     * Does an API call to the Lichess database, getting the result and returning null if the request wasn't successful.
     * Parses the successful result into a LichessResponse object.
     * Gets the first uci move from the mainline.
     * Finds the appropriate legal Move object.
     * @param board The board, where the position is coming from, and to map the uci move to a legal Move object.
     * @return The first move from the mainline from the tablebase of the current position - null if no move can be found or the position is drawing.
     */
    public static Move getMove(Board board){
        String url = "http://tablebase.lichess.ovh/standard/mainline?fen=" + board.getFEN();
        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) return null;

            LichessResponse lichessResponse = getObjectFromJSON(response.body());
            if (lichessResponse.winner() == null) return null;
            else {
                return getMoveFromUci(lichessResponse.mainline().getFirst().uci(), board);
            }

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Uses Jackson databind to map the given json to a Lichess Response object.
     * @param json The json string to parse to a LichessResponse object.
     * @return The LichessResponse Object the json string was parsed to.
     * @throws JsonProcessingException Error if the parsing fails.
     */
    private static LichessResponse getObjectFromJSON(String json) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, LichessResponse.class);
    }

    /**
     * Maps the uci move to a legal move for the current move position.
     * @param uci The uci move string.
     * @param board The board the Move object is coming from.
     * @return The legal Move object the uci move is equivalent to.
     * @throws RuntimeException Error if it can't find an appropriate move.
     */
    private static Move getMoveFromUci(String uci, Board board){
        for (Move testMove: board.getPseudolegalMoves()){
            if (!board.checkLegalMoves(testMove)) continue;

            Cell cell = testMove.cell();
            Piece p = testMove.p();
            if (Board.getFileNumberToLetter(p.getCol() + 1) == uci.charAt(0) &&
                p.getRow() + 1 == Integer.parseInt(String.valueOf(uci.charAt(1))) &&
                Board.getFileNumberToLetter(cell.getCol() + 1) == uci.charAt(2) &&
                cell.getRow() + 1 == Integer.parseInt(String.valueOf(uci.charAt(3)))){
                if (uci.length() == 5){
                    if (testMove.getClass() != PromotionMove.class) continue;
                    if (Board.getBlackPieceToNotation(((PromotionMove) testMove).promotionClass) == uci.charAt(4)) return testMove;
                }
                else return testMove;
            }
        }
        throw new RuntimeException("can't find valid move for uci: " + uci);
    }
}
