package com.comp2042.view;

import com.comp2042.controller.GameController;
import com.comp2042.controller.MainMenuController;
import com.comp2042.model.*;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.awt.*;

public class GameView {

    public Canvas gridCanvas;
    public GraphicsContext gc;

    public Label gameOverLabel;
    public Label gameWonLabel;
    public Label scoreLabel;
    private boolean gameOverVis = false;
    private boolean gameWonVis = false;

    public final StackPane pauseOverlay = new StackPane();
    private final Button resumeButton = new Button("Resume");
    private final Button returnMenuButton = new Button("Main Menu");
    private final Button pauseSettingsButton = new Button("Settings");
    private final Button gridToggleButton = new Button("Grid: ON");
    private final Button settingsBackButton = new Button("Back");
    private final VBox pauseBox = new VBox(15);
    private final VBox pauseSettingsBox = new VBox(15);

    private Coin currentCoin;
    private Snake currentSnake;
    private Obstacle currentObstacle;

    private Game game;
    private double initX;
    private double initY;
    private double fullScale = 1;
    private boolean fullsize = false;
    private int[][] turnArray = new int[MainMenuController.getxValue()][MainMenuController.getyValue()];

    public GameView(Game game) {
        this.game = game;
        pauseOverlay.setVisible(false);
    }

    public void makeScene(Scene scene, Snake snake, Coin coin) {
        makeScene(scene, snake, coin, null);
    }

    public void makeScene(Scene scene, Snake snake, Coin coin, Obstacle obstacle) {
        initX = game.getWidth() * game.getMultiplier();
        initY = game.getHeight() * game.getMultiplier();
        gridCanvas = new Canvas(initX, initY);
        gc = gridCanvas.getGraphicsContext2D();

        gameOverLabel = new Label("GAME OVER");
        gameOverLabel.setId("gameOverLabel");
        String css = "resources/views/styling.css";
        gameOverLabel.getStylesheets().add(css);
        gameOverLabel.setVisible(false);

        gameWonLabel = new Label("YOU WON!");
        gameWonLabel.setId("gameWonLabel");
        gameWonLabel.getStylesheets().add(css);
        gameWonLabel.setVisible(false);

        scoreLabel = new Label();
        scoreLabel.setId("scoreLabel");
        scoreLabel.getStylesheets().add(css);
        scoreLabel.setFont(new Font("SF Pixelate", 16));
        updateScoreLabel();
        scoreLabel.setVisible(true);

        setupPauseOverlay();

        if (scene != null) {
            fitToWindow(scene.getWidth(), scene.getHeight(), obstacle, snake, coin);
        } else if (obstacle != null) {
            drawGrid(coin, snake, obstacle, gc, false, 0);
        } else {
            drawGrid(coin, snake, gc);
        }
    }

    private void setupPauseOverlay() {
        pauseOverlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.72);");
        pauseOverlay.setVisible(false);
        pauseOverlay.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        pauseBox.setAlignment(Pos.CENTER);
        pauseBox.setMaxSize(300, 280);

        Label pauseTitle = new Label("GAME PAUSED");
        pauseTitle.setId("pauseTitle");
        pauseTitle.setStyle("-fx-font-family: 'SF Pixelate'; -fx-font-size: 32px; -fx-text-fill: #ffffff;");

        Label pauseHint = new Label("Press ESC or P to resume");
        pauseHint.setStyle("-fx-font-family: 'SF Pixelate'; -fx-font-size: 14px; -fx-text-fill: #cccccc;");

        resumeButton.getStyleClass().add("startAndQuitBtn");
        resumeButton.setStyle("-fx-font-family: 'SF Pixelate'; -fx-font-size: 16px; -fx-background-size: 100% 100%;");
        resumeButton.setPrefSize(140, 40);

