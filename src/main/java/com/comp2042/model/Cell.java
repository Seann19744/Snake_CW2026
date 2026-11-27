package com.comp2042.model;

/**
 * Immutable 2D grid coordinate record representing a tile at column {@code x} and row {@code y}.
 * Replaces mutable {@code java.awt.Point} to enforce encapsulation and eliminate double-to-int casts.
 *
 * @param x horizontal grid column index
 * @param y vertical grid row index
 */
public record Cell(int x, int y) {

    /**
     * Returns a new {@link Cell} offset by {@code (dx, dy)}.
     *
     * @param dx horizontal delta
     * @param dy vertical delta
     * @return translated cell
     */
    public Cell translate(int dx, int dy) {
        return new Cell(x + dx, y + dy);
    }

    /**
     * Returns a new {@link Cell} moved one tile in the given {@link Direction}.
     *
     * @param direction movement direction
     * @return translated cell
     */
    public Cell translate(Direction direction) {
        return new Cell(x + direction.getDx(), y + direction.getDy());
    }
}
