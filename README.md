# COMP2042 Snake Coursework

## GitHub Repository
https://github.com/Seann19744/Snake_CW2026

---

## Compilation and Execution Instructions

### Prerequisites
- **Java Development Kit (JDK)**: Version 21 or higher (Project configured for Java 21 LTS in `pom.xml`).
- **Build Tool**: Apache Maven 3.8+ (or use the included Maven wrapper `mvnw` / `mvnw.cmd`).
- **JavaFX SDK**: Version 25.0.1 (automatically managed via Maven dependencies in `pom.xml`).
- **IDE** (Optional): IntelliJ IDEA (recommended), Eclipse, or VS Code with Java extensions.

### How to Run the Application

#### Option 1: Running via IntelliJ IDEA (Recommended)
1. Open IntelliJ IDEA, select **File -> Open**, and choose the project root directory (`Snake_CW2026`).
2. Allow IntelliJ to import and resolve dependencies from `pom.xml`.
3. Verify that the Project SDK is set to JDK 21+ (**File -> Project Structure -> Project -> SDK**).
4. Navigate to `src/main/java/com/comp2042/Launcher.java`.
5. Right-click `Launcher.java` and select **Run 'Launcher.main()'**.
   > *Note*: Running via `Launcher.java` instead of `Main.java` bypasses JavaFX 11+ runtime component checks, preventing `Runtime components are missing` errors when running directly in an IDE without custom VM options.

#### Option 2: Running via Maven Command Line
Open a terminal in the root directory of the project and execute:
```bash
# Compile project
mvn clean compile

# Run JavaFX application
mvn javafx:run
```
*(On Windows PowerShell using the wrapper: `.\mvnw.cmd compile; .\mvnw.cmd javafx:run`)*

## Implemented and Working Properly

The following features, enhancements, and bug fixes have been implemented, tested, and verified to be working properly:

1. **Game Modes and Playable Levels System (UI, Logic, & Map Layouts)**:
   - Replaced basic difficulty levels and the legacy main menu dropdown with a dedicated **Select Difficulty Screen** (`difficultyOverlay` in `MainMenu.fxml` & `MainMenuController.java`) triggered upon clicking **Start**, displaying each mode button on the left alongside a natural-English rule summary on the right:
     - **Classic Mode**: Standard speed (8.5), fatal outer borders, standard growth (+1 segment per coin), coins to clear levels: 8 -> 10 -> 12 (30 coins total to win).
     - **Quick Mode**: Standard speed (8.5), border wrap-around enabled (`noBorder = true`), 2 fewer coins per level: 6 -> 8 -> 10 (24 coins total to win).
     - **Hardcore Mode**: High speed (13.0), fatal borders, +2 coins per level: 10 -> 12 -> 14 (36 coins total to win), and **double snake growth (+2 segments per coin eaten)**.
   - **Automatic In-Game Level Progression**:
     - Replaced the legacy 398-coin endless requirement with structured level advancement based on the exact count of coins eaten, decoupling victory from random score luck.
     - **Level 1 (The Open Field)**: Clean 20x20 open grid.
     - **Level 2 (Four Pillars)**: 4 static 2x2 stone pillars spawned in quadrants to restrict turning space.
     - **Level 3 (The Labyrinth)**: Interior corridor barriers dividing the field into interconnected quadrants. Completing Level 3 triggers the victory screen (**"YOU WON!"**).
   - **Live In-Game HUD**: Displays live mode, level, coin quota, and score: e.g., `[Classic Mode] Level 2/3 | Coins: 4/10 | Score: 45`.

2. **Audio Effects Implementation**:
   - Replaced an unused string path with a functional JavaFX `AudioClip` in `GameController.java` to load `/resources/views/res/coinSound.wav`.
   - Plays an audio sound effect every time the snake consumes a coin.

3. **Bug Fix: Rainbow Coin Spawning Logic**:
   - Resolved a bug in `GameController.newCoin()` where `ran == 1` was duplicated in the if-else branch (`if (ran == 1) ... else if (ran == 1) ...`). Because the first branch created `GoldCoin`, the second branch was unreachable dead code, making `RainbowCoin` impossible to spawn.
   - Updated condition to `else if (ran == 2)`, successfully enabling rainbow coin spawns and speed bonuses.

4. **Bug Fix: Reverse Direction Suicide Prevention (`isValidTurn`)**:
   - Resolved a flaw in the directional input validation method (`isValidTurn`, formerly `valKey`). Previously, pressing `DOWN` while traveling `UP` was allowed due to a self-comparison bug (`!this.key.equals(KeyCode.DOWN)`), resulting in immediate 180-degree self-collision.
   - Updated logic so `DOWN` is rejected when moving `UP`, `UP` is rejected when moving `DOWN`, `LEFT` is rejected when moving `RIGHT`, and `RIGHT` is rejected when moving `LEFT`.

