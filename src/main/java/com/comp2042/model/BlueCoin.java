package com.comp2042.model;

import javafx.animation.Timeline;
import javafx.scene.image.Image;

public class BlueCoin extends Coin {
    private Image image = new Image("resources/views/res/bluecoin.png");

    public BlueCoin(int x, int y, Snake snake, Obstacle obstacle) {
        super(x, y, snake, obstacle);
    }

    public int getPoints() {
        int points = 5;
        return points;
    }

    public Image getImage() {
        return image;
    }

    @Override
    public void applyEffect(Game game, Snake snake, Obstacle obstacle, Timeline animation) {
        super.applyEffect(game, snake, obstacle, animation);
        snake.ignoreBorders(true);
    }
}
