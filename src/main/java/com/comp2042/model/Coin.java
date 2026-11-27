package com.comp2042.model;

import java.awt.*;
import java.util.Random;

public class Coin {
    private Point pos;

    public Coin(int x, int y, Snake snake, Obstacle obstacle) {
        int tx;
        int ty;
        Random rnd = new Random();
        do {
            tx = rnd.nextInt(x);
            ty = rnd.nextInt(y);
            pos = new Point(tx, ty);
        } while (snake.getHead().equals(pos) || snake.getBody().contains(pos) || obstacle.getObstacles().contains(pos));
    }

    public Point getPos() {
        return new Point(pos);
    }

    public int getPoints() {
        return 5;
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
