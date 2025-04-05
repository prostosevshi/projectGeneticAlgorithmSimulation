package com.example.ui;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {

        UISimulationController uiSimulationController = new UISimulationController();
        UIController uiController = new UIController(uiSimulationController);

        Scene scene = new Scene(uiController.getRoot(), 1000, 600);
        stage.setScene(scene);
        stage.setTitle("Simulation");
        stage.show();

        uiSimulationController.initializeSimulation(40, 20, 40, 40, 30);
    }
}
