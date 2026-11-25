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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.fxml.FXML;

public class MainMenuController {

    private static final int X_VALUE = 20;
    private static final int Y_VALUE = 20;
    private static final boolean POWER_UP = true;

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
    /**
     * Initialize main menu labels and settings button states.
     */
    public void initialize() {
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
                gameView.gridCanvas,
                gameView.gameOverLabel,
                gameView.gameWonLabel,
                gameView.scoreLabel,
                gameView.pauseOverlay
        );
        gamePane.setPadding(new Insets(20));
        gamePane.setAlignment(gameView.gameOverLabel, Pos.TOP_CENTER);
        gamePane.setAlignment(gameView.gameWonLabel, Pos.TOP_CENTER);
        gamePane.setAlignment(gameView.scoreLabel, Pos.BOTTOM_CENTER);
        gamePane.setAlignment(gameView.pauseOverlay, Pos.CENTER);

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

    public static int getxValue() {
        return X_VALUE;
    }

    public static int getyValue() {
        return Y_VALUE;
    }
}
