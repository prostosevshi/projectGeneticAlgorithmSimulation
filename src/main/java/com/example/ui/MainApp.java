package com.example.ui;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MainApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {

        UIController uiController = new UIController();

        BorderPane root = new BorderPane();

        root.setCenter(uiController.getCanvas());

        root.setBottom(uiController.createControlButtons());

        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.setTitle("Simulation");
        stage.show();
    }
}