5. **Bug Fix: Leftmost Obstacle Column Generation**:
   - Resolved an indexing issue in `Obstacle.newObstacles()` where `rnd.nextInt(x - 1) + 1` was used to generate x-coordinates. This mathematical offset restricted obstacles to `[1, x-1]`, preventing obstacles from ever spawning on the leftmost grid column (`x = 0`).
   - Fixed to `rnd.nextInt(x)`, allowing obstacles to spawn across the full grid width while still ensuring they never overlap the snake or coin.

6. **Bug Fix: Fullscreen Toggle Keybindings**:
   - Enhanced fullscreen toggle controls in `GameController.java` to support keyboards without dedicated numpads.
   - In addition to `KeyCode.PLUS`, key codes `KeyCode.EQUALS` and `KeyCode.ADD` now trigger fullscreen.
   - In addition to `KeyCode.MINUS`, `KeyCode.SUBTRACT` now exits fullscreen / minimizes the window.

7. **Bug Fix: Fullscreen Visual Scaling for Snake**:
   - Fixed a rendering scaling bug in `GameView.java` where the snake's head and body segments were scaled by `fullScale` twice: `(game.getMultiplier() * fullScale) * fullScale`.
   - This caused the snake to render disproportionately large and overflow grid tiles in fullscreen mode. Removing the extra `* fullScale` restored correct 1:1 proportional scaling with the game grid.

8. **Bug Fix: ESC Key Game Loop Termination and State Cleanup**:
   - Fixed an issue where pressing `ESCAPE` returned the user to the main menu but allowed the JavaFX `Timeline` animation loop to keep running in the background. Added `animation.stop()` when `ESCAPE` is pressed.
   - Added explicit resets for `key` and `lastKey` to `KeyCode.LEFT` in the constructor and when exiting to the main menu, preventing previous game inputs from carrying over to a new session.

9. **In-Game Controls & Gameplay Mechanics**:
   - **Movement**: Arrow keys (`UP`, `DOWN`, `LEFT`, `RIGHT`) start the game and steer the snake.
   - **Pause Game**: Press `P` to pause the animation loop.
   - **Reset Game**: Press `TAB` to reset the snake, coin, obstacles, score, and canvas without quitting.
   - **Power-Up Coins**:
     - Regular Coin (Red): +5 points, extends snake body.
     - Gold Coin: +15 points.
     - Rainbow Coin: +5 points, temporary speed increase.
     - Blue Coin: +5 points, temporarily enables passing through borders without dying.
     - Silver Coin: +5 points, spawns random obstacle blocks.
   - **Return to Menu**: Press `ESCAPE` to halt the game and return to the main menu.

10. **Architectural MVC Refactoring**:
    - Reorganized all classes from the flat default package `com.comp2042` into distinct architectural layers: `model`, `view`, and `controller`, improving code cohesion and modularity.

11. **Code Cleanup & Dead Code Removal**:
    - Removed unused variables and dead methods in `GameController.java`, including `legacyScoreMultiplier`, `calculateBonusScore()`, and `unusedObstacleCount`.
    - Removed redundant imports (`javafx.scene.media.Media`, `MediaPlayer`, `java.io.File`).
    - Removed duplicate assignment in `Game.java` (`this.powerUp = powerUp;`).

12. **Code Readability & Naming Conventions**:
    - Renamed informal identifier `snek` to standard `snake` across all classes and methods.
    - Renamed cryptic method `valKey()` to descriptive boolean predicate `isValidTurn()`.
    - Corrected typographical error `initalizeMainMenu()` to `initializeMainMenu()` in `ViewManager.java`.

13. **Design Pattern: GoF Factory Pattern (`CoinGenerator`)**:
    - Extracted coin creation out of `GameController` into a dedicated `CoinGenerator` class (`com.comp2042.model.CoinGenerator`).
    - Encapsulates probability distributions and board condition checks (e.g., ensuring `SilverCoin` only spawns when the board has no obstacles).
    - Enables dependency injection of `Random` for deterministic unit testing.

14. **SOLID Refactoring: Polymorphic Power-Up Effects (Open-Closed Principle)**:
    - Replaced the hardcoded class-type checking ladder (`if (coin.getClass().equals(RainbowCoin.class)) ...`) in `GameController.powerCoin()` with a polymorphic `applyEffect(Game, Snake, Obstacle, Timeline)` method across the `Coin` hierarchy.
    - Each coin subclass encapsulates its own effect (`RainbowCoin` adds speed bonus, `BlueCoin` enables border-crossing, `SilverCoin` spawns obstacles, `GoldCoin` adds 15 points).
    - New coin types can now be introduced without modifying existing controller logic, fully conforming to the Open-Closed Principle (OCP) and Single Responsibility Principle (SRP).

