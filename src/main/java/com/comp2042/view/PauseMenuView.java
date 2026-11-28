package com.comp2042.view;

import com.comp2042.model.GameSettings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Dedicated view component responsible for constructing and managing the in-game
 * pause overlay, settings sub-menu, and How to Play help screen (Single Responsibility Principle).
 */
public class PauseMenuView {

    private final StackPane overlay = new StackPane();
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

    /**
     * Constructs the in-game pause menu overlay and wires internal sub-menu navigation.
     *
     * @param onGridToggled callback invoked whenever the player toggles the checkerboard grid
     */
    public PauseMenuView(Runnable onGridToggled) {
        setupOverlay(onGridToggled);
    }

    private void setupOverlay(Runnable onGridToggled) {
        String css = "resources/views/styling.css";
        if (!overlay.getStylesheets().contains(css)) {
            overlay.getStylesheets().add(css);
        }
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.65);");
        overlay.setVisible(false);
        overlay.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        pauseBox.setAlignment(Pos.CENTER);
        pauseBox.setMaxSize(340, 310);
        pauseBox.setPadding(new Insets(22, 28, 22, 28));
        pauseBox.getStyleClass().add("pixel-card");

        Label pauseTitle = new Label("GAME PAUSED");
        pauseTitle.setId("pauseTitle");
        pauseTitle.getStyleClass().add("pixel-header");

        Label pauseHint = new Label("Press ESC to resume");
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

        VBox helpSections = new VBox(8);
        helpSections.setAlignment(Pos.CENTER);
        helpSections.setMaxWidth(480);
        populateHelpContent(helpSections);

        helpBackButton.getStyleClass().add("startAndQuitBtn");
        helpBackButton.setPrefSize(130, 40);

        pauseHelpBox.getChildren().setAll(helpTitle, helpSections, helpBackButton);

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
            if (onGridToggled != null) {
                onGridToggled.run();
            }
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

        overlay.getChildren().setAll(pauseBox, pauseSettingsBox, pauseHelpBox, pauseHelpButton);
    }

    /**
     * Populates a target container with the shared "CONTROLS" and "COINS &amp; GOAL" help sections
     * so that both the Main Menu help overlay and the in-game Pause Menu help overlay share
     * a single authoritative source of truth (DRY principle).
     *
     * @param container the VBox container to populate with the help sections
     */
    public static void populateHelpContent(VBox container) {
        VBox controlsSection = new VBox(4);
        controlsSection.setAlignment(Pos.CENTER_LEFT);
        controlsSection.setMaxWidth(480);
        controlsSection.getStyleClass().add("pixel-slot");
        Label controlsHeader = new Label("CONTROLS");
        controlsHeader.setStyle("-fx-font-family: 'SF Pixelate'; -fx-font-size: 13px; -fx-text-fill: #2d572c; -fx-underline: true;");
        controlsSection.getChildren().addAll(
                controlsHeader,
                createHelpLabel("• Arrow Keys: Start moving and steer the snake"),
                createHelpLabel("• ESC: Pause the game and open the pause menu"),
                createHelpLabel("• G: Toggle grid on/off  |  F11: Fullscreen  |  TAB: Restart")
        );

        VBox coinsSection = new VBox(4);
        coinsSection.setAlignment(Pos.CENTER_LEFT);
        coinsSection.setMaxWidth(480);
        coinsSection.getStyleClass().add("pixel-slot");
        Label coinsHeader = new Label("COINS & GOAL");
        coinsHeader.setStyle("-fx-font-family: 'SF Pixelate'; -fx-font-size: 13px; -fx-text-fill: #2d572c; -fx-underline: true;");

        HBox redAndGoldRow = new HBox(8,
                createCoinIcon("resources/views/res/redcoin.png"),
                createHelpLabel("Red: +5 pts"),
                createCoinIcon("resources/views/res/goldcoin.png"),
                createHelpLabel("Gold: +15 bonus pts")
        );
        redAndGoldRow.setAlignment(Pos.CENTER_LEFT);

        Label goalLabel = createHelpLabel("Clear all 3 levels by collecting the target coins without crashing!");
        goalLabel.setStyle("-fx-font-family: 'SF Pixelate'; -fx-font-size: 10px; -fx-text-fill: #4a4a4a;");

        coinsSection.getChildren().addAll(
                coinsHeader,
                redAndGoldRow,
                createCoinHelpRow("resources/views/res/rainbowcoin.png", "Rainbow: +5 pts & speed boost"),
                createCoinHelpRow("resources/views/res/bluecoin.png", "Blue: +5 pts & pass through walls"),
                createCoinHelpRow("resources/views/res/silvercoin.png", "Silver: +5 pts & spawns obstacle blocks"),
                goalLabel
        );

        container.getChildren().setAll(controlsSection, coinsSection);
    }

    private static HBox createCoinHelpRow(String imagePath, String description) {
        HBox row = new HBox(8, createCoinIcon(imagePath), createHelpLabel(description));
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private static ImageView createCoinIcon(String imagePath) {
        ImageView icon = new ImageView(new Image(imagePath));
        icon.setFitWidth(16);
        icon.setFitHeight(16);
        icon.setPreserveRatio(true);
        icon.setPickOnBounds(true);
        return icon;
    }

    private static Label createHelpLabel(String text) {
        Label lbl = new Label(text);
        lbl.setWrapText(true);
        lbl.setStyle("-fx-font-family: 'SF Pixelate'; -fx-font-size: 11px; -fx-text-fill: #2b2b2b;");
        return lbl;
    }

    public StackPane getOverlay() {
        return overlay;
    }

    public boolean isVisible() {
        return overlay.isVisible();
    }

    public void show(boolean show) {
        overlay.setVisible(show);
        if (show) {
            pauseBox.setVisible(true);
            pauseSettingsBox.setVisible(false);
            pauseHelpBox.setVisible(false);
            pauseHelpButton.setVisible(true);
            updateGridButtonText();
            overlay.toFront();
        } else {
            pauseBox.setVisible(false);
            pauseSettingsBox.setVisible(false);
            pauseHelpBox.setVisible(false);
            pauseHelpButton.setVisible(false);
        }
    }

    public boolean isHelpVisible() {
        return pauseHelpBox.isVisible();
    }

    public void updateGridButtonText() {
        boolean enabled = GameSettings.getInstance().isGridEnabled();
        gridToggleButton.setText(enabled ? "Grid: ON" : "Grid: OFF");
    }

    public Button getResumeButton() {
        return resumeButton;
    }

    public Button getReturnMenuButton() {
        return returnMenuButton;
    }

    public Button getPauseHelpButton() {
        return pauseHelpButton;
    }

    public Button getHelpBackButton() {
        return helpBackButton;
    }
}
