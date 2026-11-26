package com.comp2042.controller;

import com.comp2042.JavaFXTestBase;
import com.comp2042.model.Difficulty;
import com.comp2042.model.Game;
import com.comp2042.model.Obstacle;
import com.comp2042.model.Snake;
import com.comp2042.model.Coin;
import com.comp2042.view.GameView;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("GameController Turn Validation Unit Tests")
class GameControllerTest extends JavaFXTestBase {

    private GameController controller;
    private GameView gameView;

    @BeforeEach
    void setUp() {
        Game game = new Game(20, 20, false, Difficulty.NORMAL);
        Obstacle obstacle = new Obstacle(20, 20);
        Snake snake = new Snake(20, 20, false, obstacle);
        Coin coin = new Coin(20, 20, snake, obstacle);
        gameView = new GameView(game);

        controller = new GameController(snake, coin, game, gameView, obstacle, null);
    }

    @Test
    @DisplayName("Verify non-arrow keys are strictly rejected as turns")
    void testNonArrowKeysRejected() {
        assertFalse(controller.isValidTurn(KeyCode.SPACE), "Space key should be rejected as a turn");
        assertFalse(controller.isValidTurn(KeyCode.ENTER), "Enter key should be rejected as a turn");
        assertFalse(controller.isValidTurn(KeyCode.A), "Letter keys should be rejected as a turn");
        assertFalse(controller.isValidTurn(KeyCode.ESCAPE), "Escape key should be rejected as a turn");
    }

    @Test
    @DisplayName("Verify reverse direction is blocked when moving LEFT")
    void testReverseBlockedMovingLeft() {
        GameController.key = KeyCode.LEFT;

        // Reversing RIGHT into itself must be blocked
        assertFalse(controller.isValidTurn(KeyCode.RIGHT), "RIGHT must be blocked when moving LEFT");

        // Turning UP or DOWN must be allowed
        assertTrue(controller.isValidTurn(KeyCode.UP), "UP must be allowed when moving LEFT");
        assertTrue(controller.isValidTurn(KeyCode.DOWN), "DOWN must be allowed when moving LEFT");
    }

    @Test
    @DisplayName("Verify reverse direction is blocked when moving UP")
    void testReverseBlockedMovingUp() {
        GameController.key = KeyCode.UP;

        // Reversing DOWN into itself must be blocked (Fix for PR #2 bug)
        assertFalse(controller.isValidTurn(KeyCode.DOWN), "DOWN must be blocked when moving UP");

        // Turning LEFT or RIGHT must be allowed
        assertTrue(controller.isValidTurn(KeyCode.LEFT), "LEFT must be allowed when moving UP");
        assertTrue(controller.isValidTurn(KeyCode.RIGHT), "RIGHT must be allowed when moving UP");
    }

    @Test
    @DisplayName("Verify reverse direction is blocked when moving DOWN")
    void testReverseBlockedMovingDown() {
        GameController.key = KeyCode.DOWN;

        // Reversing UP into itself must be blocked
        assertFalse(controller.isValidTurn(KeyCode.UP), "UP must be blocked when moving DOWN");

        // Turning LEFT or RIGHT must be allowed
        assertTrue(controller.isValidTurn(KeyCode.LEFT), "LEFT must be allowed when moving DOWN");
        assertTrue(controller.isValidTurn(KeyCode.RIGHT), "RIGHT must be allowed when moving DOWN");
    }

    @Test
    @DisplayName("Verify reverse direction is blocked when moving RIGHT")
    void testReverseBlockedMovingRight() {
        GameController.key = KeyCode.RIGHT;

        // Reversing LEFT into itself must be blocked
        assertFalse(controller.isValidTurn(KeyCode.LEFT), "LEFT must be blocked when moving RIGHT");

        // Turning UP or DOWN must be allowed
        assertTrue(controller.isValidTurn(KeyCode.UP), "UP must be allowed when moving RIGHT");
        assertTrue(controller.isValidTurn(KeyCode.DOWN), "DOWN must be allowed when moving RIGHT");
    }

