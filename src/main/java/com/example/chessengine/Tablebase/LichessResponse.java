package com.example.chessengine.Tablebase;

import java.util.List;

/**
 * @param mainline dtz and precise dtz mainline (list of LichessMoveData objects) or empty if drawn
 * @param winner (w) white, (b) black, (null) draw
 * @param dtz moves until capture or pawn move
 * @param precise_dtz moves until capture or pawn move - aims for fastest win, while guaranteeing the 50-move rule isn't violated.
 */
public record LichessResponse(List<LichessMoveData> mainline, String winner, int dtz, int precise_dtz) {
}
