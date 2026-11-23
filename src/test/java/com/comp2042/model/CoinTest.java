package com.comp2042.model;

import com.comp2042.JavaFXTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.Point;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Coin Hierarchy Unit Tests")
class CoinTest extends JavaFXTestBase {

    private Obstacle obstacle;
    private Snake snake;

    @BeforeEach
    void setUp() {
        obstacle = new Obstacle(20, 20);
        snake = new Snake(20, 20, false, obstacle);
    }

    @Test
    @DisplayName("Verify base Coin point value is 5 and coordinates are valid")
    void testBaseCoin() {
        Coin coin = new Coin(20, 20, snake, obstacle);
        Point pos = coin.getPos();

        assertNotNull(pos, "Coin position should not be null");
        assertTrue(pos.x >= 0 && pos.x < 20, "Coin X coordinate should be within [0, 20)");
        assertTrue(pos.y >= 0 && pos.y < 20, "Coin Y coordinate should be within [0, 20)");
        assertNotEquals(snake.getHead(), pos, "Coin must not spawn on snake head");
        assertFalse(snake.getBody().contains(pos), "Coin must not spawn on snake body");
        assertEquals(5, coin.getPoints(), "Base coin points should be 5");
    }

    @Test
    @DisplayName("Verify GoldCoin points are 15")
    void testGoldCoin() {
        GoldCoin goldCoin = new GoldCoin(20, 20, snake, obstacle);
        assertEquals(15, goldCoin.getPoints(), "Gold coin should yield 15 points");
        assertInstanceOf(Coin.class, goldCoin, "GoldCoin must inherit from Coin");
    }

    @Test
    @DisplayName("Verify RainbowCoin points and speed bonus")
    void testRainbowCoin() {
        RainbowCoin rainbowCoin = new RainbowCoin(20, 20, snake, obstacle);
        assertEquals(5, rainbowCoin.getPoints(), "Rainbow coin should yield 5 points");
        assertEquals(5.0, rainbowCoin.getSpeedBonus(), 0.001, "Rainbow coin speed bonus should be 5.0");
        assertInstanceOf(Coin.class, rainbowCoin, "RainbowCoin must inherit from Coin");
    }

    @Test
    @DisplayName("Verify SilverCoin points")
    void testSilverCoin() {
        SilverCoin silverCoin = new SilverCoin(20, 20, snake, obstacle);
        assertEquals(5, silverCoin.getPoints(), "Silver coin should yield 5 points");
        assertInstanceOf(Coin.class, silverCoin, "SilverCoin must inherit from Coin");
    }

    @Test
    @DisplayName("Verify BlueCoin points")
    void testBlueCoin() {
        BlueCoin blueCoin = new BlueCoin(20, 20, snake, obstacle);
        assertEquals(5, blueCoin.getPoints(), "Blue coin should yield 5 points");
        assertInstanceOf(Coin.class, blueCoin, "BlueCoin must inherit from Coin");
    }
}
