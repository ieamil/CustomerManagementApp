package org.example.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Main entry point for the Customer Management System application.
 * This class is responsible for launching the JavaFX application and loading the primary GUI scene.
 *
 * @author isil
 */
public class Main extends Application {

    public static void main(String[] args) {
        // Launches the JavaFX application
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            // Loads the FXML file for the main GUI interface
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/gui.fxml"));
            Scene scene = new Scene(loader.load());

            // Sets up the main stage (window) properties
            primaryStage.setScene(scene);
            primaryStage.setTitle("Customer Management System App");


            primaryStage.getIcons().add(new Image("/icons/AppIcon.png"));


            // Displays the main stage
            primaryStage.show();

        } catch (Exception e) {
            // Handles and logs any exceptions during the loading process
            System.out.println("Exception occurred while loading the GUI: " + e);
        }
    }
}