15. **Persistent Best Score Tracking & Pixel Art Main Menu Display**:
    - Added persistent high score tracking in `GameSettings` (`highscore.txt` in the project root directory), automatically saving new high score records whenever points are earned (`Game.addPoints(int)`) and reloading them on application startup.
    - Displays a top-centered retro `.pixel-badge` banner (`BEST SCORE: <score>`) with a pixel gold coin icon on the Main Menu that automatically refreshes when returning from a game session.

---

## Implemented but not Working Properly

- **None**: All features, mechanics, and bug fixes listed above have been tested and are working properly as expected.

---

## Features not Implemented

The following features were considered during design and development but were not implemented in this release:

1. **Multi-Player / Online Leaderboard**:
   - The game saves and displays the local player's persistent best score (`highscore.txt`), but an online multi-user leaderboard with player name entry was left for future enhancements.
2. **Custom Grid Dimensions Configuration**:
   - Although the `Game` and `Obstacle` models support variable grid dimensions (`X_VALUE`, `Y_VALUE`), the UI currently launches with the standard 20x20 grid layout and does not expose a slider or text input in the main menu for custom board sizing.
3. **Sound Effects for Collisions and Game Over**:
   - Audio feedback is currently implemented for coin collection (`coinSound.wav`). Additional sound effects for wall collisions, self-collisions, and victory/defeat screens were not included in this iteration.
4. **Dynamic / Moving Obstacles**:
   - Obstacles generated by silver coins remain static in their spawned locations throughout the round. Dynamic moving obstacles were left for future enhancements.

---

## New Java Classes

### 1. `com.comp2042.Launcher`
- **Location**: `src/main/java/com/comp2042/Launcher.java`
- **Purpose**: Application launcher entry point.
- **Justification**: JavaFX 11+ enforces strict runtime checks when the entry point class directly inherits from `javafx.application.Application`. If launched from an IDE or a shaded fat JAR without explicit VM options pointing to the JavaFX module path, the JVM throws `Error: JavaFX runtime components are missing, and are required to run this application`. `Launcher` contains a standard `main(String[] args)` method that does not extend `Application` and delegates directly to `Main.main(args)`. This enables IntelliJ IDEA, Eclipse, and packaged fat JARs to execute seamlessly across all environments.

### 2. `com.comp2042.model.Difficulty`
- **Location**: `src/main/java/com/comp2042/model/Difficulty.java`
- **Purpose**: Type-safe enumeration defining game difficulty levels (`EASY`, `NORMAL`, `HARD`).
- **Justification**: The original codebase utilized loose `char` values (`'E'`, `'N'`, `'H'`) passed into constructors without input validation, creating risk of undefined states or a frozen snake if an unexpected character was passed. `Difficulty` encapsulates gameplay parameters:
  - `speed` (double): Animation rate (8.5 for `EASY` and `NORMAL`, 13.0 for `HARD`).
  - `noBorder` (boolean): `true` for `EASY` (enabling wall wrap-around), `false` for `NORMAL` and `HARD`.
  - Provides a clean data model that integrates directly with JavaFX UI controls such as `ComboBox<Difficulty>`.

### 3. `com.comp2042.model.CoinGenerator`
- **Location**: `src/main/java/com/comp2042/model/CoinGenerator.java`
- **Purpose**: Implements the GoF Factory Pattern for generating `Coin` instances and subclasses.
- **Justification**: Decouples coin generation from controller classes. Encapsulates probability-based power-up selection and board condition validation. Supports constructor dependency injection with `java.util.Random`, enabling deterministic unit testing.

### 4. `com.comp2042.model.GameMode`
- **Location**: `src/main/java/com/comp2042/model/GameMode.java`
- **Purpose**: Type-safe enumeration defining game modes (`QUICK`, `CLASSIC`, `HARDCORE`).
- **Justification**: Encapsulates speed, wall wrap-around behavior, coin quotas required to clear each level, snake body growth rate per coin eaten, and human-readable mode descriptions (`getDescription()`) displayed on the difficulty selection screen.

### 5. `com.comp2042.model.GameSettings`
- **Location**: `src/main/java/com/comp2042/model/GameSettings.java`
- **Purpose**: Singleton configuration manager (GoF Singleton Pattern) managing user preferences such as grid display options and persistent high score storage (`highscore.txt`).
- **Justification**: Centralizes application-wide user configuration and persistent record tracking without polluting controller state. Accessible from both the Main Menu and active gameplay sessions. Automatically loads the saved high score on startup (`loadHighScore()`) and writes new records to `highscore.txt` when beaten (`updateHighScore(int)`).

