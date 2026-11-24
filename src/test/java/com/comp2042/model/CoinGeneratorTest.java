package com.comp2042.model;

import com.comp2042.JavaFXTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CoinGenerator Factory Pattern Unit Tests")
class CoinGeneratorTest extends JavaFXTestBase {

    private Obstacle obstacle;
    private Snake snake;

    @BeforeEach
    void setUp() {
        obstacle = new Obstacle(20, 20);
        snake = new Snake(20, 20, false, obstacle);
    }

    @Test
    @DisplayName("Verify CoinGenerator creates standard Coin when power-ups are disabled")
    void testStandardCoinWhenPowerUpDisabled() {
        // Even if random generates 1 (which would normally produce GoldCoin)
        CoinGenerator generator = new CoinGenerator(new DeterministicRandom(1));
        Coin coin = generator.generateCoin(20, 20, snake, obstacle, false);

        assertNotNull(coin);
        assertEquals(Coin.class, coin.getClass(), "Should return standard Coin when powerUpEnabled is false");
    }

    @Test
    @DisplayName("Verify CoinGenerator creates GoldCoin when random index is 1")
    void testCreatesGoldCoin() {
        CoinGenerator generator = new CoinGenerator(new DeterministicRandom(1));
        Coin coin = generator.generateCoin(20, 20, snake, obstacle, true);

        assertInstanceOf(GoldCoin.class, coin, "Expected GoldCoin when random index is 1");
    }

    @Test
    @DisplayName("Verify CoinGenerator creates RainbowCoin when random index is 2")
    void testCreatesRainbowCoin() {
        CoinGenerator generator = new CoinGenerator(new DeterministicRandom(2));
        Coin coin = generator.generateCoin(20, 20, snake, obstacle, true);

        assertInstanceOf(RainbowCoin.class, coin, "Expected RainbowCoin when random index is 2");
    }

    @Test
    @DisplayName("Verify CoinGenerator creates BlueCoin when random index is 3")
    void testCreatesBlueCoin() {
        CoinGenerator generator = new CoinGenerator(new DeterministicRandom(3));
        Coin coin = generator.generateCoin(20, 20, snake, obstacle, true);

        assertInstanceOf(BlueCoin.class, coin, "Expected BlueCoin when random index is 3");
    }

    @Test
    @DisplayName("Verify CoinGenerator creates SilverCoin when random index is 4 and no obstacles exist")
    void testCreatesSilverCoinWhenNoObstacles() {
        CoinGenerator generator = new CoinGenerator(new DeterministicRandom(4));
        Coin coin = generator.generateCoin(20, 20, snake, obstacle, true);

        assertInstanceOf(SilverCoin.class, coin, "Expected SilverCoin when random is 4 and obstacles empty");
    }

    @Test
    @DisplayName("Verify CoinGenerator creates standard Coin when random index is 4 but obstacles already exist")
    void testCreatesStandardCoinWhenObstaclesExist() {
        obstacle.newObstacles(snake, new Coin(20, 20, snake, obstacle), 3);
        assertFalse(obstacle.getObstacles().isEmpty(), "Obstacles should not be empty");

        CoinGenerator generator = new CoinGenerator(new DeterministicRandom(4));
        Coin coin = generator.generateCoin(20, 20, snake, obstacle, true);

        assertEquals(Coin.class, coin.getClass(), "Should revert to standard Coin when obstacles already present");
    }

    @Test
    @DisplayName("Verify CoinGenerator creates standard Coin for random index > 4")
    void testCreatesStandardCoinDefault() {
        CoinGenerator generator = new CoinGenerator(new DeterministicRandom(7));
        Coin coin = generator.generateCoin(20, 20, snake, obstacle, true);

        assertEquals(Coin.class, coin.getClass(), "Expected regular Coin for unassigned random index");
    }

    /**
     * Test double for Random returning a fixed value for deterministic testing.
     */
    private static class DeterministicRandom extends Random {
        private final int fixedValue;

        DeterministicRandom(int fixedValue) {
            this.fixedValue = fixedValue;
        }

        @Override
        public int nextInt(int bound) {
            return fixedValue;
        }
    }
}
