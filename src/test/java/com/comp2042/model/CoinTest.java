package com.comp2042.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Coin Hierarchy Unit Tests")
class CoinTest {

    private Obstacle obstacle;
    private Snake snake;
    private Game game;
    private double appliedSpeedRate;
    private SpeedController speedController;

    @BeforeEach
    void setUp() {
        GameSettings.getInstance().resetDefaults();
        obstacle = new Obstacle(20, 20);
        snake = new Snake(20, 20, false, obstacle);
        game = new Game(20, 20, true, GameMode.CLASSIC);
        appliedSpeedRate = 0.0;
        speedController = rate -> appliedSpeedRate = rate;
    }

    @Test
    @DisplayName("Verify base Coin point value is 5 and coordinates are valid")
    void testBaseCoin() {
        Coin coin = new Coin(20, 20, snake, obstacle);
        Cell pos = coin.getPos();

        assertNotNull(pos, "Coin position should not be null");
        assertTrue(pos.x() >= 0 && pos.x() < 20, "Coin X coordinate should be within [0, 20)");
        assertTrue(pos.y() >= 0 && pos.y() < 20, "Coin Y coordinate should be within [0, 20)");
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

    @Test
    @DisplayName("Verify polymorphic applyEffect for standard Coin: awards 5 points and resets speed")
    void testStandardCoinApplyEffect() {
        Coin coin = new Coin(20, 20, snake, obstacle);
        coin.applyEffect(game, snake, obstacle, speedController);

        assertEquals(5, game.getScorePoints(), "Should award 5 points");
        assertEquals(game.getSpeed(), appliedSpeedRate, 0.001, "Rate should match game speed");
    }

    @Test
    @DisplayName("Verify polymorphic applyEffect for GoldCoin: awards 15 points")
    void testGoldCoinApplyEffect() {
        GoldCoin goldCoin = new GoldCoin(20, 20, snake, obstacle);
        goldCoin.applyEffect(game, snake, obstacle, speedController);

        assertEquals(15, game.getScorePoints(), "GoldCoin should award 15 points");
    }

    @Test
    @DisplayName("Verify polymorphic applyEffect for RainbowCoin: applies speed bonus")
    void testRainbowCoinApplyEffect() {
        RainbowCoin rainbowCoin = new RainbowCoin(20, 20, snake, obstacle);
        rainbowCoin.applyEffect(game, snake, obstacle, speedController);

        assertEquals(5, game.getScorePoints(), "RainbowCoin should award 5 points");
        assertEquals(game.getSpeed() + 5.0, appliedSpeedRate, 0.001, "Rate should include 5.0 speed bonus");
    }

    @Test
    @DisplayName("Verify polymorphic applyEffect for BlueCoin: enables border pass-through")
    void testBlueCoinApplyEffect() {
        BlueCoin blueCoin = new BlueCoin(20, 20, snake, obstacle);
        blueCoin.applyEffect(game, snake, obstacle, speedController);

        assertEquals(5, game.getScorePoints(), "BlueCoin should award 5 points");
        Snake testSnake = new Snake(4, 4, false, obstacle);
        blueCoin.applyEffect(game, testSnake, obstacle, speedController);
        testSnake.move('L');
        testSnake.move('L');
        testSnake.move('L'); // Crosses border
        assertFalse(testSnake.getDead(), "Snake should pass through border when BlueCoin effect is active");
    }

    @Test
    @DisplayName("Verify polymorphic applyEffect for SilverCoin: spawns obstacles and decreases maxScore")
    void testSilverCoinApplyEffect() {
        SilverCoin silverCoin = new SilverCoin(20, 20, snake, obstacle);
        silverCoin.applyEffect(game, snake, obstacle, speedController);

        assertEquals(5, game.getScorePoints(), "SilverCoin should award 5 points");
        assertFalse(obstacle.getObstacles().isEmpty(), "SilverCoin must spawn obstacles on the board");
    }
}
