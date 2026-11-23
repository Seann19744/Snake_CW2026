package com.comp2042.model;

/**
 * The difficulty levels of the game. Each level defines how fast the snake
 * moves and whether the snake can pass through the borders.
 */
public enum Difficulty {
    EASY(8.5, true),
    NORMAL(8.5, false),
    HARD(13, false);

    private final double speed;
    private final boolean noBorder;

    Difficulty(double speed, boolean noBorder) {
        this.speed = speed;
        this.noBorder = noBorder;
    }

    /**
     * Returns how fast the game runs at this difficulty.
     *
     * @return the animation speed multiplier
     */
    public double getSpeed() {
        return speed;
    }

    /**
     * Returns whether the snake wraps around the border instead of dying.
     *
     * @return true if the border is not deadly at this difficulty
     */
    public boolean isNoBorder() {
        return noBorder;
    }
}