package com.comp2042.view;

import com.comp2042.model.*;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.List;

public class GameView {

    private static final Color ARENA_COLOR = Color.web("#feffe4");
    private static final Color OBSTACLE_COLOR = Color.BLACK;

    private final Image headImage = new Image("resources/views/res/snakehead.png");
    private final Image bodyImage = new Image("resources/views/res/snakebody.png");
    private final Image turnImage = new Image("resources/views/res/snaketurnleft.png");
    private final Image tailImage = new Image("resources/views/res/snaketail.png");

    private final Image redCoinImage = new Image("resources/views/res/redcoin.png");
    private final Image goldCoinImage = new Image("resources/views/res/goldcoin.png");
    private final Image rainbowCoinImage = new Image("resources/views/res/rainbowcoin.png");
    private final Image blueCoinImage = new Image("resources/views/res/bluecoin.png");
    private final Image silverCoinImage = new Image("resources/views/res/silvercoin.png");

    public Canvas gridCanvas;
    public GraphicsContext gc;

    public Label gameOverLabel = new Label("GAME OVER");
    public Label gameWonLabel = new Label("YOU WON!");
    public Label scoreLabel = new Label();

    private final PauseMenuView pauseMenuView;
    public final StackPane pauseOverlay;

    private Coin currentCoin;
    private Snake currentSnake;
    private Obstacle currentObstacle;

    private final Game game;
    private double initX;
    private double initY;
    private double fullScale = 1;

    public GameView(Game game) {
        this.game = game;
        this.pauseMenuView = new PauseMenuView(this::redrawGrid);
        this.pauseOverlay = pauseMenuView.getOverlay();
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
        } else {
            drawGrid(coin, snake, obstacle, gc);
        }
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

        if (gc != null && snake != null && coin != null) {
            drawGrid(coin, snake, obstacle, gc);
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
        return pauseMenuView.isVisible();
    }

    public void showPauseMenu(boolean show) {
        pauseMenuView.show(show);
    }

    public boolean isPauseHelpVisible() {
        return pauseMenuView.isHelpVisible();
    }

    public Button getPauseHelpButton() {
        return pauseMenuView.getPauseHelpButton();
    }

    public Button getHelpBackButton() {
        return pauseMenuView.getHelpBackButton();
    }

    public void updateGridButtonText() {
        pauseMenuView.updateGridButtonText();
    }

    public void redrawGrid() {
        if (gc != null && currentCoin != null && currentSnake != null) {
            drawGrid(currentCoin, currentSnake, currentObstacle, gc);
        }
    }

    public Button getResumeButton() {
        return pauseMenuView.getResumeButton();
    }

    public Button getReturnMenuButton() {
        return pauseMenuView.getReturnMenuButton();
    }

    private Image getCoinImage(Coin coin) {
        if (coin instanceof GoldCoin) {
            return goldCoinImage;
        } else if (coin instanceof RainbowCoin) {
            return rainbowCoinImage;
        } else if (coin instanceof BlueCoin) {
            return blueCoinImage;
        } else if (coin instanceof SilverCoin) {
            return silverCoinImage;
        }
        return redCoinImage;
    }

    public void drawGrid(Coin coin, Snake snake, Obstacle obstacle, GraphicsContext gc) {
        if (gc == null || coin == null || snake == null) return;
        this.currentCoin = coin;
        this.currentSnake = snake;
        this.currentObstacle = obstacle;

        gc.clearRect(0, 0, gridCanvas.getWidth(), gridCanvas.getHeight());
        gc.setFill(ARENA_COLOR);
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
        gc.drawImage(getCoinImage(coin),
                (coin.getPos().x() * tileSize) + coinPadding,
                (coin.getPos().y() * tileSize) + coinPadding,
                coinSize, coinSize);

        List<Cell> body = snake.getBody();
        for (int bodyIndex = 0; bodyIndex < body.size(); bodyIndex++) {
            drawBody(snake, body, bodyIndex);
        }

        drawHead(snake);

        if (obstacle != null) {
            gc.setFill(OBSTACLE_COLOR);
            for (Cell cell : obstacle.getObstacles()) {
                gc.fillRect(cell.x() * tileSize, cell.y() * tileSize, tileSize, tileSize);
            }
        }
    }

    /**
     * Rotates and draws a sprite image centered within its target grid cell directly on the canvas
     * without allocating offscreen ImageView snapshots.
     */
    private void drawRotatedImage(Image image, double angle, double x, double y, double size) {
        gc.save();
        gc.translate(x + size / 2.0, y + size / 2.0);
        gc.rotate(angle);
        gc.drawImage(image, -size / 2.0, -size / 2.0, size, size);
        gc.restore();
    }

    /**
     * Draws the snake's head on the canvas, rotated to face its current direction.
     * @param snake the snake whose head is drawn
     */
    private void drawHead(Snake snake) {
        double tileSize = game.getMultiplier() * fullScale;
        drawRotatedImage(headImage, snake.getSnakeDir(),
                snake.getHead().x() * tileSize, snake.getHead().y() * tileSize, tileSize);
    }

    /**
     * Draws the segment of the snake's body, choosing the right picture
     * (tail, corner or straight piece) and rotation for it.
     * @param snake the snake being drawn
     * @param body the unmodifiable list of body cells for the current frame
     * @param index the segment's position in the body list, where 0 is the tail
     */
    private void drawBody(Snake snake, List<Cell> body, int index) {
        Cell point = body.get(index);
        // Skip drawing duplicate segments that are still stacked on the same spawn/tail cell
        if (index > 0 && point.equals(body.get(index - 1))) {
            return;
        }

        Image segmentImage;
        int angle;
        if (index == 0) {
            segmentImage = tailImage;
            angle = snake.getTailDir();
        } else {
            Direction incoming = snake.getSegmentDirection(index - 1);
            Direction outgoing = snake.getSegmentDirection(index);
            if (incoming != outgoing) {
                segmentImage = turnImage;
                angle = Direction.getCornerRotation(incoming, outgoing);
            } else {
                segmentImage = bodyImage;
                angle = outgoing.getBodyAngle();
            }
        }

        double tileSize = game.getMultiplier() * fullScale;
        drawRotatedImage(segmentImage, angle, point.x() * tileSize, point.y() * tileSize, tileSize);
    }

    /**
     * Updates the visibility of the victory and game-over overlays from the authoritative {@link GameState}.
     *
     * @param state the current lifecycle phase of the game
     */
    public void updateStateOverlays(GameState state) {
        gameOverLabel.setVisible(state == GameState.DEAD);
        gameWonLabel.setVisible(state == GameState.WON);
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
