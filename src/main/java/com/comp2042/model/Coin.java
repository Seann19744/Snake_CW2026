package com.comp2042.model;

import java.util.Random;

public class Coin {
    private final Cell pos;

    public Coin(int x, int y, Snake snake, Obstacle obstacle) {
        Random rnd = new Random();
        Cell candidate;
        do {
            candidate = new Cell(rnd.nextInt(x), rnd.nextInt(y));
        } while (snake.getHead().equals(candidate)
                || snake.getBody().contains(candidate)
                || obstacle.getObstacles().contains(candidate));
        this.pos = candidate;
    }

    public Cell getPos() {
        return pos;
    }

    public int getPoints() {
        return 5;
    }

    /**
     * Returns the resource path of the sprite image representing this coin.
     * Subclasses override this method to provide their own sprite resource path,
     * allowing the view layer to resolve and cache coin images polymorphically
     * without {@code instanceof} type checks (Open-Closed Principle).
     *
     * @return classpath resource path to the coin's sprite image
     */
    public String getSpritePath() {
        return "resources/views/res/redcoin.png";
    }

    /**
     * Applies the effect of this coin when eaten by the snake.
     * By default, resets speed, resets border-passing, and awards points.
     * Subclasses override this method to provide polymorphic power-up behavior (Open-Closed Principle)
     * while depending on the {@link SpeedController} abstraction (Dependency Inversion Principle).
     *
     * @param game the current Game instance
     * @param snake the Snake instance
     * @param obstacle the Obstacle instance
     * @param speedController callback interface to update the game loop rate
     */
    public void applyEffect(Game game, Snake snake, Obstacle obstacle, SpeedController speedController) {
        if (speedController != null) {
            speedController.setSpeedRate(game.getSpeed());
        }
        snake.ignoreBorders(false);
        game.addPoints(getPoints());
    }
}
