import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * This is the class that represents the OptionsMenu scene.
 */
public class OptionsMenu {

    // Parameters for the window and buttons
    private static int windowWidth = 800;
    private static int windowHeight = 600;
    private static int buttonWidth = windowWidth/4;
    private static int buttonHeight = windowHeight/10;

    public static void show(Stage window) {

        // Creates a button to get back
        Button backButton = new Button("Back");
        backButton.setMinSize(buttonWidth, buttonHeight);

        // When the button is clicked, set the scene of our window to MainMenu
        backButton.setOnAction(e -> MainMenu.show(window));

        // Create our layout in the form of a VBox
        VBox optionsMenu = new VBox(10);
        optionsMenu.setPadding(new Insets(10, 10, 10, 10));

        // Adds our button to the layout, positions it to the center
        optionsMenu.getChildren().addAll(backButton);
        optionsMenu.setAlignment(Pos.CENTER);

        // Creates a scene which contains our layout
        Scene sceneOptions = new Scene(optionsMenu, windowWidth, windowHeight);

        // Uses the MenuTheme.css style
        sceneOptions.getStylesheets().add("MenuTheme.css");

        // Sets the scene of our stage to sceneOptions
        window.setScene(sceneOptions);

    }


}