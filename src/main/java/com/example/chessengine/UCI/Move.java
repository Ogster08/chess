package com.example.chessengine.UCI;

import java.util.Objects;

/**
 *
 */
public class Move {
    /**
     * The piece being moved
     */
    private final Piece p;
    /**
     * The cell the piece is moving to
     */
    private final Cell cell;

    /**
     * @param p    The piece making the move
     * @param cell The cell the piece is moving to
     */
    public Move(Piece p, Cell cell) {
        this.p = p;
        this.cell = cell;
    }

    /**
     * Indicates whether some other object is "equal to" this one.  In addition
     * to the general contract of {@link Object#equals(Object) Object.equals},
     * record classes must further obey the invariant that when
     * a record instance is "copied" by passing the result of the record component
     * accessor methods to the canonical constructor, as follows:
     * <pre>
     *     R copy = new R(r.c1(), r.c2(), ..., r.cn());
     * </pre>
     * then it must be the case that {@code r.equals(copy)}.
     *
     * @param obj the reference object with which to compare.
     * @return {@code true} if this record is equal to the
     * argument; {@code false} otherwise.
     * @implSpec The implicitly provided implementation returns {@code true} if
     * and only if the argument is an instance of the same record class
     * as this record, and each component of this record is equal to
     * the corresponding component of the argument; otherwise, {@code
     * false} is returned. Equality of a component {@code c} is
     * determined as follows:
     * <ul>
     *
     * <li> If the component is of a reference type, the component is
     * considered equal if and only if {@link
     * Objects#equals(Object, Object)
     * Objects.equals(this.c, r.c)} would return {@code true}.
     *
     * <li> If the component is of a primitive type, using the
     * corresponding primitive wrapper class {@code PW} (the
     * corresponding wrapper class for {@code int} is {@code
     * java.lang.Integer}, and so on), the component is considered
     * equal if and only if {@code
     * PW.compare(this.c, r.c)} would return {@code 0}.
     *
     * </ul>
     * <p>
     * Apart from the semantics described above, the precise algorithm
     * used in the implicitly provided implementation is unspecified
     * and is subject to change. The implementation may or may not use
     * calls to the particular methods listed, and may or may not
     * perform comparisons in the order of component declaration.
     * @see Objects#equals(Object, Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj.getClass() != this.getClass()) return false;
        Move m = (Move) obj;
        if (this.p != m.p) return false;
        return this.cell == m.cell;
    }

    /**
     * @return The piece being moved.
     */
    public Piece p() {
        return p;
    }

    /**
     * @return The cell the piece is being moved to.
     */
    public Cell cell() {
        return cell;
    }


    /**
     * Makes a string out of the move object, by listing the piece and cell it is moving to.
     * @return The string representation of the object
     */
    @Override
    public String toString() {
        return "Move[" +
                "p= " + p + ", " +
                "cell= " + cell + ']';
    }

}
