package com.example.chessengine.Board.Moves;

import com.example.chessengine.Board.Cell;
import com.example.chessengine.Board.Pieces.*;

public class PromotionMove extends Move {
    public final Class<?> promotionClass;

    /**
     * @param p The pawn doing the promotion
     * @param cell The cell the pawn is moving to
     * @param promotionClass The class of the promotion piece
     */
    public PromotionMove(Pawn p, Cell cell, Class<?> promotionClass) {
        super(p, cell);
        this.promotionClass = promotionClass;
    }

    public Piece getPromotionPiece(){
        if (promotionClass == Rook.class) return new Rook(p().getBoard(), cell().getRow(), cell().getCol(), p().getColour(), false);
        if (promotionClass == Queen.class) return new Queen(p().getBoard(), cell().getRow(), cell().getCol(), p().getColour());
        if (promotionClass == Bishop.class) return new Bishop(p().getBoard(), cell().getRow(), cell().getCol(), p().getColour());
        if (promotionClass == Knight.class) return new Knight(p().getBoard(), cell().getRow(), cell().getCol(), p().getColour());
        throw new IllegalArgumentException("Invalid class: " + promotionClass);
    }

    @Override
    public String toString() {
        return "PromotionMove{" +
                "promotionClass=" + promotionClass +
                "p= " + p() + ", " +
                "cell= " + cell() + '}';
    }
}
