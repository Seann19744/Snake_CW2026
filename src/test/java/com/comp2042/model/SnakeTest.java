package com.comp2042.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.Point;

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
        // Head should be at (20/2, 20/2) = (10, 10)
        assertEquals(new Point(10, 10), snake.getHead(), "Initial head position should be at (10, 10)");
        assertEquals(1, snake.getBody().size(), "Snake should start with 1 body segment");
        assertEquals(new Point(11, 10), snake.getBody().get(0), "Initial body segment should be at (11, 10)");
        assertFalse(snake.getDead(), "Snake should be alive initially");
        assertEquals(270, snake.getSnakeDir(), "Initial direction should be 270 (facing LEFT)");
    }

    @Test
    @DisplayName("Verify snake move LEFT updates head and direction")
    void testMoveLeft() {
        snake.move('L');
        assertEquals(new Point(9, 10), snake.getHead(), "Head should move left to (9, 10)");
        assertEquals(270, snake.getSnakeDir(), "Direction should be 270 for LEFT");
        assertFalse(snake.getDead(), "Snake should be alive after moving within bounds");
    }

    @Test
    @DisplayName("Verify snake move UP updates head and direction")
    void testMoveUp() {
        snake.move('U');
        assertEquals(new Point(10, 9), snake.getHead(), "Head should move up to (10, 9)");
        assertEquals(0, snake.getSnakeDir(), "Direction should be 0 for UP");
        assertFalse(snake.getDead(), "Snake should be alive after moving within bounds");
    }

    @Test
    @DisplayName("Verify snake move DOWN updates head and direction")
    void testMoveDown() {
        snake.move('D');
        assertEquals(new Point(10, 11), snake.getHead(), "Head should move down to (10, 11)");
        assertEquals(180, snake.getSnakeDir(), "Direction should be 180 for DOWN");
        assertFalse(snake.getDead(), "Snake should be alive after moving within bounds");
    }

    @Test
    @DisplayName("Verify snake move RIGHT updates head and direction")
    void testMoveRight() {
        snake.move('R');
        assertEquals(new Point(11, 10), snake.getHead(), "Head should move right to (11, 10)");
        assertEquals(90, snake.getSnakeDir(), "Direction should be 90 for RIGHT");
    }

    @Test
    @DisplayName("Verify eating coin retains tail (snake grows)")
    void testAteCoinGrowth() {
        Coin coin = new Coin(20, 20, snake, obstacle);
        // Move snake without eating: body size remains 1 because ateCoin removes tail
        snake.move('L');
        assertFalse(snake.ateCoin(coin), "Should not have eaten coin at different position");
        assertEquals(1, snake.getBody().size(), "Body size remains 1 when no coin is eaten");

        // Manually position head on coin to test eating mechanics
        Point coinPos = coin.getPos();
        // Create snake with custom position by moving towards coin
        Snake targetSnake = new Snake(20, 20, false, obstacle);
        // If head equals coin position
        assertTrue(targetSnake.ateCoin(new CoinMock(targetSnake.getHead())), "ateCoin should return true when head equals coin position");
        // Body was NOT removed, so growth occurs
        assertEquals(1, targetSnake.getBody().size(), "Body segment count preserved for growth");
    }

    @Test
    @DisplayName("Verify wall collision in Normal mode causes death")
    void testFatalWallCollision() {
        Snake normalSnake = new Snake(4, 4, false, obstacle);
        // Starts at (2, 2)
        normalSnake.move('L'); // (1, 2)
        normalSnake.move('L'); // (0, 2)
        normalSnake.move('L'); // moves to (-1, 2), triggers borderControl -> fatal
        assertTrue(normalSnake.getDead(), "Snake must die when colliding with boundary in normal mode");
    }

    @Test
    @DisplayName("Verify wall wrap-around in Easy mode (noBorder = true)")
    void testBorderWrapAround() {
        Snake easySnake = new Snake(4, 4, true, obstacle);
        // Starts at (2, 2)
        easySnake.move('L'); // (1, 2)
        easySnake.move('L'); // (0, 2)
        easySnake.move('L'); // Crosses left boundary (x < 0) -> wraps to (3, 2)
        assertFalse(easySnake.getDead(), "Snake should NOT die on border in Easy mode");
        assertEquals(3.0, easySnake.getHead().getX(), "Head X should wrap around to gx - 1 (3)");
        assertEquals(2.0, easySnake.getHead().getY(), "Head Y should remain 2");
    }

    @Test
    @DisplayName("Verify collision with obstacle causes death")
    void testObstacleCollision() {
        Point obstaclePoint = new Point(9, 10);
        obstacle.getObstacles().add(obstaclePoint);

        // Snake starts at (10, 10) and moves LEFT to (9, 10)
        snake.move('L');
        assertTrue(snake.getDead(), "Snake must die upon hitting an obstacle");
    }

    @Test
    @DisplayName("Verify Hardcore growth rate (2x) increases snake body length by 2 segments per coin")
    void testHardcoreGrowthRate() {
        Snake hardcoreSnake = new Snake(20, 20, false, obstacle, 2);
        assertEquals(2, hardcoreSnake.getGrowthRate());
        assertEquals(1, hardcoreSnake.getBody().size(), "Initial body size is 1");

        CoinMock coin = new CoinMock(hardcoreSnake.getHead());
        assertTrue(hardcoreSnake.ateCoin(coin));

        // After eating coin with growthRate = 2, body should have 2 segments (growth of +2)
        assertEquals(2, hardcoreSnake.getBody().size(), "Body size must increase by 2 segments for 2x growth rate");
    }

    @Test
    @DisplayName("Verify wrap-around onto an edge obstacle causes death")
    void testWrapAroundOntoEdgeObstacleCausesDeath() {
        Obstacle edgeObstacle = new Obstacle(4, 4);
        // Place obstacle at right edge (3, 2)
        edgeObstacle.getObstacles().add(new Point(3, 2));

        Snake wrappingSnake = new Snake(4, 4, true, edgeObstacle);
        // Starts at (2, 2) -> move LEFT to (1, 2) -> move LEFT to (0, 2)
        wrappingSnake.move('L');
        wrappingSnake.move('L');
        assertFalse(wrappingSnake.getDead());
        assertEquals(new Point(0, 2), wrappingSnake.getHead());

        // Move LEFT from (0, 2): wraps around to (3, 2), which has an obstacle!
        wrappingSnake.move('L');
        assertTrue(wrappingSnake.getDead(), "Wrapping onto an edge obstacle must kill the snake");
        assertEquals(new Point(0, 2), wrappingSnake.getHead(), "Head should revert to pre-collision position (0, 2)");
    }

    @Test
    @DisplayName("Verify tail and body directions are preserved per segment across wrap edges and Hardcore growth")
    void testPerSegmentDirectionAcrossWrapAndHardcoreGrowth() {
        // 1. Test vertical movement in Hardcore mode (+2 growth duplicating tail cell)
        Snake hardcoreSnake = new Snake(10, 10, true, obstacle, 2);
        // Move UP twice so the entire snake is aligned vertically facing UP (0 degrees)
        hardcoreSnake.move('U');
        hardcoreSnake.ateCoin(new CoinMock(new Point(0, 0))); // didn't eat
        hardcoreSnake.move('U');
        // Eat a coin while moving UP: tail cell is duplicated for 2x growth
        assertTrue(hardcoreSnake.ateCoin(new CoinMock(hardcoreSnake.getHead())));
        assertEquals(0, hardcoreSnake.getTailDir(), "Tail direction must remain 0 (UP) when tail cell is duplicated in Hardcore mode");
        assertEquals(0, hardcoreSnake.getBodyDir(1), "Body segment above duplicated tail must remain 0 (vertical), not 270 (horizontal)");

        // 2. Test tail direction when straddling a top wrap edge (y = 0 -> y = 9)
        Snake wrapSnake = new Snake(10, 10, true, obstacle, 1);
        // Head starts at (5, 5). Move UP to y = 0 and eat a coin so body length is 2
        for (int i = 0; i < 5; i++) {
            wrapSnake.move('U');
            if (i < 4) {
                wrapSnake.ateCoin(new CoinMock(new Point(9, 9)));
            } else {
                // Eat coin at (5, 0) so snake has 2 body segments: tail at (5, 2), neck at (5, 1), head at (5, 0)
                wrapSnake.ateCoin(new CoinMock(new Point(5, 0)));
            }
        }
        // Move UP across the top border: head wraps to (5, 9), neck is at (5, 0), tail is at (5, 1)
        wrapSnake.move('U');
        wrapSnake.ateCoin(new CoinMock(new Point(9, 9)));
        // Move UP again: head is at (5, 8), neck is at (5, 9), tail is at (5, 0) straddling the wrap edge!
        wrapSnake.move('U');
        wrapSnake.ateCoin(new CoinMock(new Point(9, 9)));
        assertEquals(new Point(5, 0), wrapSnake.getBody().get(0), "Tail should be at top edge (5, 0)");
        assertEquals(new Point(5, 9), wrapSnake.getBody().get(1), "Segment ahead of tail wrapped to (5, 9)");
        assertEquals(0, wrapSnake.getTailDir(), "Tail straddling top wrap edge must still point UP (0°), not fall back to 90°");
    }

    /**
     * Helper mock for Coin to test specific coordinates in ateCoin
     */
    private static class CoinMock extends Coin {
        private final Point fixedPos;

        public CoinMock(Point pos) {
            super(20, 20, new Snake(20, 20, false, new Obstacle(20, 20)), new Obstacle(20, 20));
            this.fixedPos = pos;
        }

        @Override
        public Point getPos() {
            return new Point(fixedPos);
        }
    }
}
