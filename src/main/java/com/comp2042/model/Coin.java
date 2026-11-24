package com.comp2042.model;

import javafx.animation.Timeline;
import javafx.scene.image.Image;

import java.awt.*;
import java.util.Random;

public class Coin {
    private Point pos;
    private Image image = new Image("resources/views/res/redcoin.png");

    public Coin(int x, int y, Snake snake, Obstacle obstacle) {
        int tx;
        int ty;
        do {
            Random rnd = new Random();

            tx = rnd.nextInt(x);
            ty = rnd.nextInt(y);
            pos = new Point(tx, ty);
        } while (snake.getHead().equals(pos) || snake.getBody().contains(pos) || obstacle.getObstacles().contains(pos));
    }

    public Point getPos() {
        return new Point(pos);
    }

    public int getPoints() {
        int points = 5;
        return points;
    }

    public Image getImage() {
        return image;
    }

    /**
     * Applies the effect of this coin when eaten by the snake.
     * By default, resets speed, resets border-passing, and awards points.
     * Subclasses override this method to provide polymorphic power-up behavior (Open-Closed Principle).
     *
     * @param game the current Game instance
     * @param snake the Snake instance
     * @param obstacle the Obstacle instance
     * @param animation the game loop Timeline
     */
    public void applyEffect(Game game, Snake snake, Obstacle obstacle, Timeline animation) {
        animation.setRate(game.getSpeed());
        snake.ignoreBorders(false);
        game.addPoints(getPoints());
    }
}
