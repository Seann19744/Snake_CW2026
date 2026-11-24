package com.comp2042.model;

import javafx.animation.Timeline;
import javafx.scene.image.Image;

import java.util.Random;

public class SilverCoin extends Coin {
    private Image image = new Image("resources/views/res/silvercoin.png");

    public SilverCoin(int x, int y, Snake snake, Obstacle obstacle) {
        super(x, y, snake, obstacle);
    }

    public Image getImage() {
        return image;
    }

    public int getPoints() {
        int points = 5;
        return points;
    }

    @Override
    public void applyEffect(Game game, Snake snake, Obstacle obstacle, Timeline animation) {
        super.applyEffect(game, snake, obstacle, animation);
        Random rnd = new Random();
        obstacle.newObstacles(snake, this, rnd.nextInt(game.getWidth() * game.getHeight() / 20) + 3);
        game.decMaxScore(obstacle.getObstacles().size());
    }
}
