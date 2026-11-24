package com.comp2042.model;

import com.comp2042.JavaFXTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.Point;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Obstacle Model Unit Tests")
class ObstacleTest extends JavaFXTestBase {

    private Obstacle obstacle;
    private Snake snake;
    private Coin coin;

    @BeforeEach
    void setUp() {
        obstacle = new Obstacle(20, 20);
        snake = new Snake(20, 20, false, obstacle);
        coin = new Coin(20, 20, snake, obstacle);
    }

    @Test
    @DisplayName("Verify obstacle list is initially empty")
    void testInitialObstaclesEmpty() {
        assertNotNull(obstacle.getObstacles(), "Obstacles list should not be null");
        assertTrue(obstacle.getObstacles().isEmpty(), "Obstacles list should initially be empty");
    }

    @Test
    @DisplayName("Verify newObstacles spawns the requested amount within boundaries")
    void testNewObstaclesSpawn() {
        obstacle.newObstacles(snake, coin, 5);
        assertEquals(5, obstacle.getObstacles().size(), "Should have spawned exactly 5 obstacles");

        for (Point p : obstacle.getObstacles()) {
            assertTrue(p.x >= 0 && p.x < 20, "Obstacle X should be within [0, 20)");
            assertTrue(p.y >= 0 && p.y < 20, "Obstacle Y should be within [0, 20)");
        }
    }

    @Test
    @DisplayName("Verify newObstacles never collides with snake head, body, or coin")
    void testObstaclesAvoidSnakeAndCoin() {
        obstacle.newObstacles(snake, coin, 10);
        for (Point p : obstacle.getObstacles()) {
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
        // Add 51 obstacles
        obstacle.newObstacles(snake, coin, 51);
        int currentCount = obstacle.getObstacles().size();
        assertEquals(51, currentCount, "Initial batch should contain 51 obstacles");

        // Attempting to add more when size > 50 should return immediately
        obstacle.newObstacles(snake, coin, 10);
        assertEquals(currentCount, obstacle.getObstacles().size(), "Obstacle count should not increase once > 50");
    }

    @Test
    @DisplayName("Verify loadLevelLayout sets distinct obstacle patterns per level")
    void testLoadLevelLayout() {
        // Level 1: empty arena
        obstacle.loadLevelLayout(1);
        assertTrue(obstacle.getObstacles().isEmpty(), "Level 1 should have 0 static obstacles");

        // Level 2: Four Pillars (4 blocks of 2x2 = 16 obstacles)
        obstacle.loadLevelLayout(2);
        assertEquals(16, obstacle.getObstacles().size(), "Level 2 should have 16 pillar obstacle points");

        // Level 3: The Labyrinth
        obstacle.loadLevelLayout(3);
        assertFalse(obstacle.getObstacles().isEmpty(), "Level 3 must have labyrinth corridor walls");
        assertFalse(obstacle.getObstacles().contains(new Point(10, 10)), "Center must be clear for snake traversal");
    }
}
