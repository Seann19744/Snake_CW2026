package com.comp2042.model;

import java.util.Random;

public class SilverCoin extends Coin {

    public SilverCoin(int x, int y, Snake snake, Obstacle obstacle) {
        super(x, y, snake, obstacle);
    }

    @Override
    public int getPoints() {
        return 5;
    }

    @Override
    public void applyEffect(Game game, Snake snake, Obstacle obstacle, SpeedController speedController) {
        super.applyEffect(game, snake, obstacle, speedController);
        Random rnd = new Random();
        obstacle.newObstacles(snake, this, rnd.nextInt(game.getWidth() * game.getHeight() / 20) + 3);
        game.decMaxScore(obstacle.getObstacles().size());
    }
}
