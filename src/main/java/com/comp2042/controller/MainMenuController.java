package com.comp2042.controller;

import com.comp2042.Main;
import com.comp2042.model.*;
import com.comp2042.view.GameView;
import com.comp2042.view.ViewManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.control.ComboBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.fxml.FXML;

public class MainMenuController {

    private static final int X_VALUE = 20;
    private static final int Y_VALUE = 20;
    private static final boolean POWER_UP = true;

    @FXML
    private ComboBox<GameMode> difficultyBox;

    @FXML
    private StackPane settingsOverlay;

    @FXML
    private Button btn_gridToggle;

    @FXML
    /**
     * Have dropdown bar display all game modes.
     */
    public void initialize() {
        difficultyBox.getItems().setAll(GameMode.values());
        difficultyBox.setValue(GameMode.CLASSIC);
        updateGridButton();
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

    public void handleStart() {
        GameMode gameMode = difficultyBox.getValue();
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

        // Dynamically scale on window resize or fullscreen toggle
        gameScene.widthProperty().addListener((obs, oldV, newV) ->
                gameView.fitToWindow(newV.doubleValue(), gameScene.getHeight(), obstacle, snake, coin)
        );
        gameScene.heightProperty().addListener((obs, oldV, newV) ->
                gameView.fitToWindow(gameScene.getWidth(), newV.doubleValue(), obstacle, snake, coin)
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
