package com.comp2042.model;

public class BlueCoin extends Coin {

    public BlueCoin(int x, int y, Snake snake, Obstacle obstacle) {
        super(x, y, snake, obstacle);
    }

    @Override
    public void applyEffect(Game game, Snake snake, Obstacle obstacle, SpeedController speedController) {
        super.applyEffect(game, snake, obstacle, speedController);
        snake.ignoreBorders(true);
    }
}
