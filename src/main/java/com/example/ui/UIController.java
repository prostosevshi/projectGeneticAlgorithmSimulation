package com.example.ui;

import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

public class UIController {

    private UISimulationController uiSimulationController;
    private GridPane gridPane;
    private Button pauseOrResumeButton;

    public UIController(UISimulationController uiSimulationController) {
        this.uiSimulationController = uiSimulationController;
        this.gridPane = new GridPane();
        initializeUI();
    }

    private void initializeUI() {
        Button startButton = new Button("Start");
        startButton.setOnAction(event -> uiSimulationController.startSimulation());

        pauseOrResumeButton = new Button("Pause");
        pauseOrResumeButton.setOnAction(event -> togglePause());

        Button resetButton = new Button("Reset");
        resetButton.setOnAction(event -> uiSimulationController.resetSimulation());

        Button speedUpButton  = new Button("Speed Up ");
        speedUpButton.setOnAction(event -> uiSimulationController.increaseSpeed());

        Button slowDownButton  = new Button("Slow Down");
        slowDownButton .setOnAction(event -> uiSimulationController.decreaseSpeed());

        Button changeParametersButton = new Button("Change Parameters");
        changeParametersButton.setOnAction(event -> showParameterDialog());

        gridPane.add(startButton, 0, 0);
        gridPane.add(pauseOrResumeButton, 1, 0);
        gridPane.add(resetButton, 2, 0);
        gridPane.add(speedUpButton, 3, 0);
        gridPane.add(slowDownButton, 4, 0);
        gridPane.add(changeParametersButton, 5, 0);
    }

    private void togglePause() {
        if (uiSimulationController.getSimulation().isPaused()) {
            uiSimulationController.pauseOrResumeSimulation();
            pauseOrResumeButton.setText("Pause");
        } else {
            uiSimulationController.pauseOrResumeSimulation();
            pauseOrResumeButton.setText("Resume");
        }
    }

    private void showParameterDialog() {

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Change Parameters");

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField widthField = new TextField();
        widthField.setText(String.valueOf(uiSimulationController.getSimulation().getWorldMap().getWidth()));

        TextField heightField = new TextField();
        heightField.setText(String.valueOf(uiSimulationController.getSimulation().getWorldMap().getHeight()));

        TextField foodField = new TextField();
        foodField.setText(String.valueOf(uiSimulationController.getSimulation().getNumberOfFood()));

        TextField poisonField = new TextField();
        poisonField.setText(String.valueOf(uiSimulationController.getSimulation().getNumberOfPoison()));

        TextField creaturesField = new TextField();
        creaturesField.setText(String.valueOf(uiSimulationController.getSimulation().getNumberOfCreatures()));

        grid.add(new Label("Map Width:"), 0, 0);
        grid.add(widthField, 1, 0);

        grid.add(new Label("Map Height:"), 0, 1);
        grid.add(heightField, 1, 1);

        grid.add(new Label("Food:"), 0, 2);
        grid.add(foodField, 1, 2);

        grid.add(new Label("Poison:"), 0, 3);
        grid.add(poisonField, 1, 3);

        grid.add(new Label("Creatures:"), 0, 4);
        grid.add(creaturesField, 1, 4);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {

                int width = Integer.parseInt(widthField.getText());
                int height = Integer.parseInt(heightField.getText());
                int food = Integer.parseInt(foodField.getText());
                int poison = Integer.parseInt(poisonField.getText());
                int creatures = Integer.parseInt(creaturesField.getText());

                uiSimulationController.changeMapSize(width, height);
                uiSimulationController.updateParameters(food, poison, creatures);
            }
            return null;
        });

        dialog.showAndWait();
    }

    public Parent getRoot() {
        return gridPane;
    }
}
