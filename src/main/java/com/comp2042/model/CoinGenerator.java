package com.comp2042.model;

import java.util.Random;

/**
 * Generator class responsible for instantiating Coin objects.
 * Implements the Factory Pattern to encapsulate coin creation and probability logic,
 * decoupling instantiation from controllers and supporting the Single Responsibility Principle.
 */
public class CoinGenerator {

    public static final int POWER_UP_ROLL_BOUND = 12;
    public static final int GOLD_COIN_ROLL = 1;
    public static final int RAINBOW_COIN_ROLL = 2;
    public static final int BLUE_COIN_ROLL = 3;
    public static final int SILVER_COIN_ROLL = 4;

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

        int roll = random.nextInt(POWER_UP_ROLL_BOUND);
        if (roll == GOLD_COIN_ROLL) {
            return new GoldCoin(width, height, snake, obstacle);
        } else if (roll == RAINBOW_COIN_ROLL) {
            return new RainbowCoin(width, height, snake, obstacle);
        } else if (roll == BLUE_COIN_ROLL) {
            return new BlueCoin(width, height, snake, obstacle);
        } else if (roll == SILVER_COIN_ROLL && obstacle.getObstacles().isEmpty()) {
            return new SilverCoin(width, height, snake, obstacle);
        } else {
            return new Coin(width, height, snake, obstacle);
        }
    }
}
