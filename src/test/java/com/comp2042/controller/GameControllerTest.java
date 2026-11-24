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

    @BeforeEach
    void setUp() {
        Game game = new Game(20, 20, false, Difficulty.NORMAL);
        Obstacle obstacle = new Obstacle(20, 20);
        Snake snake = new Snake(20, 20, false, obstacle);
        Coin coin = new Coin(20, 20, snake, obstacle);
        GameView gameView = new GameView(game);

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
    @DisplayName("Verify pause menu toggles visibility properly")
    void testPauseMenuToggling() {
        assertFalse(controller.isPauseMenuVisible(), "Pause menu should initially be hidden");

        controller.showPauseMenu();
        assertTrue(controller.isPauseMenuVisible(), "Pause menu should be visible after showPauseMenu()");

        controller.hidePauseMenu();
        assertFalse(controller.isPauseMenuVisible(), "Pause menu should be hidden after hidePauseMenu()");

        controller.togglePauseMenu();
        assertTrue(controller.isPauseMenuVisible(), "Pause menu should be visible after toggle");

        controller.togglePauseMenu();
        assertFalse(controller.isPauseMenuVisible(), "Pause menu should be hidden after second toggle");
        controller.stopAnimation();
    }
}
