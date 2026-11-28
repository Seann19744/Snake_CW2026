package com.comp2042.model;

public class RainbowCoin extends Coin {

    public RainbowCoin(int x, int y, Snake snake, Obstacle obstacle) {
        super(x, y, snake, obstacle);
    }

    public double getSpeedBonus() {
        return 5.0;
    }

    @Override
    public String getSpritePath() {
        return "resources/views/res/rainbowcoin.png";
    }

    @Override
    public void applyEffect(Game game, Snake snake, Obstacle obstacle, SpeedController speedController) {
        super.applyEffect(game, snake, obstacle, speedController);
        if (speedController != null) {
            speedController.setSpeedRate(game.getSpeed() + getSpeedBonus());
        }
    }
}