        pauseSettingsButton.getStyleClass().add("startAndQuitBtn");
        pauseSettingsButton.setStyle("-fx-font-family: 'SF Pixelate'; -fx-font-size: 16px; -fx-background-size: 100% 100%;");
        pauseSettingsButton.setPrefSize(140, 40);

        returnMenuButton.getStyleClass().add("startAndQuitBtn");
        returnMenuButton.setStyle("-fx-font-family: 'SF Pixelate'; -fx-font-size: 16px; -fx-background-size: 100% 100%;");
        returnMenuButton.setPrefSize(140, 40);

        pauseBox.getChildren().setAll(pauseTitle, pauseHint, resumeButton, pauseSettingsButton, returnMenuButton);

        // Settings sub-menu within pause overlay
        pauseSettingsBox.setAlignment(Pos.CENTER);
        pauseSettingsBox.setMaxSize(300, 280);
        pauseSettingsBox.setVisible(false);

        Label settingsTitle = new Label("SETTINGS");
        settingsTitle.setStyle("-fx-font-family: 'SF Pixelate'; -fx-font-size: 32px; -fx-text-fill: #ffffff;");

        gridToggleButton.getStyleClass().add("startAndQuitBtn");
        gridToggleButton.setStyle("-fx-font-family: 'SF Pixelate'; -fx-font-size: 16px; -fx-background-size: 100% 100%;");
        gridToggleButton.setPrefSize(140, 40);
        updateGridButtonText();

        settingsBackButton.getStyleClass().add("startAndQuitBtn");
        settingsBackButton.setStyle("-fx-font-family: 'SF Pixelate'; -fx-font-size: 16px; -fx-background-size: 100% 100%;");
        settingsBackButton.setPrefSize(140, 40);

        pauseSettingsBox.getChildren().setAll(settingsTitle, gridToggleButton, settingsBackButton);

        pauseSettingsButton.setOnAction(e -> {
            updateGridButtonText();
            pauseBox.setVisible(false);
            pauseSettingsBox.setVisible(true);
        });

        gridToggleButton.setOnAction(e -> {
            GameSettings.getInstance().toggleGrid();
            updateGridButtonText();
            redrawGrid();
        });

        settingsBackButton.setOnAction(e -> {
            pauseSettingsBox.setVisible(false);
            pauseBox.setVisible(true);
        });

