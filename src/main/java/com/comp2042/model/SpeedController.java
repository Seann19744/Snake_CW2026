package com.comp2042.model;

/**
 * Pure-Java abstraction for adjusting the game loop speed rate (Dependency Inversion Principle).
 * Decouples model power-up effects ({@link Coin#applyEffect}) from JavaFX's {@code Timeline}.
 */
@FunctionalInterface
public interface SpeedController {

    /**
     * Sets the active game loop animation speed rate.
     *
     * @param rate the new speed rate
     */
    void setSpeedRate(double rate);
}
