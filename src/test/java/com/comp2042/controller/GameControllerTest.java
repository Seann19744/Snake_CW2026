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
}