### 6. `com.comp2042.model.GameState`
- **Location**: `src/main/java/com/comp2042/model/GameState.java`
- **Purpose**: Type-safe enumeration defining the lifecycle phases of a game session (`READY`, `RUNNING`, `PAUSED`, `LEVEL_CLEARED`, `WON`, `DEAD`).
- **Justification**: Replaces fragmented boolean flags (`gameStarted`, overlay visibility checks) with an explicit finite state machine in `GameController`, ensuring deterministic transitions when starting a match, clearing levels, pausing/resuming, winning, or dying.

---

## Modified Java Classes

### 1. `com.comp2042.Main`
- Updated package imports to reflect the new package architecture (`com.comp2042.view.ViewManager`).
- Fixed method invocation typo: updated `viewManager.initalizeMainMenu()` to `viewManager.initializeMainMenu()`.

### 2. `com.comp2042.controller.MainMenuController`
- Moved to package `com.comp2042.controller`.
- Replaced the main menu `ComboBox` dropdown with a dedicated **Select Difficulty** overlay (`difficultyOverlay`) opened via `handleStart()`.
- Added handlers (`handleQuickMode()`, `handleClassicMode()`, `handleHardcoreMode()`, `handleCloseDifficulty()`) and dynamic description labels (`lbl_quickDesc`, `lbl_classicDesc`, `lbl_hardcoreDesc`) populated from `GameMode.getDescription()`.
- Added top-right `?` help button handlers (`handleOpenHelp()`, `handleCloseHelp()`) controlling the **How to Play** overlay (`helpOverlay`).
- Added `handleQuit()` handler for the main menu **Quit** button to cleanly close the stage and exit the JavaFX application via `Platform.exit()`.
- Added `drawPixelArtBackground()` on `menuBgCanvas` to render a retro 25x25 checkerboard tile grid (`#feffe4` / `#f1efd2`), double-layered pixel arcade border with corner rivets, and decorative pixel-art snake and coin sprites.
- Added `lbl_highScore` binding and `updateHighScoreLabel()` (with a `showingProperty()` stage listener) to display the persistent best score at the top of the Main Menu.
- Refactored `startGame(GameMode)` and `launchSnake(Stage, GameMode)` to launch the game directly with the selected `GameMode`.
- Replaced hardcoded char constant `DIFFICULTY = 'N'` with dynamic selection.

### 3. `src/main/resources/resources/views/MainMenu.fxml` & `styling.css`
- Updated `fx:controller` attribute to match the new package path: `com.comp2042.controller.MainMenuController`.
- Replaced the `<ComboBox>` dropdown with a structured `<StackPane fx:id="difficultyOverlay">` screen containing left-aligned `startAndQuitBtn` mode buttons (`Quick`, `Classic`, `Hardcore`) paired with right-aligned wrapped English description labels in `HBox` rows, plus a `Back` navigation button.
- Added a top-right `?` button (`btn_help`) and `<StackPane fx:id="helpOverlay">` explaining all keyboard controls (`Arrow Keys`, `ESC/P`, `G`, `F11`, `TAB`) and coin power-up effects with embedded `16x16` pixel coin sprite icons.
- Added a harmonized **Quit** button (`btn_quit`) styled with `startAndQuitBtn` and aligned below **Start** and **Settings**.
- Redesigned the menu theme in `MainMenu.fxml` and `styling.css` with a background `<Canvas fx:id="menuBgCanvas">`, retro 8-bit bordered dialog cards (`.pixel-card`), cartridge-style slot containers (`.pixel-slot`), top-centered high score badge (`.pixel-badge` with `lbl_highScore`), shaded pixel headers (`.pixel-header`), harmonized `130x40` buttons, golden hover highlights (`#ffe66d`), and tactile 8-bit button press offsets.