    @Test
    @DisplayName("Verify pause menu toggles visibility properly without prematurely starting movement")
    void testPauseMenuToggling() {
        assertFalse(controller.isGameStarted(), "Game should not be started before arrow key");
        assertFalse(controller.isPauseMenuVisible(), "Pause menu should initially be hidden");

        controller.showPauseMenu();
        assertTrue(controller.isPauseMenuVisible(), "Pause menu should be visible after showPauseMenu()");

        // Hiding pause menu before arrow key must NOT start the snake moving
        controller.hidePauseMenu();
        assertFalse(controller.isPauseMenuVisible(), "Pause menu should be hidden after hidePauseMenu()");
        assertFalse(controller.isGameStarted(), "Game should still not be started after unpausing if no arrow key was pressed");

        controller.togglePauseMenu();
        assertTrue(controller.isPauseMenuVisible(), "Pause menu should be visible after toggle");

        controller.togglePauseMenu();
        assertFalse(controller.isPauseMenuVisible(), "Pause menu should be hidden after second toggle");
        assertFalse(controller.isGameStarted());
        controller.stopAnimation();
    }

    @Test
    @DisplayName("Verify G key toggles GameSettings gridEnabled")
    void testGridToggleWithGKey() {
        com.comp2042.model.GameSettings.getInstance().setGridEnabled(true);
        assertTrue(com.comp2042.model.GameSettings.getInstance().isGridEnabled());

        controller.handle(new javafx.scene.input.KeyEvent(
                javafx.scene.input.KeyEvent.KEY_PRESSED, "", "", KeyCode.G, false, false, false, false
        ));
        assertFalse(com.comp2042.model.GameSettings.getInstance().isGridEnabled(), "G key should toggle grid to false");

        controller.handle(new javafx.scene.input.KeyEvent(
                javafx.scene.input.KeyEvent.KEY_PRESSED, "", "", KeyCode.G, false, false, false, false
        ));
        assertTrue(com.comp2042.model.GameSettings.getInstance().isGridEnabled(), "G key should toggle grid back to true");
    }

    @Test
    @DisplayName("Verify handleResize invokes fitToWindow without throwing exceptions")
    void testHandleResize() {
        assertDoesNotThrow(() -> controller.handleResize(1200, 800));
    }

    @Test
    @DisplayName("Verify top-right ? help button in pause menu opens and closes How to Play screen")
    void testPauseHelpButtonOverlay() {
        controller.showPauseMenu();
        assertTrue(controller.isPauseMenuVisible());
        assertFalse(gameView.isPauseHelpVisible(), "Help screen should initially be hidden when pause menu opens");
        assertTrue(gameView.getPauseHelpButton().isVisible(), "Top-right ? button should be visible on pause menu");

        gameView.getPauseHelpButton().fire();
        assertTrue(gameView.isPauseHelpVisible(), "Clicking ? button should open How to Play screen");
        assertFalse(gameView.getPauseHelpButton().isVisible(), "? button should hide while How to Play is open");

        gameView.getHelpBackButton().fire();
        assertFalse(gameView.isPauseHelpVisible(), "Clicking Back should close How to Play screen");
        assertTrue(gameView.getPauseHelpButton().isVisible(), "? button should be visible again on pause menu");
    }

