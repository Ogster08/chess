package com.example.chessengine.Tablebase;

/**
 * Record to hold the move information for the mainline in the Lichess API repsonse.
 * @param uci The uci move notation.
 * @param san The san move notation.
 * @param dtz moves until capture or pawn move.
 * @param precise_dtz moves until capture or pawn move - aims for fastest win, while guaranteeing the 50-move rule isn't violated.
 */
public record LichessMoveData(String uci, String san, int dtz, int precise_dtz) {
}
