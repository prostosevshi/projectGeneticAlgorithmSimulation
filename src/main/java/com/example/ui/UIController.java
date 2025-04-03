package com.example.ui;

import com.example.model.Entity;
import com.example.movingEntity.Creature;
import com.example.staticEntity.Food;
import com.example.staticEntity.Poison;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

import java.util.List;

public class UIController {

    private UISimulationController uiSimulationController;
    private GridPane gridPane;
    private Button pauseOrResumeButton;
    private Canvas canvas;
    private GraphicsContext gc;
    private static final int CELL_SIZE = 20;

    public UIController(UISimulationController uiSimulationController) {
        this.uiSimulationController = uiSimulationController;
        this.gridPane = new GridPane();
        this.canvas = new Canvas(800, 600);
        this.gc = canvas.getGraphicsContext2D();
        startRenderLoop();
        initializeUI();
    }

    private void initializeUI() {
        /*Button startButton = new Button("Start");
        startButton.setOnAction(event -> uiSimulationController.startSimulation());*/

        pauseOrResumeButton = new Button("Start");
        pauseOrResumeButton.setOnAction(event -> togglePause());

        Button resetButton = new Button("Reset");
        resetButton.setOnAction(event -> uiSimulationController.resetSimulation());

        Button speedUpButton = new Button("Speed Up ");
        speedUpButton.setOnAction(event -> uiSimulationController.increaseSpeed());

        Button slowDownButton = new Button("Slow Down");
        slowDownButton.setOnAction(event -> uiSimulationController.decreaseSpeed());

        Button changeParametersButton = new Button("Change Parameters");
        changeParametersButton.setOnAction(event -> showParameterDialog());

        //gridPane.add(startButton, 0, 0);
        gridPane.add(pauseOrResumeButton, 1, 0);
        gridPane.add(resetButton, 2, 0);
        gridPane.add(speedUpButton, 3, 0);
        gridPane.add(slowDownButton, 4, 0);
        gridPane.add(changeParametersButton, 5, 0);
        gridPane.add(canvas, 0, 1, 6, 1); // Добавляем холст в UI

    }

    public void drawEntities(List<Entity> entities, int mapWidth, int mapHeight) {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        gc.setStroke(Color.LIGHTGRAY);
        for (int x = 0; x <= mapWidth; x++) {
            gc.strokeLine(x * CELL_SIZE, 0, x * CELL_SIZE, mapHeight * CELL_SIZE);
        }
        for (int y = 0; y <= mapHeight; y++) {
            gc.strokeLine(0, y * CELL_SIZE, mapWidth * CELL_SIZE, y * CELL_SIZE);
        }

        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        gc.strokeRect(0, 0, mapWidth * CELL_SIZE, mapHeight * CELL_SIZE);

        for (Entity entity : entities) {
            int x = entity.getX();
            int y = entity.getY();

            if (entity instanceof Creature) {
                gc.setFill(Color.BLUE);
            } else if (entity instanceof Food) {
                gc.setFill(Color.GREEN);
            } else if (entity instanceof Poison) {
                gc.setFill(Color.RED);
            }

            gc.fillRect(x * CELL_SIZE + 1, y * CELL_SIZE + 1, CELL_SIZE - 2, CELL_SIZE - 2);

            if (entity instanceof Creature) {
                Creature creature = (Creature) entity;
                String healthText = String.valueOf(creature.getHealth());

                // Выводим текст (жизни) по центру квадрата
                gc.setFill(Color.WHITE); // Цвет текста
                gc.setFont(javafx.scene.text.Font.font(12)); // Размер шрифта
                gc.setTextAlign(TextAlignment.CENTER);
                gc.fillText(healthText, x * CELL_SIZE + CELL_SIZE / 2,
                        y * CELL_SIZE + CELL_SIZE / 2 + 5);
            }
        }
    }

    private void startRenderLoop() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(100), event -> {
            if (uiSimulationController.getSimulation().getWorldMap() != null) {
                List<Entity> entities = uiSimulationController.getSimulation().getWorldMap().getEntities();
                drawEntities(entities,
                        uiSimulationController.getSimulation().getWorldMap().getWidth(),
                        uiSimulationController.getSimulation().getWorldMap().getHeight());
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private void togglePause() {
        if (uiSimulationController.getSimulation().isPaused()) {
            uiSimulationController.pauseOrResumeSimulation();
            pauseOrResumeButton.setText("Pause");
        } else {
            uiSimulationController.pauseOrResumeSimulation();
            pauseOrResumeButton.setText("Start");
        }
    }

    private void showParameterDialog() {
        uiSimulationController.getSimulation().resetSimulation();

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
            try {
                int width = Integer.parseInt(widthField.getText());
                int height = Integer.parseInt(heightField.getText());
                int food = Integer.parseInt(foodField.getText());
                int poison = Integer.parseInt(poisonField.getText());
                int creatures = Integer.parseInt(creaturesField.getText());

                uiSimulationController.changeMapSize(width, height);
                uiSimulationController.updateParameters(food, poison, creatures);
            } catch (NumberFormatException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Invalid Input");
                alert.setHeaderText("Invalid Parameter Values");
                alert.setContentText("Please make sure all fields are filled with valid numbers.");
                alert.showAndWait();
            }
            return null;
        });

        dialog.showAndWait();
    }

    public Parent getRoot() {
        return gridPane;
    }
}
