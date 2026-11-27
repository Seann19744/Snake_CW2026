package com.comp2042.model;

/**
 * Type-safe enumeration representing the four cardinal movement directions of
 * the snake.
 * Encapsulates grid translation deltas, sprite rotation angles,
 * opposite-direction checks,
 * and corner turn sprite angles.
 */
public enum Direction {
    UP(0, -1, 0),
    DOWN(0, 1, 180),
    LEFT(-1, 0, 270),
    RIGHT(1, 0, 90);

    private final int dx;
    private final int dy;
    private final int angle;

    Direction(int dx, int dy, int angle) {
        this.dx = dx;
        this.dy = dy;
        this.angle = angle;
    }

    public int getDx() {
        return dx;
    }

    public int getDy() {
        return dy;
    }

    public int getAngle() {
        return angle;
    }

    /**
     * Returns the straight body sprite rotation angle (270 for horizontal, 0 for
     * vertical).
     */
    public int getBodyAngle() {
        return (this == LEFT || this == RIGHT) ? 270 : 0;
    }

    /**
     * Checks whether the specified direction is the 180-degree opposite of this
     * direction.
     *
     * @param other the direction to compare against
     * @return true if {@code other} is directly opposite to this direction
     */
    public boolean isOpposite(Direction other) {
        if (other == null) {
            return false;
        }
        return this.dx + other.dx == 0 && this.dy + other.dy == 0;
    }

    /**
     * Computes the rotation angle (in degrees) for the corner turn sprite when
     * transitioning
     * from {@code incoming} direction into {@code outgoing} direction.
     *
     * @param incoming the direction entering the corner tile
     * @param outgoing the direction exiting the corner tile
     * @return rotation angle in degrees (90, 180, 270, or 360)
     */
    public static int getCornerRotation(Direction incoming, Direction outgoing) {
        if ((incoming == LEFT && outgoing == UP) || (incoming == DOWN && outgoing == RIGHT)) {
            return 180;
        } else if ((incoming == LEFT && outgoing == DOWN) || (incoming == UP && outgoing == RIGHT)) {
            return 270;
        } else if ((incoming == RIGHT && outgoing == UP) || (incoming == DOWN && outgoing == LEFT)) {
            return 90;
        } else if ((incoming == UP && outgoing == LEFT) || (incoming == RIGHT && outgoing == DOWN)) {
            return 360;
        }
        return 0;
    }

    /**
     * Maps a legacy character ('U', 'D', 'L', 'R') to its corresponding
     * {@link Direction}.
     */
    public static Direction fromChar(char dir) {
        return switch (dir) {
            case 'U' -> UP;
            case 'D' -> DOWN;
            case 'L' -> LEFT;
            case 'R' -> RIGHT;
            default -> throw new IllegalArgumentException("Unknown direction char: " + dir);
        };
    }
}
