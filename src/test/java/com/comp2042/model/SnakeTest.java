package com.comp2042.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Snake Model Unit Tests")
class SnakeTest {

    private Obstacle obstacle;
    private Snake snake;

    @BeforeEach
    void setUp() {
        GameSettings.getInstance().resetDefaults();
        obstacle = new Obstacle(20, 20);
        snake = new Snake(20, 20, false, obstacle);
    }

    @Test
    @DisplayName("Verify initial snake state: centered head, 1 body segment, alive")
    void testInitialSnakeState() {
        assertEquals(new Cell(10, 10), snake.getHead(), "Initial head position should be at (10, 10)");
        assertEquals(1, snake.getBody().size(), "Snake should start with 1 body segment");
        assertEquals(new Cell(11, 10), snake.getBody().get(0), "Initial body segment should be at (11, 10)");
        assertFalse(snake.getDead(), "Snake should be alive initially");
        assertEquals(270, snake.getSnakeDir(), "Initial direction should be 270 (facing LEFT)");
    }

    @Test
    @DisplayName("Verify snake move LEFT updates head and direction")
    void testMoveLeft() {
        snake.move(Direction.LEFT);
        assertEquals(new Cell(9, 10), snake.getHead(), "Head should move left to (9, 10)");
        assertEquals(270, snake.getSnakeDir(), "Direction should be 270 for LEFT");
        assertFalse(snake.getDead(), "Snake should be alive after moving within bounds");
    }

    @Test
    @DisplayName("Verify snake move UP updates head and direction")
    void testMoveUp() {
        snake.move(Direction.UP);
        assertEquals(new Cell(10, 9), snake.getHead(), "Head should move up to (10, 9)");
        assertEquals(0, snake.getSnakeDir(), "Direction should be 0 for UP");
        assertFalse(snake.getDead(), "Snake should be alive after moving within bounds");
    }

    @Test
    @DisplayName("Verify snake move DOWN updates head and direction")
    void testMoveDown() {
        snake.move(Direction.DOWN);
        assertEquals(new Cell(10, 11), snake.getHead(), "Head should move down to (10, 11)");
        assertEquals(180, snake.getSnakeDir(), "Direction should be 180 for DOWN");
        assertFalse(snake.getDead(), "Snake should be alive after moving within bounds");
    }

    @Test
    @DisplayName("Verify snake move RIGHT updates head and direction")
    void testMoveRight() {
        snake.move(Direction.RIGHT);
        assertEquals(new Cell(11, 10), snake.getHead(), "Head should move right to (11, 10)");
        assertEquals(90, snake.getSnakeDir(), "Direction should be 90 for RIGHT");
    }

    @Test
    @DisplayName("Verify eating coin retains tail (snake grows)")
    void testAteCoinGrowth() {
        Coin coin = new Coin(20, 20, snake, obstacle);
        snake.move(Direction.LEFT);
        assertFalse(snake.resolveCoinConsumption(coin), "Should not have eaten coin at different position");
        assertEquals(1, snake.getBody().size(), "Body size remains 1 when no coin is eaten");

        Snake targetSnake = new Snake(20, 20, false, obstacle);
        assertTrue(targetSnake.resolveCoinConsumption(new CoinMock(targetSnake.getHead())), "resolveCoinConsumption should return true when head equals coin position");
        assertEquals(1, targetSnake.getBody().size(), "Body segment count preserved for growth");
    }

    @Test
    @DisplayName("Verify wall collision in Normal mode causes death")
    void testFatalWallCollision() {
        Snake normalSnake = new Snake(4, 4, false, obstacle);
        normalSnake.move(Direction.LEFT); // (1, 2)
        normalSnake.move(Direction.LEFT); // (0, 2)
        normalSnake.move(Direction.LEFT); // moves to (-1, 2), triggers borderControl -> fatal
        assertTrue(normalSnake.getDead(), "Snake must die when colliding with boundary in normal mode");
    }

