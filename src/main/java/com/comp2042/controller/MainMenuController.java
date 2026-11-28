package com.comp2042.controller;

import com.comp2042.Main;
import com.comp2042.model.*;
import com.comp2042.view.GameView;
import com.comp2042.view.ViewManager;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.fxml.FXML;

public class MainMenuController {

    private static final int X_VALUE = 20;
    private static final int Y_VALUE = 20;
    private static final boolean POWER_UP = true;

    @FXML
    private Canvas menuBgCanvas;

    @FXML
    private StackPane difficultyOverlay;

    @FXML
    private Label lbl_quickDesc;

    @FXML
    private Label lbl_classicDesc;

    @FXML
    private Label lbl_hardcoreDesc;

    @FXML
    private StackPane settingsOverlay;

    @FXML
    private StackPane helpOverlay;

    @FXML
    private Button btn_gridToggle;

    @FXML
    private Label lbl_highScore;

    /**
     * Initializes the main menu pixel-art background canvas, mode description labels,
     * high score display, and settings button states.
     */
    @FXML
    public void initialize() {
        drawPixelArtBackground();
        if (lbl_quickDesc != null) {
            lbl_quickDesc.setText(GameMode.QUICK.getDescription());
        }
        if (lbl_classicDesc != null) {
            lbl_classicDesc.setText(GameMode.CLASSIC.getDescription());
        }
        if (lbl_hardcoreDesc != null) {
            lbl_hardcoreDesc.setText(GameMode.HARDCORE.getDescription());
        }
        updateGridButton();
        updateHighScoreLabel();

        if (viewManager != null && viewManager.getPrimaryStage() != null) {
            viewManager.getPrimaryStage().showingProperty().addListener((obs, wasShowing, isNowShowing) -> {
                if (isNowShowing) {
                    updateHighScoreLabel();
                }
            });
        }
    }

    public void updateHighScoreLabel() {
        if (lbl_highScore != null) {
            lbl_highScore.setText("BEST SCORE: " + GameSettings.getInstance().getHighScore());
        }
    }

