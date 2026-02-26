package com.example.chessengine.tablebase;

import com.example.chessengine.UCI.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class LichessAPI {

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

    private static LichessResponse getObjectFromJSON(String json) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, LichessResponse.class);
    }

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
