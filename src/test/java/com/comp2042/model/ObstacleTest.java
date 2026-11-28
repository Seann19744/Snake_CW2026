package com.comp2042.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Obstacle Model Unit Tests")
class ObstacleTest {

    private Obstacle obstacle;
    private Snake snake;
    private Coin coin;

    @BeforeEach
    void setUp() {
        GameSettings.getInstance().resetDefaults();
        obstacle = new Obstacle(20, 20);
        snake = new Snake(20, 20, false, obstacle);
        coin = new Coin(20, 20, snake, obstacle);
    }

    @Test
    @DisplayName("Verify obstacle list is initially empty and unmodifiable")
    void testInitialObstaclesEmpty() {
        assertNotNull(obstacle.getObstacles(), "Obstacles list should not be null");
        assertTrue(obstacle.getObstacles().isEmpty(), "Obstacles list should initially be empty");
        assertThrows(UnsupportedOperationException.class,
                () -> obstacle.getObstacles().add(new Cell(0, 0)),
                "getObstacles() must return an unmodifiable list");
    }

    @Test
    @DisplayName("Verify newObstacles spawns the requested amount within boundaries")
    void testNewObstaclesSpawn() {
        obstacle.newObstacles(snake, coin, 5);
        assertEquals(5, obstacle.getObstacles().size(), "Should have spawned exactly 5 obstacles");

        for (Cell p : obstacle.getObstacles()) {
            assertTrue(p.x() >= 0 && p.x() < 20, "Obstacle X should be within [0, 20)");
            assertTrue(p.y() >= 0 && p.y() < 20, "Obstacle Y should be within [0, 20)");
        }
    }

    @Test
    @DisplayName("Verify newObstacles never collides with snake head, body, or coin")
    void testObstaclesAvoidSnakeAndCoin() {
        obstacle.newObstacles(snake, coin, 10);
        for (Cell p : obstacle.getObstacles()) {
            assertNotEquals(snake.getHead(), p, "Obstacle must not spawn on snake head");
            assertFalse(snake.getBody().contains(p), "Obstacle must not spawn on snake body");
            assertNotEquals(coin.getPos(), p, "Obstacle must not spawn on current coin");
        }
    }

    @Test
    @DisplayName("Verify reset clears all spawned obstacles")
    void testResetObstacles() {
        obstacle.newObstacles(snake, coin, 5);
        assertFalse(obstacle.getObstacles().isEmpty(), "Obstacles should be present");

        obstacle.reset();
        assertTrue(obstacle.getObstacles().isEmpty(), "Obstacles list should be empty after reset");
    }

    @Test
    @DisplayName("Verify obstacle limit cap stops spawning when exceeding 50 obstacles")
    void testObstacleLimitCap() {
        obstacle.newObstacles(snake, coin, 51);
        int currentCount = obstacle.getObstacles().size();
        assertEquals(51, currentCount, "Initial batch should contain 51 obstacles");

        obstacle.newObstacles(snake, coin, 10);
        assertEquals(currentCount, obstacle.getObstacles().size(), "Obstacle count should not increase once > 50");
    }

    @Test
    @DisplayName("Verify loadLevelLayout sets distinct obstacle patterns per level")
    void testLoadLevelLayout() {
        obstacle.loadLevelLayout(1);
        assertTrue(obstacle.getObstacles().isEmpty(), "Level 1 should have 0 static obstacles");

        obstacle.loadLevelLayout(2);
        assertEquals(16, obstacle.getObstacles().size(), "Level 2 should have 16 pillar obstacle points");

        obstacle.loadLevelLayout(3);
        assertFalse(obstacle.getObstacles().isEmpty(), "Level 3 must have labyrinth corridor walls");
        assertFalse(obstacle.getObstacles().contains(new Cell(10, 10)), "Center must be clear for snake traversal");
    }

    @Test
    @DisplayName("Verify newObstacles spawns unique cells and respects the 2-tile safety buffer around snake head")
    void testNewObstaclesUniqueAndSafeFromHead() {
        obstacle.newObstacles(snake, coin, 35);
        assertEquals(35, obstacle.getObstacles().size(), "Should spawn 35 obstacles");

        java.util.Set<Cell> uniquePoints = new java.util.HashSet<>(obstacle.getObstacles());
        assertEquals(35, uniquePoints.size(), "All spawned obstacles must occupy unique cells without duplicates");

        Cell head = snake.getHead();
        for (Cell p : obstacle.getObstacles()) {
            assertFalse(obstacle.isTooCloseToHead(p, head),
                    "Obstacle at " + p + " must not spawn within 2-tile safety buffer of head at " + head);
        }
    }
}