    /**
     * Draws a retro pixel-art checkerboard grid, arcade border frame, and decorative pixel sprites
     * across the main menu background canvas to match the in-game arena aesthetic.
     */
    private void drawPixelArtBackground() {
        if (menuBgCanvas == null) {
            return;
        }
        GraphicsContext gc = menuBgCanvas.getGraphicsContext2D();
        double width = menuBgCanvas.getWidth();
        double height = menuBgCanvas.getHeight();
        double tileSize = 25.0;

        // Base cream fill
        gc.setFill(Color.web("#feffe4"));
        gc.fillRect(0, 0, width, height);

        // Checkerboard alternating tiles matching the game grid
        gc.setFill(Color.web("#f1efd2"));
        int cols = (int) (width / tileSize);
        int rows = (int) (height / tileSize);
        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < rows; y++) {
                if ((x + y) % 2 == 1) {
                    gc.fillRect(x * tileSize, y * tileSize, tileSize, tileSize);
                }
            }
        }

        // Decorative pixel-art snake & coins along the perimeter tiles
        try {
            Image headImg = new Image("resources/views/res/snakehead.png");
            Image bodyImg = new Image("resources/views/res/snakebody.png");
            Image tailImg = new Image("resources/views/res/snaketail.png");
            Image redCoinImg = new Image("resources/views/res/redcoin.png");
            Image goldCoinImg = new Image("resources/views/res/goldcoin.png");
            Image rainbowCoinImg = new Image("resources/views/res/rainbowcoin.png");

            // Bottom-left pixel snake moving left towards a gold coin
            gc.drawImage(goldCoinImg, 2 * tileSize + 2, 14 * tileSize + 2, tileSize - 4, tileSize - 4);
            gc.drawImage(headImg, 5 * tileSize, 14 * tileSize, tileSize, tileSize);
            gc.drawImage(bodyImg, 6 * tileSize, 14 * tileSize, tileSize, tileSize);
            gc.drawImage(bodyImg, 7 * tileSize, 14 * tileSize, tileSize, tileSize);
            gc.drawImage(tailImg, 8 * tileSize, 14 * tileSize, tileSize, tileSize);

            // Top-left & bottom-right decorative pixel coins
            gc.drawImage(redCoinImg, 2 * tileSize + 2, 1 * tileSize + 2, tileSize - 4, tileSize - 4);
            gc.drawImage(rainbowCoinImg, 18 * tileSize + 2, 14 * tileSize + 2, tileSize - 4, tileSize - 4);
        } catch (Exception ignored) {
        }

        // Chunky retro pixel-art arcade border around the window
        gc.setStroke(Color.web("#4a4a4a"));
        gc.setLineWidth(6);
        gc.strokeRect(3, 3, width - 6, height - 6);

        gc.setStroke(Color.web("#b0ab88"));
        gc.setLineWidth(2);
        gc.strokeRect(8, 8, width - 16, height - 16);

        // Pixel corner rivets
        gc.setFill(Color.web("#4a4a4a"));
        gc.fillRect(6, 6, 8, 8);
        gc.fillRect(width - 14, 6, 8, 8);
        gc.fillRect(6, height - 14, 8, 8);
        gc.fillRect(width - 14, height - 14, 8, 8);
    }

    @FXML
    public void handleStart() {
        if (difficultyOverlay != null) {
            difficultyOverlay.setVisible(true);
        }
    }

    @FXML
    public void handleCloseDifficulty() {
        if (difficultyOverlay != null) {
            difficultyOverlay.setVisible(false);
        }
    }

    @FXML
    public void handleQuickMode() {
        startGame(GameMode.QUICK);
    }

    @FXML
    public void handleClassicMode() {
        startGame(GameMode.CLASSIC);
    }

    @FXML
    public void handleHardcoreMode() {
        startGame(GameMode.HARDCORE);
    }

    @FXML
    public void handleOpenSettings() {
        updateGridButton();
        if (settingsOverlay != null) {
            settingsOverlay.setVisible(true);
        }
    }

    @FXML
    public void handleCloseSettings() {
        if (settingsOverlay != null) {
            settingsOverlay.setVisible(false);
        }
    }

    @FXML
    public void handleOpenHelp() {
        if (helpOverlay != null) {
            helpOverlay.setVisible(true);
        }
    }

    @FXML
    public void handleCloseHelp() {
        if (helpOverlay != null) {
            helpOverlay.setVisible(false);
        }
    }

    @FXML
    public void handleQuit() {
        if (viewManager != null && viewManager.getPrimaryStage() != null) {
            viewManager.getPrimaryStage().close();
        }
        Platform.exit();
    }

    @FXML
    public void handleToggleGrid() {
        GameSettings.getInstance().toggleGrid();
        updateGridButton();
    }

    private void updateGridButton() {
        if (btn_gridToggle != null) {
            boolean enabled = GameSettings.getInstance().isGridEnabled();
            btn_gridToggle.setText(enabled ? "Grid: ON" : "Grid: OFF");
        }
    }

    private Scene gameScene;

    private ViewManager viewManager = Main.getViewManager();

    public void startGame(GameMode gameMode) {
        if (difficultyOverlay != null) {
            difficultyOverlay.setVisible(false);
        }
        viewManager.initGameStage();

        Stage gameStage = viewManager.getGameStage();
        launchSnake(gameStage, gameMode);
        gameStage.setScene(gameScene);
        gameStage.setMaximized(true);
        viewManager.getPrimaryStage().close();
        gameStage.show();
    }

    private void launchSnake(Stage gameStage, GameMode gameMode) {
        Game game = new Game(X_VALUE, Y_VALUE, POWER_UP, gameMode);
        Obstacle obstacle = new Obstacle(game.getWidth(), game.getHeight());
        Snake snake = new Snake(game.getWidth(), game.getHeight(), game.isNoBorder(), obstacle, gameMode.getGrowthRate());
        Coin coin = new Coin(game.getWidth(), game.getHeight(), snake, obstacle);

        GameView gameView = new GameView(game);
        GameController controller = new GameController(snake, coin, game, gameView, obstacle, gameStage);

        StackPane gamePane = new StackPane();
        gamePane.setStyle("-fx-background-color: #ffffff;");
        Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
        gameScene = new Scene(gamePane, visualBounds.getWidth(), visualBounds.getHeight());

        gameScene.setOnKeyPressed(controller::handle);

        gameView.makeScene(gameScene, snake, coin, obstacle);
        gamePane.getChildren().addAll(
                gameView.getGridCanvas(),
                gameView.getGameOverLabel(),
                gameView.getGameWonLabel(),
                gameView.getScoreLabel(),
                gameView.getPauseOverlay()
        );
        gamePane.setPadding(new Insets(20));
        gamePane.setAlignment(gameView.getGameOverLabel(), Pos.TOP_CENTER);
        gamePane.setAlignment(gameView.getGameWonLabel(), Pos.TOP_CENTER);
        gamePane.setAlignment(gameView.getScoreLabel(), Pos.BOTTOM_CENTER);
        gamePane.setAlignment(gameView.getPauseOverlay(), Pos.CENTER);

        // Dynamically scale on window resize or fullscreen toggle using live controller state
        gameScene.widthProperty().addListener((obs, oldV, newV) ->
                controller.handleResize(newV.doubleValue(), gameScene.getHeight())
        );
        gameScene.heightProperty().addListener((obs, oldV, newV) ->
                controller.handleResize(gameScene.getWidth(), newV.doubleValue())
        );
    }

    public ViewManager getViewManager() {
        return viewManager;
    }
}
