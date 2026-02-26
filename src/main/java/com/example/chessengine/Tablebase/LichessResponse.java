package com.example.chessengine.Tablebase;

import java.util.List;

public record LichessResponse(List<LichessMoveData> mainline, String winner, int dtz, int precise_dtz) {
}