### 4. `com.comp2042.controller.GameController`
- Moved to package `com.comp2042.controller` and updated model and view imports.
- **Factory Pattern Integration**: Injected `CoinGenerator` to handle coin spawning in `newCoin()` and `reset()`, removing hardcoded random coin instantiation from the controller.
- **Polymorphic Power-Up Handling (OCP)**: Refactored `powerCoin()` to delegate directly to `coin.applyEffect(game, snake, obstacle, animation)`, completely removing fragile `getClass().equals(...)` conditionals and conforming to the Open-Closed Principle.
- **Rainbow Coin Bug Fix**: Corrected condition in `newCoin()` from duplicate `ran == 1` to `ran == 2`, making `RainbowCoin` spawnable during gameplay.
- **Direction Reversal Bug Fix**: In `isValidTurn()` (formerly `valKey()`), corrected the check for `KeyCode.DOWN` from `!this.key.equals(KeyCode.DOWN)` to `!this.key.equals(KeyCode.UP)`, preventing instant 180° suicide turns across all four directions.
- **Audio Integration**: Replaced unused String variable `coinSoundPath` with a loaded `AudioClip` (`/resources/views/res/coinSound.wav`) played whenever `snake.ateCoin(coin)` is true.
- **Fullscreen Keybindings**: Added `KeyCode.EQUALS` and `KeyCode.ADD` to fullscreen triggers, and `KeyCode.SUBTRACT` to minimize triggers.
- **ESC Key Handling**: Added `animation.stop()` in `KeyCode.ESCAPE` to halt background loop execution when returning to the main menu.
- **Direction State Initialization**: Explicitly set `key = KeyCode.LEFT` and `lastKey = KeyCode.LEFT` in the constructor and `reset()` method so direction states do not bleed into subsequent games.
- **Dead Code Removal**: Removed unused fields `legacyScoreMultiplier`, `unusedObstacleCount`, unused method `calculateBonusScore()`, and unused imports (`javafx.scene.media.Media`, `MediaPlayer`, `java.io.File`).

### 5. `com.comp2042.model.Game`
- Moved to package `com.comp2042.model`.
- Removed duplicate assignment statement `this.powerUp = powerUp;` in the constructor.
- Refactored constructor signature from `Game(int x, int y, boolean powerUp, char difficulty)` to `Game(int x, int y, boolean powerUp, Difficulty difficulty)`, extracting speed and border properties safely from the enum.
- Updated `addPoints(int)` to notify `GameSettings.getInstance().updateHighScore(scorePoints)` so new high score records are immediately persisted to `highscore.txt`.

### 6. `com.comp2042.model.Obstacle`
- Moved to package `com.comp2042.model`.
- Fixed coordinate generation in `newObstacles()`: changed `rnd.nextInt(x - 1) + 1` to `rnd.nextInt(x)`, allowing obstacles to spawn across all columns including column 0.
- Added comprehensive Javadoc documentation explaining obstacle placement logic and collision prevention with the snake and coin.

### 7. `com.comp2042.view.GameView`
- Moved to package `com.comp2042.view`.
- Fixed fullscreen distortion: removed duplicate `* fullScale` in `drawHead()` and `drawBody()`, eliminating the bug where snake body parts were scaled quadratically.
- Renamed parameter `snek` to `snake` in methods (`turnDir`, `gameOver`, `drawGrid`, `drawHead`, `drawBody`).
- Added in-game pause overlay (`pauseOverlay`) with a Settings sub-menu (`pauseSettingsBox`) and a top-right `?` help button (`pauseHelpButton`) opening an in-game **How to Play** overlay (`pauseHelpBox`), styled with matching 8-bit `.pixel-card` and `.pixel-slot` arcade panels and harmonized `130x40` buttons.

### 8. `com.comp2042.view.ViewManager`
- Moved to package `com.comp2042.view`.
- Corrected method name spelling: `initalizeMainMenu()` -> `initializeMainMenu()`.
- Updated imports and stage initialization.

### 9. `com.comp2042.model.Snake`
- Moved to package `com.comp2042.model`.
- Standardized variable naming throughout the class (`snek` -> `snake`).

### 10. `com.comp2042.model.Coin` and Subclasses (`GoldCoin`, `SilverCoin`, `BlueCoin`, `RainbowCoin`)
- Moved to package `com.comp2042.model`.
- Updated constructors and method parameters from `Snake snek` to `Snake snake`.
- Implemented polymorphic `applyEffect(Game, Snake, Obstacle, Timeline)` across all coin types, encapsulating power-up behaviors within the coin models rather than external controller conditionals.

---

### Package Reorganisation (MVC Architecture)

The project structure was refactored from a single flat package (`com.comp2042`) into a Model-View-Controller (MVC) architectural design pattern:

```
src/main/java/com/comp2042/
├── Launcher.java                       [Application Entry Point]
├── Main.java                           [JavaFX Application Lifecycle]
├── controller/                         [Controller Layer]
│   ├── GameController.java             (Input handling, game loop, audio trigger)
│   └── MainMenuController.java         (Main menu UI interactions, difficulty binding)
├── model/                              [Model Layer]
│   ├── BlueCoin.java                   (Border pass power-up coin)
│   ├── Coin.java                       (Base coin entity)
│   ├── CoinGenerator.java              (GoF Factory Pattern for coin instantiation)
│   ├── Difficulty.java                 (Legacy difficulty enumeration)
│   ├── Game.java                       (Game state, score, speed, grid boundaries)
│   ├── GameMode.java                   (Game modes: Quick, Classic, Hardcore)
│   ├── GoldCoin.java                   (High-value bonus coin)
│   ├── Obstacle.java                   (Obstacle entity and placement algorithms)
│   ├── RainbowCoin.java                (Speed boost coin)
│   ├── SilverCoin.java                 (Obstacle-spawning coin)
│   └── Snake.java                      (Snake segments, movement, collision detection)
└── view/                               [View Layer]
    ├── GameView.java                   (Canvas rendering, head/tail rotations, UI labels)
    └── ViewManager.java                (JavaFX stage management and scene transitions)
```

**Architectural Rationale**:
- **Separation of Concerns**: Model classes have zero dependencies on JavaFX UI controls or input listeners, making them easier to maintain and unit test.
- **Maintainability and Extensibility**: Adding new coin types, UI screens, or input mechanisms now requires changes only in their dedicated package without risking regressions elsewhere.
- **Improved Code Navigation**: Eliminates the cognitive overhead of searching through a cluttered root package.

---

### Renamed Identifiers and Code Quality Improvements

Adhering to clean code principles and Java naming conventions:
- **`snek` -> `snake`**: Replaced all colloquialisms with descriptive, professional terminology across variable names, parameter lists, and method signatures in all classes.
- **`valKey()` -> `isValidTurn()`**: Renamed this method in `GameController.java` to clearly express its purpose as a boolean predicate checking whether a direction change is legally permitted.
- **`initalizeMainMenu()` -> `initializeMainMenu()`**: Corrected spelling error in `ViewManager.java` and updated all callers (`Main.java`).
- **Standardized Constants**: Maintained UPPER_SNAKE_CASE for class constants (`X_VALUE`, `Y_VALUE`, `POWER_UP`).

---

## Unexpected Problems Encountered and Solutions

1. **IntelliJ IDEA Run Configuration Classpath Issue (`ClassNotFoundException`)**:
   - **Problem**: Following the package restructuring into `com.comp2042.controller`, `com.comp2042.model`, and `com.comp2042.view`, IntelliJ IDEA automatically updated its cached run configuration to `com.comp2042.model.comp2042.Launcher`. Attempting to start the application resulted in `java.lang.ClassNotFoundException: com.comp2042.model.comp2042.Launcher`.
   - **Solution**: Manually edited the Run/Debug Configuration in IntelliJ IDEA, ensuring the Main class correctly pointed to `com.comp2042.Launcher` in the project root module.

2. **Quadratic Fullscreen Scaling Distortion**:
   - **Problem**: When switching to fullscreen mode using the fullscreen key, the snake head and body segments rendered at an enormous scale, overflowing beyond their grid tiles and distorting the game visual layout, while obstacles and coins rendered at standard fullscreen dimensions.
   - **Solution**: Code inspection in `GameView.drawHead()` and `GameView.drawBody()` revealed that each segment's draw dimensions were calculated as `(game.getMultiplier() * fullScale) * fullScale`. Because `gridCanvas` was already scaled by `fullScale`, multiplying by `fullScale` a second time produced quadratic enlargement ($fullScale^2$). Removing the extra `* fullScale` restored proper proportional scaling.

3. **Background Game Loop Persistence on Main Menu Navigation**:
   - **Problem**: Pressing `ESCAPE` during gameplay switched stages and displayed the main menu, but the game loop `Timeline animation` was never stopped. The game continued executing in the background, updating state, consuming CPU cycles, and creating memory leaks.
   - **Solution**: Added `animation.stop()` inside the `KeyCode.ESCAPE` event branch in `GameController.handle()`, ensuring the animation loop is completely halted when the player exits the game session.

4. **Input Direction Carryover Across Sessions**:
   - **Problem**: Static variables `key` and `lastKey` in `GameController` retained their values from the previous game session upon restarting. If a player was moving DOWN when they exited or died, starting a new game could cause the snake to instantly register DOWN instead of the default LEFT, leading to unexpected turns or collisions.
   - **Solution**: Explicitly reset `key = KeyCode.LEFT` and `lastKey = KeyCode.LEFT` in the `GameController` constructor as well as inside the `reset()` method.

