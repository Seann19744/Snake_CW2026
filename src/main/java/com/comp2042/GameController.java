package com.comp2042;

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
    private Snake snek;
    private Coin coin;
    private GameView gameView;
    private Game game;
    private Stage stage;
    private Obstacle obstacle;

    public static KeyCode key = KeyCode.LEFT;
    public static KeyCode lastKey = KeyCode.LEFT;
    private boolean keyPressed = false;

    private final AudioClip coinSound = new AudioClip(
            getClass().getResource("/resources/views/res/coinSound.wav").toExternalForm()
    );

    public GameController(Snake snek, Coin coin, Game game, GameView gameView, Obstacle obstacle, Stage stage) {
        this.snek = snek;
        this.coin = coin;
        this.gameView = gameView;
        this.game = game;
        this.stage = stage;

        this.obstacle = obstacle;
        animation.setCycleCount(Animation.INDEFINITE);
        animation.setRate(game.getSpeed());
    }

    private final Timeline animation = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
        if (snek.getDead()) {
            return;
        } else {

            if (key.equals(KeyCode.LEFT)) {
                snek.move('L');
            } else if (key.equals(KeyCode.RIGHT)) {
                snek.move('R');
            } else if (key.equals(KeyCode.UP)) {
                snek.move('U');
            } else if (key.equals(KeyCode.DOWN)) {
                snek.move('D');
            }

            if (!snek.getDead() && snek.ateCoin(coin)) {
                coinSound.play();
                powerCoin();
                game.incScore();
                if (game.gameWon()) {
                    gameView.drawGrid(coin, snek, obstacle, gameView.gc, false, 0);
                    return;
                }
                newCoin();
            }

            keyPressed = false;

            if (!snek.getDead() || snek.hitSelf()) {
                gameView.drawGrid(coin, snek, obstacle, gameView.gc, false, 0);
            }

            gameView.gameWon();
            gameView.gameOver(snek);
            gameView.updateScoreLabel();
        }
    }));

    /**
     * Runs everytime a key is pressed while game window is focused.
     * connected in MainMenuController.java ( gameScene.setOnKeyPressed(controller::handle); )
     * @param event which key was pressed.
     */
    public void handle(KeyEvent event) {

        // Arrow keys to start the game
        if (event.getCode().isArrowKey()) animation.play();

        // Snake changing direction using method valKey
        if (valKey(event.getCode()) && !keyPressed) {
            lastKey = key;
            key = event.getCode();
            keyPressed = true;
            gameView.turnDir(snek);
        }

        // Pause the game
        if (event.getCode().equals(KeyCode.P)) {
            animation.stop();
        }

        // Reset the game
        if (event.getCode().equals(KeyCode.TAB)) {
            reset();
            gameView.scoreLabel.setText("Score: " + game.getScorePoints());
        }

        // Fullscreen
        if (event.getCode().equals(KeyCode.PLUS)
                || event.getCode().equals(KeyCode.EQUALS)
                || event.getCode().equals(KeyCode.ADD)) {

            gameView.scaleToFullscreen(true, stage, obstacle, snek, coin);
            gameView.gameOverLabel.setStyle("-fx-font-size: 50;");
            gameView.gameWonLabel.setStyle("-fx-font-size: 50;");
            gameView.scoreLabel.setStyle("-fx-font-size: 35;");
        }

        // Minimize screen
        if (event.getCode().equals(KeyCode.MINUS)
                || event.getCode().equals(KeyCode.SUBTRACT)) {

            gameView.scaleToFullscreen(false, stage, obstacle, snek, coin);
            gameView.gameOverLabel.setStyle("-fx-font-size: 15;");
            gameView.gameWonLabel.setStyle("-fx-font-size: 15;");
            gameView.scoreLabel.setStyle("-fx-font-size: 15;");
        }

        // Goes back to main menu
        if (event.getCode().equals(KeyCode.ESCAPE)) {
            MainMenuController mainMenuController = new MainMenuController();
            mainMenuController.getViewManager().getPrimaryStage().show();
            mainMenuController.getViewManager().getGameStage().close();
        }
    }

    /**
     * Checks whether a pressed key is an allowed direction change.
     * Only arrow keys are accepted, and the snake cannot reverse onto itself
     * (for example, DOWN is rejected while moving UP)
     *
     * @param key the key the player just used
     * @return true if the snake may turn in the direction, false otherwise.
     */
    private boolean valKey(KeyCode key) {
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

        snek = new Snake(game.getWidth(), game.getHeight(), game.isNoBorder(), obstacle);
        coin = new Coin(game.getWidth(), game.getHeight(), snek, obstacle);

        animation.setRate(game.getSpeed());
        key = KeyCode.LEFT;
        keyPressed = false;
        game.resetScore();
        obstacle.reset();

        gameView.gameWon();
        gameView.gameOver(snek);
        gameView.updateScoreLabel();

        gameView.drawGrid(coin, snek, obstacle, gameView.gc, false, 0);
    }

    private void powerCoin() {
        animation.setRate(game.getSpeed());
        snek.ignoreBorders(false);
        if (coin.getClass().equals(RainbowCoin.class)) {
            RainbowCoin tempCoin = (RainbowCoin) coin;
            animation.setRate(game.getSpeed() + tempCoin.getSpeedBonus());
        } else if (coin.getClass().equals(SilverCoin.class)){
            Random rnd = new Random();
            obstacle.newObstacles(snek, coin, rnd.nextInt(game.getWidth() * game.getHeight()/ 20) +3);
            game.decMaxScore(obstacle.getObstacles().size());
        } else if (coin.getClass().equals(BlueCoin.class)) {
            snek.ignoreBorders(true);
        }
        game.addPoints(coin.getPoints());
    }

    private void newCoin() {
        if (game.isPowerUp()) {
            Random rnd = new Random();
            int ran = rnd.nextInt(12);
            if (ran == 1) {
                coin = new GoldCoin(game.getWidth(), game.getHeight(), snek, obstacle);
            } else if (ran == 2) {
                coin = new RainbowCoin(game.getWidth(), game.getHeight(), snek, obstacle);
            } else if (ran == 3) {
                coin = new BlueCoin(game.getWidth(), game.getHeight(), snek, obstacle);
            } else if (ran == 4 && obstacle.getObstacles().size() == 0) {
                coin = new SilverCoin(game.getWidth(), game.getHeight(), snek, obstacle);
            } else {
                coin = new Coin(game.getWidth(), game.getHeight(), snek, obstacle);
            }
        } else {
            coin = new Coin(game.getWidth(), game.getHeight(), snek, obstacle);
        }
    }
}
