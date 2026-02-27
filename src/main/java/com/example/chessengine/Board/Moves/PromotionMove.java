package com.example.chessengine.Board.Moves;

import com.example.chessengine.Board.Cell;
import com.example.chessengine.Board.Pieces.*;

/**
 * The PromotionMove class, extending the move class.
 * Contains the pawn being moved and the cell it is being moved to
 * Contains the class the pawn is promoting to
 */
public class PromotionMove extends Move {
    /**
     * The class of the promotion piece
     */
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

    /**
     * Creates a new object from the class of the promotion piece.
     * Throws an error if the class isn't a piece class
     * @return a new object of the promotion piece
     */
    public Piece getPromotionPiece(){
        if (promotionClass == Rook.class) return new Rook(p().getBoard(), cell().getRow(), cell().getCol(), p().getColour(), false);
        if (promotionClass == Queen.class) return new Queen(p().getBoard(), cell().getRow(), cell().getCol(), p().getColour());
        if (promotionClass == Bishop.class) return new Bishop(p().getBoard(), cell().getRow(), cell().getCol(), p().getColour());
        if (promotionClass == Knight.class) return new Knight(p().getBoard(), cell().getRow(), cell().getCol(), p().getColour());
        throw new IllegalArgumentException("Invalid class: " + promotionClass);
    }

    /**
     * @return the string representation of the promotion move, with the pawn, target square and promotion piece class
     */
    @Override
    public String toString() {
        return "PromotionMove{" +
                "promotionClass=" + promotionClass +
                "p= " + p() + ", " +
                "cell= " + cell() + '}';
    }
}
