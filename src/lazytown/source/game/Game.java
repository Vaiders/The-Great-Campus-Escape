package lazytown.source.game;


import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import lazytown.source.AssetManager;
import lazytown.source.Main;
import lazytown.source.game.actor.*;
import lazytown.source.game.level.Level;
import lazytown.source.sound.SoundEngine;
import lazytown.source.menu.ConfirmBox;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * This is the class that represents the Game scene.
 * It is responsible for loading in everything and starting the game loop.
 */
public class Game {
    public static StackPane root;
    private static Group background;
    private static Scene sceneGame;
    private static GamePlayLoop gamePlayLoop;
    private static Image[] pSprites;
    public static MainCharacter playerOne;
    private static SoundEngine backgroundMusic = new SoundEngine("music");
    private static SoundEngine backgroundSfx = new SoundEngine("sfx");
    private static SoundEngine footsteps = new SoundEngine("sfx");
    public static Level[] levels;
    public static Level level;
    public static Director director = new Director();


    // Here we declare four booleans which will be the foundation of the player controls, we do not initialize them as
    // they default to false, whenever they are changed to true, logic will happen in another class. Later on, there
    // will be more variables as we give the player character more controls, like interaction, and using items.
    private boolean up, down, left, right, keyE;

    /**
     * The method that takes care of nearly everything within our game world, this is being run from the main menu
     * and is the core of our game, in this method we do just about everything from putting up loading
     * screens, and loading assets, to rendering everything and initializing the game loop.
     * Finally, it changes the scene of our window to the game scene, where all of this is contained.
     * @param primaryStage our primary stage (window).
     */
    public void show(Stage primaryStage) {

        // Variables
        root = new StackPane();
        background = new Group();
        root.getChildren().add(background);
        sceneGame = new Scene(root, Main.getWindowWidth(), Main.getWindowHeight());


        // Methods we need to call to make our game work
        showLoadingScreen(primaryStage);
        loadLevel();
        eventHandling();
        assetLoading();
        playMusic();
        spawnActors();
        renderActors();
        startGameLoop();
        renderUI();

        // Sets the scene of our stage to sceneGame
        primaryStage.setScene(sceneGame);
    }

    // This method renders a loading screen.
    private void showLoadingScreen(Stage primaryStage) {

        BorderPane loadingScreen = new BorderPane();
        loadingScreen.setPadding(new Insets(10));

        Label loadingLabel = new Label("Loading...");
        loadingScreen.setBottom(loadingLabel);

        Scene loadingScene = new Scene(loadingScreen, Main.getWindowWidth(), Main.getWindowHeight());
        loadingScene.getStylesheets().add(AssetManager.getTheme("LoadingScreenTheme.css"));
        primaryStage.setScene(loadingScene);
    }

    // This method is used to render the level from a map.
    private void loadLevel() {
       levels = new Level[5];
       for (int i = 0; i < 5; i++) {
           levels[i] = new Level(i);
       }

        // Instantiates a level object, renders the level
        level = levels[2];
        level.renderMap(background);
    }

    // This method does all of our event handling, when the user presses or releases a key, act accordingly.
    private void eventHandling() {
        // First we need to listen for key presses, and set the appropriate boolean values accordingly, afterwards, we
        // will do the same for when the key is no longer pressed. We use switch statements for this as that is by far
        // the most compact and elegant way of programming this logic.
        sceneGame.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case W:         up      = true; down    = false;  break;
                case A:         left    = true; right   = false; break;
                case S:         down    = true; up      = false; break;
                case D:         right   = true; left    = false; break;
                case E:         keyE    = true; UI.displayTextWindow(); break;
                case DIGIT1:    UI.consumePizza(); break;
                case DIGIT2:    UI.consumeBeer(); break;
                case DIGIT3:    playerOne.setSpawnLocation(100, 100); break;
                case ESCAPE:    exitGame(); break;
                case M:         UI.showMap(); break;
            }
        });

        sceneGame.setOnKeyReleased(event -> {
            switch (event.getCode()) {
                case W:     up      = false; break;
                case A:     left    = false; break;
                case S:     down    = false; break;
                case D:     right   = false; break;
                case E:     keyE    = false; break;
                case M:         UI.hideMap(); break;
            }
        });

    }

    // This method is used for loading in our art assets, audio and visual.
    private static void assetLoading() {

       pSprites = AssetManager.getPlayerSprites();

       // Loads and plays the background music.
       backgroundMusic.load("menuMusic2.mp3");
    }

    private static void playMusic() {
        backgroundMusic.play();
    }

    // This method takes care of spawning in our various actors, among those are the player, the guards, the pickups,
    // and whatever the player can interact with, later it could be extended to be more things, like props.
    private void spawnActors() {
       playerOne = new MainCharacter(this,"M 17,0 L 13,4 12,22 14,36 17,44 32,44 37,35 35,0 Z", 0, 0, pSprites);
//       Testing out a 75x75 SVG path, does not seem to make a difference.
//       playerOne = new MainCharacter(this,"M 25,0 L 15,20 25,70 50,70 60,20 50,0 Z", 0, 0, pSprites);
    }

    // This method takes care of rendering our actors to the stackPane object that we have set up
    private static void renderActors() {

        level.renderActors(background);
        root.getChildren().add(playerOne.spriteFrame);
        playerOne.setSpawnLocation(450, 200);
    }

    // This method starts our game loop, so what we have here is actually a dynamic game.
    private static void startGameLoop() {
        gamePlayLoop = new GamePlayLoop();
        gamePlayLoop.start();
    }

    // This method renders the in-game user interface.
    private void renderUI() {
        root.getChildren().add(UI.getUI());
        UI.loadTextWindow("Welcome to LazyTown! Press E to continue.",
                                    "Use W A S D to move around.\nE is also used to interact with items, such as doors, lockers and water taps.",
                                    "Scavenge around for some food and drinks. Currently you can hold 2 of each.\nPress 1 to eat, and 2 to drink.",
                                    "Once you find your backpack, your food and drink inventory will increase.",
                                    "Press ESC at any time to check the controls.\nGood luck!");
    }

    public static void changeLevel(int changeTo, int spawnX, int spawnY) {
        background.getChildren().clear();
        director.resetCurrentActors();
        level = levels[changeTo];
        level.renderMap(Game.getBackground());
        playerOne.updateLevelSize();
        level.renderActors(Game.getBackground());
        playerOne.setSpawnLocation(spawnX, spawnY);
    }

    // This method renders a new window to exit the game.
    private static void exitGame(){
        if(ConfirmBox.display("Exit Game","Are you sure you want to exit?"))
            System.exit(0);
    }

    public boolean isUp() {
        return up;
    }

    public boolean isDown() {
        return down;
    }

    public boolean isLeft() {
        return left;
    }

    public boolean isRight() {
        return right;
    }

    public boolean isKeyE() {
        return keyE;
    }

    public void setKeyE(boolean keyE) {
        this.keyE = keyE;
    }

    public static Group getBackground() {
        return background;
    }

    public static Level getLevel() {
        return level;
    }

    public static GamePlayLoop getGamePlayLoop() {
        return gamePlayLoop;
    }

    public static SoundEngine getBackgroundSfx() {
        return backgroundSfx;
    }
}