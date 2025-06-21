package com.example.ui.app;
import com.example.controller.enums.MapStats;
import com.example.ui.controller.UIController;
import com.example.ui.controller.UISimulationController;
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

        Scene scene = new Scene(uiController.getRoot(), 1100, 600);
        stage.setScene(scene);
        stage.setTitle("Simulation");
        stage.show();

        uiSimulationController.initializeSimulation(MapStats.WIDTH, MapStats.HEIGHT, MapStats.NUMBEROFFOOD, MapStats.NUMBEROFPOISON, MapStats.NUMBEROFCREATURES);
    }
}