    @Test
    @DisplayName("Verify wall wrap-around in Easy mode (noBorder = true)")
    void testBorderWrapAround() {
        Snake easySnake = new Snake(4, 4, true, obstacle);
        easySnake.move(Direction.LEFT); // (1, 2)
        easySnake.move(Direction.LEFT); // (0, 2)
        easySnake.move(Direction.LEFT); // Crosses left boundary (x < 0) -> wraps to (3, 2)
        assertFalse(easySnake.getDead(), "Snake should NOT die on border in Easy mode");
        assertEquals(3, easySnake.getHead().x(), "Head X should wrap around to gx - 1 (3)");
        assertEquals(2, easySnake.getHead().y(), "Head Y should remain 2");
    }

    @Test
    @DisplayName("Verify collision with obstacle causes death")
    void testObstacleCollision() {
        Cell obstaclePoint = new Cell(9, 10);
        obstacle.getObstacles().add(obstaclePoint);

        snake.move(Direction.LEFT);
        assertTrue(snake.getDead(), "Snake must die upon hitting an obstacle");
    }

    @Test
    @DisplayName("Verify Hardcore growth rate (2x) increases snake body length by 2 segments per coin")
    void testHardcoreGrowthRate() {
        Snake hardcoreSnake = new Snake(20, 20, false, obstacle, 2);
        assertEquals(2, hardcoreSnake.getGrowthRate());
        assertEquals(1, hardcoreSnake.getBody().size(), "Initial body size is 1");

        CoinMock coin = new CoinMock(hardcoreSnake.getHead());
        assertTrue(hardcoreSnake.resolveCoinConsumption(coin));

        assertEquals(2, hardcoreSnake.getBody().size(), "Body size must increase by 2 segments for 2x growth rate");
    }

    @Test
    @DisplayName("Verify wrap-around onto an edge obstacle causes death")
    void testWrapAroundOntoEdgeObstacleCausesDeath() {
        Obstacle edgeObstacle = new Obstacle(4, 4);
        edgeObstacle.getObstacles().add(new Cell(3, 2));

        Snake wrappingSnake = new Snake(4, 4, true, edgeObstacle);
        wrappingSnake.move(Direction.LEFT);
        wrappingSnake.move(Direction.LEFT);
        assertFalse(wrappingSnake.getDead());
        assertEquals(new Cell(0, 2), wrappingSnake.getHead());

        wrappingSnake.move(Direction.LEFT);
        assertTrue(wrappingSnake.getDead(), "Wrapping onto an edge obstacle must kill the snake");
        assertEquals(new Cell(0, 2), wrappingSnake.getHead(), "Head should revert to pre-collision position (0, 2)");
    }

    @Test
    @DisplayName("Verify tail and body directions are preserved per segment across wrap edges and Hardcore growth")
    void testPerSegmentDirectionAcrossWrapAndHardcoreGrowth() {
        Snake hardcoreSnake = new Snake(10, 10, true, obstacle, 2);
        hardcoreSnake.move(Direction.UP);
        hardcoreSnake.resolveCoinConsumption(new CoinMock(new Cell(0, 0)));
        hardcoreSnake.move(Direction.UP);
        assertTrue(hardcoreSnake.resolveCoinConsumption(new CoinMock(hardcoreSnake.getHead())));
        assertEquals(0, hardcoreSnake.getTailDir(), "Tail direction must remain 0 (UP) when tail cell is duplicated in Hardcore mode");
        assertEquals(0, hardcoreSnake.getBodyDir(1), "Body segment above duplicated tail must remain 0 (vertical), not 270 (horizontal)");

        Snake wrapSnake = new Snake(10, 10, true, obstacle, 1);
        for (int i = 0; i < 5; i++) {
            wrapSnake.move(Direction.UP);
            if (i < 4) {
                wrapSnake.resolveCoinConsumption(new CoinMock(new Cell(9, 9)));
            } else {
                wrapSnake.resolveCoinConsumption(new CoinMock(new Cell(5, 0)));
            }
        }
        wrapSnake.move(Direction.UP);
        wrapSnake.resolveCoinConsumption(new CoinMock(new Cell(9, 9)));
        wrapSnake.move(Direction.UP);
        wrapSnake.resolveCoinConsumption(new CoinMock(new Cell(9, 9)));
        assertEquals(new Cell(5, 0), wrapSnake.getBody().get(0), "Tail should be at top edge (5, 0)");
        assertEquals(new Cell(5, 9), wrapSnake.getBody().get(1), "Segment ahead of tail wrapped to (5, 9)");
        assertEquals(0, wrapSnake.getTailDir(), "Tail straddling top wrap edge must still point UP (0°), not fall back to 90°");
    }