5. **Premature Snake Movement on In-Game Pause Toggle (`ESCAPE` / `P`)**:
   - **Problem**: When opening and closing the in-game pause menu using `ESCAPE` (or `P`) or clicking "Resume" before the player had initiated movement with an arrow key, the resume logic unconditionally invoked `animation.play()`. This caused the stationary snake to prematurely start moving across the board from its initial spawn position without player intent.
   - **Solution**: Introduced an explicit `gameStarted` state boolean in `GameController`. The animation timeline is now only resumed if `gameStarted` is `true` (triggered when the player actively presses an arrow key). Reset and level advancement operations reset `gameStarted = false`, ensuring the snake remains stationary until the player initiates directional movement. Additionally consumed the `KeyEvent` (`event.consume()`) on menu toggling to prevent redundant key event propagation.

6. **Playable Arena Visual Boundary Indistinguishability**:
   - **Problem**: In maximized/windowed fullscreen mode, the outer container margins (`gamePane`) and the playable canvas (`gridCanvas`) both shared the same background color (`#feffe4`). Because there was no visual separation, players could not identify the 20x20 grid borders, making it impossible to anticipate wall collisions in border-enabled game modes.
   - **Solution**: Set the non-playable letterbox container (`gamePane`) background to pure white (`#ffffff`) and added a crisp perimeter border (`#b0ab88`) around the playable arena canvas in `GameView.drawGrid()`, clearly delineating the playable field from non-playable window margins.

7. **Opaque White Background Box on Spawned Coin Sprites**:
   - **Problem**: When rendering spawned coins (`redcoin.png`, `bluecoin.png`, `goldcoin.png`, `rainbowcoin.png`, `silvercoin.png`) on top of the newly implemented checkerboard grid pattern, an unwanted opaque white square box appeared around the circular coin sprites. Pixel analysis revealed the original image assets lacked an alpha transparency channel on their corner pixels (`A=255, R~248, G~250, B~250`), producing visible square white artifacts over darker checkered tiles.
   - **Solution**: Processed all five coin PNG assets to replace the solid near-white background pixels outside the circular coin diameter ($radius > 530px$) with 100% alpha transparency (`A=0`), including smooth anti-aliased perimeter edge blending. Additionally updated `GameView.drawGrid()` to center each coin sprite proportionally inside its grid tile.

8. **Stale Entity Ghosting on Window Resize and Fullscreen Spam (`F11`)**:
   - **Problem**: When rapidly toggling fullscreen via `F11` or resizing the window, ghost duplicate images of coins and snake segments appeared at their initial starting positions in the background. Investigation showed that the JavaFX `Scene` dimension change listeners registered in `MainMenuController` captured static closure references to the initial `coin` and `snake` local variables instantiated at match launch. Whenever `fitToWindow()` was triggered by resize events, it redrew these obsolete, stale entity references instead of the controller's active live entities.
   - **Solution**: Delegated window resize events to a dedicated `handleResize(width, height)` handler in `GameController`, which supplies the authoritative, currently active `coin`, `snake`, and `obstacle` references. Additionally added `gc.clearRect(...)` to `GameView.drawGrid()` prior to redrawing, guaranteeing a clean canvas without lingering render artifacts.

9. **Continued Snake Movement After Victory (`YOU WON!`)**:
   - **Problem**: When the player collected the final coin of Level 3, the win branch in `GameController` displayed the `"YOU WON!"` overlay and returned from the tick callback without calling `animation.stop()`, while the top of the `Timeline` tick only checked `snake.getDead()`. Consequently, the snake continued moving across the board on subsequent ticks after winning, allowing it to collide with walls/obstacles (triggering `"GAME OVER"` on top of `"YOU WON!"`) or re-consume the final coin.
   - **Solution**: Extracted the `Timeline` tick body into `onTick()`, invoked `animation.stop()` immediately inside the final level victory branch, and guarded `onTick()` with `if (snake.getDead() || game.gameWon()) { animation.stop(); return; }` so the game loop halts cleanly upon victory.

10. **Level Transition Failure to Pause, Snake Length Reset, and Broken Pause Resume**:
    - **Problem**: When advancing to a new level, `GameController.loadLevel()` reset `gameStarted = false` without stopping the `Timeline` animation, causing the snake to immediately move left from the center of the new level without player input. Because `gameStarted` was `false` while the timeline was running, pausing and clicking "Resume" failed to restart movement. Furthermore, `Snake.resetPosition()` cleared the body list to a single segment, wiping out the player's accumulated snake length between levels.
    - **Solution**: Introduced the `GameState` enum (`READY`, `RUNNING`, `PAUSED`, `LEVEL_CLEARED`, `WON`, `DEAD`) in `GameController` (with `prePauseState` restoration on pause/resume) and called `animation.stop()` in `loadLevel()` with `gameState = GameState.LEVEL_CLEARED`. Updated `Snake.resetPosition()` to stack all accumulated body segments onto the initial spawn tail tile `(x / 2 + 1, y / 2)` so the snake preserves its full length and smoothly uncoils from the center spawn block as it moves in the new level.

