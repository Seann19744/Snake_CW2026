package com.comp2042.controller;

import com.comp2042.model.*;
import com.comp2042.view.GameView;
import javafx.scene.media.AudioClip;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Random;

public class GameController {
    private Snake snake;
    private Coin coin;
    private GameView gameView;
    private Game game;
    private Stage stage;
    private Obstacle obstacle;
    private final CoinGenerator coinGenerator;

    public static KeyCode key = KeyCode.LEFT;
    public static KeyCode lastKey = KeyCode.LEFT;
    private boolean keyPressed = false;
    private GameState gameState = GameState.READY;
    private GameState prePauseState = GameState.READY;

    private final AudioClip coinSound = new AudioClip(
            getClass().getResource("/resources/views/res/coinSound.wav").toExternalForm()
    );

    public GameController(Snake snake, Coin coin, Game game, GameView gameView, Obstacle obstacle, Stage stage) {
        this(snake, coin, game, gameView, obstacle, stage, new CoinGenerator());
    }

    public GameController(Snake snake, Coin coin, Game game, GameView gameView, Obstacle obstacle, Stage stage, CoinGenerator coinGenerator) {
        this.snake = snake;
        this.coin = coin;
        this.gameView = gameView;
        this.game = game;
        this.stage = stage;
        this.obstacle = obstacle;
        this.coinGenerator = coinGenerator;

        this.snake.setGrowthRate(game.getGameMode().getGrowthRate());
        this.obstacle.loadLevelLayout(game.getCurrentLevel());

        if (this.snake.getDead()) {
            this.gameState = GameState.DEAD;
        } else if (this.game.gameWon()) {
            this.gameState = GameState.WON;
        } else {
            this.gameState = GameState.READY;
        }
        this.prePauseState = this.gameState;

        this.gameView.getResumeButton().setOnAction(e -> hidePauseMenu());
        this.gameView.getReturnMenuButton().setOnAction(e -> returnToMainMenu());

        animation.setCycleCount(Animation.INDEFINITE);
        animation.setRate(game.getSpeed());

        key = KeyCode.LEFT;
        lastKey = KeyCode.LEFT;
    }

    private final Timeline animation = new Timeline(new KeyFrame(Duration.seconds(1), event -> onTick()));

    void onTick() {
        if (snake.getDead()) {
            gameState = GameState.DEAD;
            animation.stop();
            return;
        }
        if (game.gameWon() || gameState != GameState.RUNNING) {
            if (game.gameWon()) {
                gameState = GameState.WON;
            }
            animation.stop();
            return;
        }

        if (key.equals(KeyCode.LEFT)) {
            snake.move('L');
        } else if (key.equals(KeyCode.RIGHT)) {
            snake.move('R');
        } else if (key.equals(KeyCode.UP)) {
            snake.move('U');
        } else if (key.equals(KeyCode.DOWN)) {
            snake.move('D');
        }

        if (!snake.getDead() && snake.ateCoin(coin)) {
            coinSound.play();
            powerCoin();
            game.incScore();
            if (game.isLevelCompleted()) {
                if (game.advanceLevel()) {
                    loadLevel(game.getCurrentLevel());
                    return;
                } else {
                    gameState = GameState.WON;
                    animation.stop();
                    gameView.drawGrid(coin, snake, obstacle, gameView.gc, false, 0);
                    gameView.gameWon();
                    gameView.updateScoreLabel();
                    return;
                }
            } else {
                newCoin();
            }
        }

        keyPressed = false;

        if (!snake.getDead() || snake.hitSelf()) {
            gameView.drawGrid(coin, snake, obstacle, gameView.gc, false, 0);
        }

        if (snake.getDead()) {
            gameState = GameState.DEAD;
            animation.stop();
        }

        gameView.gameWon();
        gameView.gameOver(snake);
        gameView.updateScoreLabel();
    }

    /**
     * Runs everytime a key is pressed while game window is focused.
     * connected in MainMenuController.java ( gameScene.setOnKeyPressed(controller::handle); )
     * @param event which key was pressed.
     */
    public void handle(KeyEvent event) {
        KeyCode code = event.getCode();

        // Pause Menu toggle with ESC or P
        if (code.equals(KeyCode.ESCAPE) || code.equals(KeyCode.P)) {
            togglePauseMenu();
            event.consume();
            return;
        }

        // If game is paused, ignore other input
        if (gameView.isPauseMenuVisible()) {
            return;
        }

        // Fullscreen toggle with F11
        if (code.equals(KeyCode.F11)) {
            gameView.toggleFullScreen(stage, obstacle, snake, coin);
            event.consume();
            return;
        }

        // Grid toggle shortcut with G
        if (code.equals(KeyCode.G)) {
            GameSettings.getInstance().toggleGrid();
            gameView.updateGridButtonText();
            gameView.redrawGrid();
            event.consume();
            return;
        }

        // Arrow keys to start/resume movement from READY or LEVEL_CLEARED and steer the snake
        if (isValidTurn(code)) {
            if (!keyPressed && !code.equals(key)) {
                lastKey = key;
                key = code;
                keyPressed = true;
                gameView.turnDir(snake);
            }
            if ((gameState == GameState.READY || gameState == GameState.LEVEL_CLEARED)
                    && !snake.getDead() && !game.gameWon()) {
                gameState = GameState.RUNNING;
                animation.play();
            }
        }

        // Reset the game
        if (code.equals(KeyCode.TAB)) {
            reset();
        }
    }