    @Test
    @DisplayName("Verify self-collision in normal mode and duplicated-tail self-collision in Hardcore mode")
    void testSelfCollisionNormalAndHardcore() {
        // 1. Normal mode: grow snake to 4 body segments and steer into its own body
        Snake normalSnake = new Snake(20, 20, false, obstacle, 1);
        // Move LEFT 4 times and eat a coin each step so body size reaches 5
        for (int i = 0; i < 4; i++) {
            normalSnake.move(Direction.LEFT);
            assertTrue(normalSnake.resolveCoinConsumption(new CoinMock(normalSnake.getHead())));
        }
        assertEquals(5, normalSnake.getBody().size());
        // Loop UP -> RIGHT -> DOWN into its own horizontal body
        normalSnake.move(Direction.UP);
        normalSnake.resolveCoinConsumption(new CoinMock(new Cell(0, 0)));
        normalSnake.move(Direction.RIGHT);
        normalSnake.resolveCoinConsumption(new CoinMock(new Cell(0, 0)));
        normalSnake.move(Direction.DOWN);
        assertTrue(normalSnake.getDead(), "Normal snake must die when steering into its own body segment");

        // 2. Hardcore mode (growthRate = 2): duplicated tail cell must NOT be exempt from self-collision
        Snake hardcoreSnake = new Snake(20, 20, false, obstacle, 2);
        // Starts: head at (10, 10), body [(11, 10)]
        // Step 1: Move UP to (10, 9), eat coin -> body becomes [(11, 10), (11, 10), (10, 10)] (size 3)
        hardcoreSnake.move(Direction.UP);
        assertTrue(hardcoreSnake.resolveCoinConsumption(new CoinMock(hardcoreSnake.getHead())));
        // Step 2: Move RIGHT to (11, 9), eat coin -> body becomes [(11, 10), (11, 10), (11, 10), (10, 10), (10, 9)] (size 5)
        // Notice tail cell (11, 10) is duplicated at indices 0, 1, 2!
        hardcoreSnake.move(Direction.RIGHT);
        assertTrue(hardcoreSnake.resolveCoinConsumption(new CoinMock(hardcoreSnake.getHead())));
        assertFalse(hardcoreSnake.getDead());

        // Step 3: Move DOWN from (11, 9) onto (11, 10), where the duplicated tail still sits!
        hardcoreSnake.move(Direction.DOWN);
        assertTrue(hardcoreSnake.getDead(), "Stepping onto a duplicated tail cell in Hardcore mode must cause self-collision death");
    }

    @Test
    @DisplayName("Verify resetPosition resets head to center, stacks preserved body length on spawn tail, and clears dead flag")
    void testResetPosition() {
        // Grow snake to 3 body segments and move away from center
        snake.move(Direction.LEFT);
        snake.resolveCoinConsumption(new CoinMock(snake.getHead()));
        snake.move(Direction.UP);
        snake.resolveCoinConsumption(new CoinMock(snake.getHead()));
        assertEquals(3, snake.getBody().size());
        assertNotEquals(new Cell(10, 10), snake.getHead());

        snake.resetPosition(20, 20);

        assertEquals(new Cell(10, 10), snake.getHead(), "Head must reset to center (10, 10)");
        assertEquals(3, snake.getBody().size(), "Accumulated body length (3) must be preserved");
        for (Cell cell : snake.getBody()) {
            assertEquals(new Cell(11, 10), cell, "All body segments must stack on spawn tail (11, 10)");
        }
        assertEquals(Direction.LEFT, snake.getDirection(), "Direction must reset to LEFT");
        assertEquals(270, snake.getSnakeDir(), "Angle must reset to 270");
        assertFalse(snake.getDead(), "Dead flag must be false after resetPosition");
    }

    /**
     * Helper mock for Coin to test specific coordinates in ateCoin
     */
    private static class CoinMock extends Coin {
        private final Cell fixedPos;

        public CoinMock(Cell pos) {
            super(20, 20, new Snake(20, 20, false, new Obstacle(20, 20)), new Obstacle(20, 20));
            this.fixedPos = pos;
        }

        @Override
        public Cell getPos() {
            return fixedPos;
        }
    }
}
