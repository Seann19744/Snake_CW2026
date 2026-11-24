package com.comp2042.model;

import java.util.Random;

/**
 * Generator class responsible for instantiating Coin objects.
 * Implements the Factory Pattern to encapsulate coin creation and probability logic,
 * decoupling instantiation from controllers and supporting the Single Responsibility Principle.
 */
public class CoinGenerator {

    private final Random random;

    /**
     * Constructs a CoinGenerator using the default pseudo-random number generator.
     */
    public CoinGenerator() {
        this(new Random());
    }

    /**
     * Constructs a CoinGenerator with an injected Random instance (useful for deterministic unit testing).
     *
     * @param random the Random instance to use for coin type selection
     */
    public CoinGenerator(Random random) {
        this.random = random;
    }

    /**
     * Generates a new Coin based on game rules and probability.
     * When power-ups are disabled, standard Coins are always generated.
     * When power-ups are enabled, special coins (Gold, Rainbow, Blue, Silver) are generated based on chance.
     *
     * @param width the grid width
     * @param height the grid height
     * @param snake the snake instance (to prevent spawning on its body/head)
     * @param obstacle the obstacle instance (to prevent spawning on obstacles)
     * @param powerUpEnabled whether special power-up coin variants may spawn
     * @return a new Coin or Coin subclass instance
     */
    public Coin generateCoin(int width, int height, Snake snake, Obstacle obstacle, boolean powerUpEnabled) {
        if (!powerUpEnabled) {
            return new Coin(width, height, snake, obstacle);
        }

        int ran = random.nextInt(12);
        if (ran == 1) {
            return new GoldCoin(width, height, snake, obstacle);
        } else if (ran == 2) {
            return new RainbowCoin(width, height, snake, obstacle);
        } else if (ran == 3) {
            return new BlueCoin(width, height, snake, obstacle);
        } else if (ran == 4 && obstacle.getObstacles().isEmpty()) {
            return new SilverCoin(width, height, snake, obstacle);
        } else {
            return new Coin(width, height, snake, obstacle);
        }
    }

    /**
     * Alias for generateCoin to maintain compatibility with factory conventions.
     */
    public Coin createCoin(int width, int height, Snake snake, Obstacle obstacle, boolean powerUpEnabled) {
        return generateCoin(width, height, snake, obstacle, powerUpEnabled);
    }
}