11. **`TAB` Reset Overwriting Full In-Game HUD Label**:
    - **Problem**: Pressing `TAB` in `GameController.handle()` invoked `reset()`, which properly updated `scoreLabel` to the complete HUD format (`[Classic Mode] Level 1/3 | Coins: 0/8 | Score: 0`) via `gameView.updateScoreLabel()`, but the next statement in `handle()` immediately overwrote `gameView.scoreLabel` with the legacy `"Score: 0"` string.
    - **Solution**: Removed the redundant `gameView.scoreLabel.setText("Score: " + game.getScorePoints())` statement from the `KeyCode.TAB` branch in `GameController.handle()`, preserving the full HUD format upon match reset.

12. **Stale Corner Sprites in `turnArray` Across Resets and Levels**:
    - **Problem**: `GameView.turnArray` was allocated in a field initializer using static `MainMenuController` constants (`MainMenuController.getxValue()`, `getyValue()`) before the `Game` model existed, and was only cleared cell-by-cell when the snake's tail (`body.get(0)`) passed over a corner tile. If a player reset the match (`TAB`) or cleared a level after turning—before the tail reached the corner coordinate—the non-zero turn code remained stuck in `turnArray[x][y]`, causing bent corner sprites to render on a straight snake body whenever the snake later crossed that tile.
    - **Solution**: Initialized `turnArray = new int[game.getWidth()][game.getHeight()]` inside the `GameView(Game)` constructor (decoupling `GameView` from `MainMenuController`) and added `resetTurnArray()`, invoking it in both `GameController.reset()` and `GameController.loadLevel()`.

13. **Border Wrap-Around Bypassing Obstacle Collision Detection**:
    - **Problem**: In `Snake.move(char dir)`, the obstacle collision check (`obstacle.getObstacles().contains(head)`) was evaluated inside the `if` branch prior to the `else if` branch that wrapped `head` across the grid boundary when `noBorder` or `ignoreBorders` was active. When the snake crossed a border, `head` temporarily held an out-of-bounds coordinate (e.g., `(-1, y)`), which never matched any in-bounds obstacle, and then wrapped onto the opposite edge (e.g., `(gx - 1, y)`) without re-checking for obstacles—allowing the snake to pass straight through border obstacles.
    - **Solution**: Restructured `Snake.move(char dir)` to resolve boundary wrapping first and then evaluate `if (!dead && obstacle.getObstacles().contains(head))` against the final destination coordinate, reverting `head` to `prevHead` and marking `dead = true` upon collision.

14. **Unfair Silver-Coin Obstacle Spawns and Duplicate Obstacle Coordinates**:
    - **Problem**: `Obstacle.newObstacles()` only rejected candidate coordinates matching `coin.getPos()`, `snake.getBody()`, or `snake.getHead()`. It did not check `obstacles.contains(p)` (allowing duplicate obstacle entries on the same tile) and did not maintain any safety distance in front of the snake's moving head, allowing obstacles to spawn 1 tile ahead of the head and cause unavoidable instant death.
    - **Solution**: Updated `Obstacle.newObstacles()` to reject existing obstacle tiles (`obstacles.contains(p)`) and added `isTooCloseToHead(Point, Point)` to enforce a 2-tile safety buffer around `snake.getHead()` (accounting for border wrap-around distance).

15. **Dropped Fast Directional Inputs on Quick U-Turns and `ImageView.snapshot()` Render Stutter**:
    - **Problem**: `GameController` used a single boolean lock (`keyPressed = true`) to block additional key presses within the same `Timeline` tick (~118ms at speed 8.5). When a player rapidly pressed two perpendicular keys (e.g., `UP` then `RIGHT` while traveling `LEFT`) to perform a tight 1-tile U-turn, the second input arrived before `onTick()` cleared `keyPressed` and was silently dropped. Additionally, `GameView.drawHead()` and `drawBody()` invoked `ImageView.snapshot()` for every snake segment on every tick, allocating dozens of offscreen textures per frame.
    - **Solution**: Added a 2-slot FIFO input queue (`Deque<KeyCode> inputQueue = new ArrayDeque<>(2)`) in `GameController` that validates rapid second turns against the preceding queued direction (`isValidTurnFrom()`) and dequeues one turn per tick. Replaced all `ImageView.snapshot()` calls in `GameView` with direct hardware-accelerated canvas rotation (`drawRotatedImage()` using `gc.save()`, `gc.translate()`, `gc.rotate()`, `gc.drawImage()`, `gc.restore()`).
