package com.comp2042.model;

public class GoldCoin extends Coin {

    public GoldCoin(int x, int y, Snake snake, Obstacle obstacle) {
        super(x, y, snake, obstacle);
    }

    @Override
    public int getPoints() {
        return 15;
    }

    @Override
    public String getSpritePath() {
        return "resources/views/res/goldcoin.png";
    }
}