    public boolean isGameStarted() {
        return gameState == GameState.RUNNING;
    }

    public GameState getGameState() {
        return gameState;
    }

    public boolean isPauseMenuVisible() {
        return gameView.isPauseMenuVisible();
    }

    public void togglePauseMenu() {
        if (gameView.isPauseMenuVisible()) {
            hidePauseMenu();
        } else {
            showPauseMenu();
        }
    }

    public void showPauseMenu() {
        if (gameState != GameState.PAUSED) {
            prePauseState = gameState;
            gameState = GameState.PAUSED;
        }
        animation.stop();
        gameView.showPauseMenu(true);
    }

    public boolean isAnimationRunning() {
        return animation.getStatus() == Animation.Status.RUNNING;
    }

    public void stopAnimation() {
        animation.stop();
    }

    public void hidePauseMenu() {
        gameView.showPauseMenu(false);
        if (gameState == GameState.PAUSED) {
            gameState = prePauseState;
        }
        if (gameState == GameState.RUNNING && !snake.getDead() && !game.gameWon()) {
            animation.play();
        }
    }

    public void returnToMainMenu() {
        animation.stop();
        if (stage != null) {
            stage.close();
        }
        MainMenuController mainMenuController = new MainMenuController();
        mainMenuController.getViewManager().getPrimaryStage().show();
    }

    public void handleResize(double width, double height) {
        gameView.fitToWindow(width, height, obstacle, snake, coin);
    }

    /**
     * Checks whether a pressed key is an allowed direction change.
     * Only arrow keys are accepted, and the snake cannot reverse onto itself
     * (for example, DOWN is rejected while moving UP)
     *
     * @param key the key the player just used
     * @return true if the snake may turn in the direction, false otherwise.
     */
    boolean isValidTurn(KeyCode key) {
        // Rejects any key that is not arrow key button (Up, Down, Left, Right)
        if (!key.isArrowKey()) return false;
        else {
            // Checks the opposite direction of the snake moving before moving the snake
            // This is so that the snake does not overlap itself
            if (key.equals(KeyCode.LEFT)) if (!this.key.equals(KeyCode.RIGHT)) return true;
            if (key.equals(KeyCode.RIGHT)) if (!this.key.equals(KeyCode.LEFT)) return true;
            if (key.equals(KeyCode.DOWN)) if (!this.key.equals(KeyCode.UP)) return true;
            if (key.equals(KeyCode.UP)) return !this.key.equals(KeyCode.DOWN);
            // Safety net to ignore invalid turns
            return false;
        }
    }

    private void reset() {

        animation.jumpTo(Duration.ZERO);
        animation.stop();

        game.resetScore();
        obstacle.loadLevelLayout(1);
        snake = new Snake(game.getWidth(), game.getHeight(), game.isNoBorder(), obstacle, game.getGameMode().getGrowthRate());
        coin = coinGenerator.generateCoin(game.getWidth(), game.getHeight(), snake, obstacle, false);

        animation.setRate(game.getSpeed());
        key = KeyCode.LEFT;
        lastKey = KeyCode.LEFT;
        keyPressed = false;
        gameState = GameState.READY;
        prePauseState = GameState.READY;

        gameView.showPauseMenu(false);
        gameView.gameWon();
        gameView.gameOver(snake);
        gameView.updateScoreLabel();

        gameView.drawGrid(coin, snake, obstacle, gameView.gc, false, 0);
    }

    void loadLevel(int level) {
        animation.stop();
        gameState = GameState.LEVEL_CLEARED;
        prePauseState = GameState.LEVEL_CLEARED;
        key = KeyCode.LEFT;
        lastKey = KeyCode.LEFT;
        keyPressed = false;
        obstacle.loadLevelLayout(level);
        snake.resetPosition(game.getWidth(), game.getHeight());
        newCoin();
        gameView.updateScoreLabel();
        gameView.drawGrid(coin, snake, obstacle, gameView.gc, false, 0);
    }

    private void powerCoin() {
        coin.applyEffect(game, snake, obstacle, animation);
    }

    private void newCoin() {
        coin = coinGenerator.generateCoin(game.getWidth(), game.getHeight(), snake, obstacle, game.isPowerUp());
    }
}