    @Test
    @DisplayName("Verify arrow keys do not start animation after game is won")
    void testArrowKeysIgnoredWhenGameWon() {
        Game wonGame = new Game(20, 20, false, Difficulty.EASY);
        // Complete all 3 levels in Quick/Easy mode (6, 8, 10 coins)
        for (int i = 0; i < 6; i++) wonGame.incScore();
        wonGame.advanceLevel();
        for (int i = 0; i < 8; i++) wonGame.incScore();
        wonGame.advanceLevel();
        for (int i = 0; i < 10; i++) wonGame.incScore();
        assertTrue(wonGame.gameWon(), "Game should be marked as won");

        Obstacle obs = new Obstacle(20, 20);
        Snake snk = new Snake(20, 20, true, obs);
        Coin cn = new Coin(20, 20, snk, obs);
        GameView gv = new GameView(wonGame);
        GameController wonController = new GameController(snk, cn, wonGame, gv, obs, null);

        wonController.handle(new javafx.scene.input.KeyEvent(
                javafx.scene.input.KeyEvent.KEY_PRESSED, "", "", KeyCode.LEFT, false, false, false, false
        ));
        assertFalse(wonController.isAnimationRunning(), "Animation must not start or run when game is already won");
    }

    @Test
    @DisplayName("Verify loadLevel pauses game in LEVEL_CLEARED, resets head to center spawn, and stacks body segments to uncoil")
    void testLoadLevelPausesAndStacksSnakeOnSpawnToUncoil() {
        Game game = new Game(20, 20, false, Difficulty.EASY);
        Obstacle obs = new Obstacle(20, 20);
        Snake snk = new Snake(20, 20, true, obs);
        // Move and grow the snake so it is away from center and has 3 body segments
        snk.move('L'); // head at (9, 10), body has (10, 10) and (11, 10) -> size 2
        snk.move('U'); // head at (9, 9), body has (9, 10), (10, 10), (11, 10) -> size 3
        int bodySizeBeforeNewLevel = snk.getBody().size();
        assertEquals(3, bodySizeBeforeNewLevel);

        Coin cn = new Coin(20, 20, snk, obs);
        GameView gv = new GameView(game);
        GameController ctrl = new GameController(snk, cn, game, gv, obs, null);

        // Start the game first
        ctrl.handle(new javafx.scene.input.KeyEvent(
                javafx.scene.input.KeyEvent.KEY_PRESSED, "", "", KeyCode.UP, false, false, false, false
        ));
        assertEquals(com.comp2042.model.GameState.RUNNING, ctrl.getGameState());

        // Advance to Level 2
        ctrl.loadLevel(2);

        // Game must pause in LEVEL_CLEARED state and all 16 Level 2 obstacles must spawn intact
        assertEquals(com.comp2042.model.GameState.LEVEL_CLEARED, ctrl.getGameState(), "State must be LEVEL_CLEARED");
        assertFalse(ctrl.isAnimationRunning(), "Timeline animation must stop when entering a new level");
        assertEquals(16, obs.getObstacles().size(), "All 16 Level 2 pillar obstacles must spawn intact");

        // Snake head must be at center spawn (10, 10), with all 3 body segments stacked on spawn block (11, 10)
        assertEquals(new java.awt.Point(10, 10), snk.getHead(), "Snake head must reset to center spawn (10, 10)");
        assertEquals(bodySizeBeforeNewLevel, snk.getBody().size(), "Snake length must not reset on new level");
        for (java.awt.Point segment : snk.getBody()) {
            assertEquals(new java.awt.Point(11, 10), segment, "All body segments must initially be stacked on spawn block (11, 10)");
        }

        // Pressing a valid arrow key unpauses and resumes movement
        ctrl.handle(new javafx.scene.input.KeyEvent(
                javafx.scene.input.KeyEvent.KEY_PRESSED, "", "", KeyCode.LEFT, false, false, false, false
        ));
        assertEquals(com.comp2042.model.GameState.RUNNING, ctrl.getGameState(), "Arrow key should resume game to RUNNING");
        ctrl.stopAnimation();
    }