        pauseOverlay.getChildren().setAll(pauseBox, pauseSettingsBox);
    }

    public void fitToWindow(double windowWidth, double windowHeight, Obstacle obstacle, Snake snake, Coin coin) {
        if (windowWidth <= 0 || windowHeight <= 0) return;

        double marginW = 100;
        double marginH = 120;
        double availableW = Math.max(initX, windowWidth - marginW);
        double availableH = Math.max(initY, windowHeight - marginH);

        fullScale = Math.max(1.0, Math.min(availableW / initX, availableH / initY));

        if (gridCanvas != null) {
            gridCanvas.setWidth(initX * fullScale);
            gridCanvas.setHeight(initY * fullScale);
        }

        if (scoreLabel != null) {
            scoreLabel.setFont(new Font("SF Pixelate", Math.max(16, 11 * fullScale)));
        }
        if (gameOverLabel != null) {
            gameOverLabel.setFont(new Font("SF Pixelate", Math.max(26, 18 * fullScale)));
        }
        if (gameWonLabel != null) {
            gameWonLabel.setFont(new Font("SF Pixelate", Math.max(26, 18 * fullScale)));
        }

        if (gc != null && snake != null && coin != null && obstacle != null) {
            drawGrid(coin, snake, obstacle, gc, false, 0);
        } else if (gc != null && snake != null && coin != null) {
            drawGrid(coin, snake, gc);
        }
    }

    public void scaleToFullscreen(boolean fullScreen, Stage primaryStage, Obstacle obstacle, Snake snake, Coin coin) {
        if (primaryStage != null) {
            primaryStage.setFullScreen(fullScreen);
        }
        double width = 800;
        double height = 600;
        try {
            Screen screen = Screen.getPrimary();
            if (screen != null) {
                Rectangle2D bounds = fullScreen ? screen.getBounds() : screen.getVisualBounds();
                width = bounds.getWidth();
                height = bounds.getHeight();
            }
        } catch (Exception ignored) {}
        fitToWindow(width, height, obstacle, snake, coin);
    }

    public void toggleFullScreen(Stage stage, Obstacle obstacle, Snake snake, Coin coin) {
        if (stage != null) {
            boolean nextState = !stage.isFullScreen();
            stage.setFullScreen(nextState);
            scaleToFullscreen(nextState, stage, obstacle, snake, coin);
        }
    }

    public boolean isPauseMenuVisible() {
        return pauseOverlay.isVisible();
    }

    public void showPauseMenu(boolean show) {
        pauseOverlay.setVisible(show);
        if (show) {
            pauseBox.setVisible(true);
            pauseSettingsBox.setVisible(false);
            updateGridButtonText();
            pauseOverlay.toFront();
        } else {
            pauseBox.setVisible(false);
            pauseSettingsBox.setVisible(false);
        }
    }

    public void updateGridButtonText() {
        boolean enabled = GameSettings.getInstance().isGridEnabled();
        gridToggleButton.setText(enabled ? "Grid: ON" : "Grid: OFF");
    }

    public void redrawGrid() {
        if (gc != null && currentCoin != null && currentSnake != null && currentObstacle != null) {
            drawGrid(currentCoin, currentSnake, currentObstacle, gc, false, 0);
        } else if (gc != null && currentCoin != null && currentSnake != null) {
            drawGrid(currentCoin, currentSnake, gc);
        }
    }

    public Button getResumeButton() {
        return resumeButton;
    }

    public Button getReturnMenuButton() {
        return returnMenuButton;
    }

    public void drawGrid(Coin coin, Snake snake, Obstacle obstacle, GraphicsContext gc, boolean a, int b) {
        if (gc == null || coin == null || snake == null || obstacle == null) return;
        this.currentCoin = coin;
        this.currentSnake = snake;
        this.currentObstacle = obstacle;

        gc.clearRect(0, 0, gridCanvas.getWidth(), gridCanvas.getHeight());
        gc.setFill(game.getColor());
        gc.fillRect(0, 0, gridCanvas.getWidth(), gridCanvas.getHeight());

        // Checkerboard pattern (alternating tiles when grid is enabled)
        if (GameSettings.getInstance().isGridEnabled()) {
            double tileSize = game.getMultiplier() * fullScale;
            gc.setFill(Color.web("#f1efd2"));
            for (int x = 0; x < game.getWidth(); x++) {
                for (int y = 0; y < game.getHeight(); y++) {
                    if ((x + y) % 2 == 1) {
                        gc.fillRect(x * tileSize, y * tileSize, tileSize, tileSize);
                    }
                }
            }
        }

        // Boundary outline separating the playable grid from the white non-playable window area
        gc.setStroke(Color.web("#b0ab88"));
        gc.setLineWidth(2 * fullScale);
        gc.strokeRect(0, 0, gridCanvas.getWidth(), gridCanvas.getHeight());

        double tileSize = game.getMultiplier() * fullScale;
        double coinPadding = 2 * fullScale;
        double coinSize = tileSize - (2 * coinPadding);
        gc.drawImage(coin.getImage(),
                (coin.getPos().x * tileSize) + coinPadding,
                (coin.getPos().y * tileSize) + coinPadding,
                coinSize, coinSize);

        int bodyIndex = 0;
        for (Point point : snake.getBody()) {
            drawBody(snake, point, bodyIndex);
            bodyIndex++;
        }

        drawHead(snake);

        gc.setFill(obstacle.getColor());
        for (Point point : obstacle.getObstacles()) {
            gc.fillRect(point.x * game.getMultiplier() * fullScale, point.y * game.getMultiplier() * fullScale, game.getMultiplier() * fullScale, game.getMultiplier() * fullScale);
        }
    }

    private void drawGrid(Coin coin, Snake snake, GraphicsContext gc) {
        if (gc == null || coin == null || snake == null) return;
        this.currentCoin = coin;
        this.currentSnake = snake;

        gc.clearRect(0, 0, gridCanvas.getWidth(), gridCanvas.getHeight());
        gc.setFill(game.getColor());
        gc.fillRect(0, 0, gridCanvas.getWidth(), gridCanvas.getHeight());

        // Checkerboard pattern (alternating tiles when grid is enabled)
        if (GameSettings.getInstance().isGridEnabled()) {
            double tileSize = game.getMultiplier() * fullScale;
            gc.setFill(Color.web("#f1efd2"));
            for (int x = 0; x < game.getWidth(); x++) {
                for (int y = 0; y < game.getHeight(); y++) {
                    if ((x + y) % 2 == 1) {
                        gc.fillRect(x * tileSize, y * tileSize, tileSize, tileSize);
                    }
                }
            }
        }

        // Boundary outline separating the playable grid from the white non-playable window area
        gc.setStroke(Color.web("#b0ab88"));
        gc.setLineWidth(2 * fullScale);
        gc.strokeRect(0, 0, gridCanvas.getWidth(), gridCanvas.getHeight());

        double tileSize = game.getMultiplier() * fullScale;
        double coinPadding = 2 * fullScale;
        double coinSize = tileSize - (2 * coinPadding);
        gc.drawImage(coin.getImage(),
                (coin.getPos().x * tileSize) + coinPadding,
                (coin.getPos().y * tileSize) + coinPadding,
                coinSize, coinSize);

        int bodyIndex = 0;
        for (Point point : snake.getBody()) {
            drawBody(snake, point, bodyIndex);
            bodyIndex++;
        }

        drawHead(snake);
    }

    /**
     * Draws the snake's head on the canvas, rotated to face its current direction.
     * @param snake the snake whose head is drawn
     */
    private void drawHead(Snake snake) {
        snake.getImageViewHead().setRotate(snake.getSnakeDir());
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        Image rotatedImage = snake.getImageViewHead().snapshot(params, null);
        gc.drawImage(rotatedImage, (snake.getHead().x * game.getMultiplier()) * fullScale, (snake.getHead().y * game.getMultiplier()) * fullScale, game.getMultiplier() * fullScale, game.getMultiplier() * fullScale);
    }

    /**
     * Draws the segment of the snake's body, choosing the right picture
     * (tail, corner or straight piece) and rotation for it.
     * @param snake the snake being drawn
     * @param point the grid position of this segment
     * @param index the segment's position in the body list, where 0 is the tail
     */
    private void drawBody(Snake snake, Point point, int index) {
        Image rotatedImage;
        if (snake.getBody().size() <= 1) {
            snake.getImageViewTail().setRotate(snake.getSnakeDir());
            SnapshotParameters params = new SnapshotParameters();
            params.setFill(Color.TRANSPARENT);
            rotatedImage = snake.getImageViewTail().snapshot(params, null);
        } else if (index == 0) {
            int rotateTail;
            if (snake.getBody().get(1).getX() == snake.getBody().get(0).getX() && snake.getBody().get(1).getY() == snake.getBody().get(0).getY() - 1) {
                rotateTail = 0;
            } else if (snake.getBody().get(1).getX() == snake.getBody().get(0).getX() && snake.getBody().get(1).getY() == snake.getBody().get(0).getY() + 1) {
                rotateTail = 180;
            } else if (snake.getBody().get(1).getX() == snake.getBody().get(0).getX() - 1 && snake.getBody().get(1).getY() == snake.getBody().get(0).getY()) {
                rotateTail = 270;
            } else {
                rotateTail = 90;
            }
            snake.getImageViewTail().setRotate(rotateTail);
            SnapshotParameters params = new SnapshotParameters();
            params.setFill(Color.TRANSPARENT);
            rotatedImage = snake.getImageViewTail().snapshot(params, null);
        } else if (index > 0 && (turnArray[(int) snake.getBody().get(index).getX()][(int) snake.getBody().get(index).getY()] != 0)) {
            snake.getImageViewTurn().setRotate(90 * turnArray[(int) snake.getBody().get(index).getX()][(int) snake.getBody().get(index).getY()]);
            SnapshotParameters params = new SnapshotParameters();
            params.setFill(Color.TRANSPARENT);
            rotatedImage = snake.getImageViewTurn().snapshot(params, null);
        } else {
            snake.getImageViewBody().setRotate(snake.getBodyDir(index));
            SnapshotParameters params = new SnapshotParameters();
            params.setFill(Color.TRANSPARENT);
            rotatedImage = snake.getImageViewBody().snapshot(params, null);
        }

        if (turnArray[(int) snake.getBody().get(0).getX()][(int) snake.getBody().get(0).getY()] != 0) {
            turnArray[(int) snake.getBody().get(0).getX()][(int) snake.getBody().get(0).getY()] = 0;
        }

        gc.drawImage(rotatedImage, (point.x * game.getMultiplier()) * fullScale, (point.y * game.getMultiplier()) * fullScale, game.getMultiplier() * fullScale, game.getMultiplier() * fullScale);
    }

    public void turnDir(Snake snake) {
        if ((GameController.lastKey == KeyCode.LEFT && GameController.key == KeyCode.UP) || (GameController.lastKey == KeyCode.DOWN && GameController.key == KeyCode.RIGHT)) {
            turnArray[(int) snake.getHead().getX()][(int) snake.getHead().getY()] = 2;
        } else if ((GameController.lastKey == KeyCode.LEFT && GameController.key == KeyCode.DOWN) || (GameController.lastKey == KeyCode.UP && GameController.key == KeyCode.RIGHT)) {
            turnArray[(int) snake.getHead().getX()][(int) snake.getHead().getY()] = 3;
        } else if ((GameController.lastKey == KeyCode.RIGHT && GameController.key == KeyCode.UP) || (GameController.lastKey == KeyCode.DOWN && GameController.key == KeyCode.LEFT)) {
            turnArray[(int) snake.getHead().getX()][(int) snake.getHead().getY()] = 1;
        } else if ((GameController.lastKey == KeyCode.UP && GameController.key == KeyCode.LEFT) || (GameController.lastKey == KeyCode.RIGHT && GameController.key == KeyCode.DOWN)) {
            turnArray[(int) snake.getHead().getX()][(int) snake.getHead().getY()] = 4;
        } else turnArray[(int) snake.getHead().getX()][(int) snake.getHead().getY()] = 0;
    }

    public void gameOver(Snake snake) {
        if (snake.getDead() && !gameOverVis) {
            gameOverVis = true;
            gameOverLabel.setVisible(true);
        } else if (!snake.getDead() && gameOverVis) {
            gameOverLabel.setVisible(false);
            gameOverVis = false;
        }
    }

    public void gameWon() {
        if (game.gameWon() && !gameWonVis) {
            gameWonVis = true;
            gameWonLabel.setVisible(true);
        } else if (!game.gameWon() && gameWonVis) {
            gameWonVis = false;
            gameWonLabel.setVisible(false);
        }
    }

    public void updateScoreLabel() {
        scoreLabel.setText(String.format("[%s] Level %d/%d | Coins: %d/%d | Score: %d",
                game.getGameMode().getDisplayName(),
                game.getCurrentLevel(),
                Game.MAX_LEVEL,
                game.getLevelCoinsEaten(),
                game.getCoinsRequiredForCurrentLevel(),
                game.getScorePoints()));
    }

}
