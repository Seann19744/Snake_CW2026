package com.comp2042.view;

import com.comp2042.controller.GameController;
import com.comp2042.model.*;
import javafx.geometry.Insets;
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

    public Label gameOverLabel = new Label("GAME OVER");
    public Label gameWonLabel = new Label("YOU WON!");
    public Label scoreLabel = new Label();
    private boolean gameOverVis = false;
    private boolean gameWonVis = false;

    public final StackPane pauseOverlay = new StackPane();
    private final Button resumeButton = new Button("Resume");
    private final Button returnMenuButton = new Button("Main Menu");
    private final Button pauseSettingsButton = new Button("Settings");
    private final Button pauseHelpButton = new Button("?");
    private final Button gridToggleButton = new Button("Grid: ON");
    private final Button settingsBackButton = new Button("Back");
    private final Button helpBackButton = new Button("Back");
    private final VBox pauseBox = new VBox(15);
    private final VBox pauseSettingsBox = new VBox(15);
    private final VBox pauseHelpBox = new VBox(14);

    private Coin currentCoin;
    private Snake currentSnake;
    private Obstacle currentObstacle;

    private Game game;
    private double initX;
    private double initY;
    private double fullScale = 1;
    private boolean fullsize = false;
    private int[][] turnArray;

    public GameView(Game game) {
        this.game = game;
        this.turnArray = new int[game.getWidth()][game.getHeight()];
        setupPauseOverlay();
    }

    public void resetTurnArray() {
        this.turnArray = new int[game.getWidth()][game.getHeight()];
    }

    public int getTurnAt(int x, int y) {
        return turnArray[x][y];
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

        if (scene != null) {
            fitToWindow(scene.getWidth(), scene.getHeight(), obstacle, snake, coin);
        } else if (obstacle != null) {
            drawGrid(coin, snake, obstacle, gc, false, 0);
        } else {
            drawGrid(coin, snake, gc);
        }
    }

    private void setupPauseOverlay() {
        String css = "resources/views/styling.css";
        if (!pauseOverlay.getStylesheets().contains(css)) {
            pauseOverlay.getStylesheets().add(css);
        }
        pauseOverlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.65);");
        pauseOverlay.setVisible(false);
        pauseOverlay.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        pauseBox.setAlignment(Pos.CENTER);
        pauseBox.setMaxSize(340, 310);
        pauseBox.setPadding(new Insets(22, 28, 22, 28));
        pauseBox.getStyleClass().add("pixel-card");

        Label pauseTitle = new Label("GAME PAUSED");
        pauseTitle.setId("pauseTitle");
        pauseTitle.getStyleClass().add("pixel-header");

        Label pauseHint = new Label("Press ESC or P to resume");
        pauseHint.setStyle("-fx-font-family: 'SF Pixelate'; -fx-font-size: 12px; -fx-text-fill: #6e6a50;");

        resumeButton.getStyleClass().add("startAndQuitBtn");
        resumeButton.setPrefSize(130, 40);

        pauseSettingsButton.getStyleClass().add("startAndQuitBtn");
        pauseSettingsButton.setPrefSize(130, 40);

        returnMenuButton.getStyleClass().add("startAndQuitBtn");
        returnMenuButton.setPrefSize(130, 40);

        pauseBox.getChildren().setAll(pauseTitle, pauseHint, resumeButton, pauseSettingsButton, returnMenuButton);

        // Top-right '?' help button in pause menu
        pauseHelpButton.getStyleClass().add("startAndQuitBtn");
        pauseHelpButton.setStyle("-fx-font-family: 'SF Pixelate'; -fx-font-size: 18px; -fx-background-size: 100% 100%;");
        pauseHelpButton.setPrefSize(40, 40);
        pauseHelpButton.setMinSize(40, 40);
        pauseHelpButton.setMaxSize(40, 40);
        StackPane.setAlignment(pauseHelpButton, Pos.TOP_RIGHT);
        StackPane.setMargin(pauseHelpButton, new Insets(16, 16, 0, 0));

        // Settings sub-menu within pause overlay
        pauseSettingsBox.setAlignment(Pos.CENTER);
        pauseSettingsBox.setMaxSize(330, 250);
        pauseSettingsBox.setPadding(new Insets(22, 25, 22, 25));
        pauseSettingsBox.getStyleClass().add("pixel-card");
        pauseSettingsBox.setVisible(false);

        Label settingsTitle = new Label("SETTINGS");
        settingsTitle.getStyleClass().add("pixel-header");

        gridToggleButton.getStyleClass().add("startAndQuitBtn");
        gridToggleButton.setPrefSize(130, 40);
        updateGridButtonText();

        settingsBackButton.getStyleClass().add("startAndQuitBtn");
        settingsBackButton.setPrefSize(130, 40);

        pauseSettingsBox.getChildren().setAll(settingsTitle, gridToggleButton, settingsBackButton);

        // Help / How to Play sub-menu within pause overlay
        pauseHelpBox.setAlignment(Pos.CENTER);
        pauseHelpBox.setMaxSize(520, 390);
        pauseHelpBox.setPadding(new Insets(16, 20, 16, 20));
        pauseHelpBox.getStyleClass().add("pixel-card");
        pauseHelpBox.setVisible(false);

        Label helpTitle = new Label("HOW TO PLAY");
        helpTitle.getStyleClass().add("pixel-header");

        VBox controlsSection = new VBox(5);
        controlsSection.setAlignment(Pos.CENTER_LEFT);
        controlsSection.setMaxWidth(480);
        controlsSection.getStyleClass().add("pixel-slot");
        Label controlsHeader = new Label("CONTROLS");
        controlsHeader.setStyle("-fx-font-family: 'SF Pixelate'; -fx-font-size: 14px; -fx-text-fill: #2d572c; -fx-underline: true;");
        controlsSection.getChildren().addAll(
                controlsHeader,
                createHelpLabel("• Arrow Keys: Start moving and steer the snake"),
                createHelpLabel("• ESC or P: Pause the game and open the pause menu"),
                createHelpLabel("• G: Toggle grid on/off  |  F11: Fullscreen  |  TAB: Restart")
        );

        VBox coinsSection = new VBox(5);
        coinsSection.setAlignment(Pos.CENTER_LEFT);
        coinsSection.setMaxWidth(480);
        coinsSection.getStyleClass().add("pixel-slot");
        Label coinsHeader = new Label("COINS & GOAL");
        coinsHeader.setStyle("-fx-font-family: 'SF Pixelate'; -fx-font-size: 14px; -fx-text-fill: #2d572c; -fx-underline: true;");
        coinsSection.getChildren().addAll(
                coinsHeader,
                createHelpLabel("• Red Coin: +5 points  |  Gold Coin: +15 bonus points"),
                createHelpLabel("• Rainbow Coin: +5 points & boosts movement speed"),
                createHelpLabel("• Blue Coin: +5 points & lets you pass through walls"),
                createHelpLabel("• Silver Coin: +5 points & spawns obstacle blocks"),
                createHelpLabel("• Clear all 3 levels by collecting target coins without crashing!")
        );

        helpBackButton.getStyleClass().add("startAndQuitBtn");
        helpBackButton.setPrefSize(130, 40);

        pauseHelpBox.getChildren().setAll(helpTitle, controlsSection, coinsSection, helpBackButton);

        pauseSettingsButton.setOnAction(e -> {
            updateGridButtonText();
            pauseBox.setVisible(false);
            pauseHelpBox.setVisible(false);
            pauseHelpButton.setVisible(false);
            pauseSettingsBox.setVisible(true);
        });

        pauseHelpButton.setOnAction(e -> {
            pauseBox.setVisible(false);
            pauseSettingsBox.setVisible(false);
            pauseHelpButton.setVisible(false);
            pauseHelpBox.setVisible(true);
        });

        gridToggleButton.setOnAction(e -> {
            GameSettings.getInstance().toggleGrid();
            updateGridButtonText();
            redrawGrid();
        });

        settingsBackButton.setOnAction(e -> {
            pauseSettingsBox.setVisible(false);
            pauseHelpBox.setVisible(false);
            pauseBox.setVisible(true);
            pauseHelpButton.setVisible(true);
        });

        helpBackButton.setOnAction(e -> {
            pauseHelpBox.setVisible(false);
            pauseSettingsBox.setVisible(false);
            pauseBox.setVisible(true);
            pauseHelpButton.setVisible(true);
        });

        pauseOverlay.getChildren().setAll(pauseBox, pauseSettingsBox, pauseHelpBox, pauseHelpButton);
    }

    private Label createHelpLabel(String text) {
        Label lbl = new Label(text);
        lbl.setWrapText(true);
        lbl.setStyle("-fx-font-family: 'SF Pixelate'; -fx-font-size: 12px; -fx-text-fill: #2b2b2b;");
        return lbl;
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
            pauseHelpBox.setVisible(false);
            pauseHelpButton.setVisible(true);
            updateGridButtonText();
            pauseOverlay.toFront();
        } else {
            pauseBox.setVisible(false);
            pauseSettingsBox.setVisible(false);
            pauseHelpBox.setVisible(false);
            pauseHelpButton.setVisible(false);
        }
    }

    public boolean isPauseHelpVisible() {
        return pauseHelpBox.isVisible();
    }

    public Button getPauseHelpButton() {
        return pauseHelpButton;
    }

    public Button getHelpBackButton() {
        return helpBackButton;
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
        // Skip drawing duplicate segments that are still stacked on the same spawn/tail cell
        if (index > 0 && point.equals(snake.getBody().get(index - 1))) {
            return;
        }

        Image rotatedImage;
        if (snake.getBody().size() <= 1) {
            snake.getImageViewTail().setRotate(snake.getSnakeDir());
            SnapshotParameters params = new SnapshotParameters();
            params.setFill(Color.TRANSPARENT);
            rotatedImage = snake.getImageViewTail().snapshot(params, null);
        } else if (index == 0) {
            Point nextPoint = snake.getHead();
            for (int k = 1; k < snake.getBody().size(); k++) {
                if (!snake.getBody().get(k).equals(point)) {
                    nextPoint = snake.getBody().get(k);
                    break;
                }
            }
            int rotateTail;
            if (nextPoint.getX() == point.getX() && nextPoint.getY() == point.getY() - 1) {
                rotateTail = 0;
            } else if (nextPoint.getX() == point.getX() && nextPoint.getY() == point.getY() + 1) {
                rotateTail = 180;
            } else if (nextPoint.getX() == point.getX() - 1 && nextPoint.getY() == point.getY()) {
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