    @Test
    @DisplayName("Verify TAB reset preserves the full HUD format on scoreLabel instead of overwriting with 'Score: 0'")
    void testTabResetPreservesFullHudText() {
        controller.handle(new javafx.scene.input.KeyEvent(
                javafx.scene.input.KeyEvent.KEY_PRESSED, "", "", KeyCode.TAB, false, false, false, false
        ));
        String hudText = gameView.scoreLabel.getText();
        assertNotEquals("Score: 0", hudText, "TAB must not overwrite HUD with legacy 'Score: 0'");
        assertTrue(hudText.contains("[Classic Mode] Level 1/3 | Coins: 0/8 | Score: 0"),
                "TAB reset should display full HUD string, got: " + hudText);
    }

    @Test
    @DisplayName("Verify turnArray is sized from game dimensions and cleared on TAB reset and loadLevel")
    void testTurnArrayClearedOnResetAndNewLevel() {
        // Turn UP at initial head position (10, 10)
        controller.handle(new javafx.scene.input.KeyEvent(
                javafx.scene.input.KeyEvent.KEY_PRESSED, "", "", KeyCode.UP, false, false, false, false
        ));
        controller.stopAnimation();
        assertNotEquals(0, gameView.getTurnAt(10, 10), "Turn at (10, 10) should be recorded in turnArray");

        // Pressing TAB reset must clear turnArray
        controller.handle(new javafx.scene.input.KeyEvent(
                javafx.scene.input.KeyEvent.KEY_PRESSED, "", "", KeyCode.TAB, false, false, false, false
        ));
        assertEquals(0, gameView.getTurnAt(10, 10), "turnArray must be cleared on TAB reset");

        // Record another turn at (10, 10), then call loadLevel(2)
        controller.handle(new javafx.scene.input.KeyEvent(
                javafx.scene.input.KeyEvent.KEY_PRESSED, "", "", KeyCode.DOWN, false, false, false, false
        ));
        controller.stopAnimation();
        assertNotEquals(0, gameView.getTurnAt(10, 10));

        controller.loadLevel(2);
        assertEquals(0, gameView.getTurnAt(10, 10), "turnArray must be cleared when advancing to a new level");

        // Verify custom game dimensions (e.g. 40x30) size turnArray properly without MainMenuController coupling
        Game customGame = new Game(40, 30, false, Difficulty.NORMAL);
        GameView customView = new GameView(customGame);
        assertEquals(0, customView.getTurnAt(39, 29));
    }

    @Test
    @DisplayName("Verify two-slot input queue buffers rapid second turn within one tick for smooth U-turns")
    void testTwoSlotInputQueueForRapidUTurn() {
        // Initially facing LEFT; press UP then immediately RIGHT within the same tick
        controller.handle(new javafx.scene.input.KeyEvent(
                javafx.scene.input.KeyEvent.KEY_PRESSED, "", "", KeyCode.UP, false, false, false, false
        ));
        controller.stopAnimation();
        assertEquals(KeyCode.UP, GameController.key, "First input (UP) should apply immediately for the current tick");
        assertEquals(0, controller.getQueuedInputCount(), "Queue is empty after first input applies");

        // Second rapid input (RIGHT) before onTick() runs must be buffered in inputQueue, not dropped
        controller.handle(new javafx.scene.input.KeyEvent(
                javafx.scene.input.KeyEvent.KEY_PRESSED, "", "", KeyCode.RIGHT, false, false, false, false
        ));
        assertEquals(1, controller.getQueuedInputCount(), "Second rapid input (RIGHT) must be buffered in queue");

        // Tick 1 moves UP and clears keyPressed; Tick 2 dequeues RIGHT and moves RIGHT
        controller.onTick();
        assertEquals(KeyCode.UP, GameController.key, "Tick 1 should move UP");
        assertEquals(1, controller.getQueuedInputCount(), "RIGHT remains queued for Tick 2");

        controller.onTick();
        assertEquals(KeyCode.RIGHT, GameController.key, "Tick 2 must dequeue and apply RIGHT to complete the U-turn");
        assertEquals(0, controller.getQueuedInputCount(), "Input queue should now be empty");
        controller.stopAnimation();
    }
}
