package com.example.ui.controller;

import com.example.model.Entity;
import com.example.model.creature.Creature;
import com.example.model.staticEntity.Food;
import com.example.model.staticEntity.Poison;
import com.example.model.staticEntity.Rock;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class UIController {

    private static final int CELL_SIZE = 20;
    private final UISimulationController uiSimulationController;
    private final Canvas canvas;
    private final GraphicsContext gc;

    private BorderPane rootPane;
    private Button pauseOrResumeButton;
    private Label speedLabel;
    private Label generationLabel;
    private Label numberOfCreaturesAliveLabel;
    private VBox lifetimeHistoryBox;

    public UIController(UISimulationController uiSimulationController) {
        this.uiSimulationController = uiSimulationController;
        uiSimulationController.getSimulation().setLifetimeUpdateListener(this::updateLifetimeHistory);

        this.canvas = new Canvas(900, 800);
        this.gc = canvas.getGraphicsContext2D();
        startRenderLoop();
        initializeUI();
    }

    private void initializeUI() {
        /*Button startButton = new Button("Start");
        startButton.setOnAction(event -> uiSimulationController.startSimulation());*/

        rootPane = new BorderPane();

        generationLabel = new Label("Generation: 0");
        numberOfCreaturesAliveLabel = new Label("Creatures Alive: 0");

        HBox topInfoBox = new HBox(20, generationLabel, numberOfCreaturesAliveLabel);
        topInfoBox.setAlignment(Pos.CENTER);
        topInfoBox.setStyle("-fx-padding: 10;");
        rootPane.setTop(topInfoBox);

        StackPane canvasWrapper = new StackPane(canvas);
        canvasWrapper.setStyle("-fx-padding: 10;");
        canvasWrapper.setAlignment(Pos.CENTER);

        rootPane.setCenter(canvasWrapper);

        //ToolBar toolbar = new ToolBar();
        //toolbar.setStyle("-fx-padding: 10; -fx-background-color: transparent;");

        pauseOrResumeButton = new Button("Start");
        pauseOrResumeButton.setOnAction(event -> togglePause());

        Button resetButton = new Button("Reset");
        resetButton.setOnAction(event -> uiSimulationController.resetSimulation());

        Button speedUpButton = new Button("Speed Up ");
        speedUpButton.setOnAction(event -> {
            uiSimulationController.increaseSpeed();
            updateSpeedLabel();
        });

        Button slowDownButton = new Button("Slow Down");
        slowDownButton.setOnAction(event -> {
            uiSimulationController.decreaseSpeed();
            updateSpeedLabel();
        });

        Button changeParametersButton = new Button("Change Parameters");
        changeParametersButton.setOnAction(event -> showParameterDialog());

        speedLabel = new Label();
        updateSpeedLabel();

        //VBOX
        lifetimeHistoryBox = new VBox(5);
        lifetimeHistoryBox.setStyle("-fx-padding: 10;");
        lifetimeHistoryBox.setAlignment(Pos.TOP_LEFT);
        Label historyLabel = new Label("Last 10 generations:");
        lifetimeHistoryBox.getChildren().add(historyLabel);

        StackPane rightPane = new StackPane(lifetimeHistoryBox);
        rightPane.setStyle("-fx-padding: 10;");
        rootPane.setRight(rightPane);
        //VBOX

        Region spacerLeft = new Region();
        Region spacerRight = new Region();
        HBox.setHgrow(spacerLeft, Priority.ALWAYS);
        HBox.setHgrow(spacerRight, Priority.ALWAYS);

        HBox buttonBox = new HBox(10, spacerLeft,
                pauseOrResumeButton,
                resetButton,
                speedUpButton,
                slowDownButton,
                changeParametersButton,
                new Region(), // spacer
                speedLabel);
        HBox.setHgrow(buttonBox.getChildren().get(buttonBox.getChildren().size() - 2), Priority.ALWAYS);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setStyle("-fx-padding: 10;");

        rootPane.setBottom(buttonBox);

        // ======= Настройка размеров =======
        //canvas.widthProperty().bind(rootPane.widthProperty().subtract(20));
        canvas.heightProperty().bind(rootPane.heightProperty().subtract(100));
    }

    public void updateLifetimeHistory(List<Integer> lifetimes) {
        lifetimeHistoryBox.getChildren().clear();

        Label historyLabel = new Label("Last 10 lifetimes:");
        lifetimeHistoryBox.getChildren().add(historyLabel);

        List<Integer> padded = new ArrayList<>();

        int missing = 10 - lifetimes.size();
        for (int i = 0; i < missing; i++) {
            padded.add(0);
        }
        padded.addAll(lifetimes);

        List<Integer> last10 = padded.subList(padded.size() - 10, padded.size());
        Collections.reverse(last10);

        for (int lifetime : last10) {
            Label label = new Label(String.valueOf(lifetime));
            lifetimeHistoryBox.getChildren().add(label);
        }
    }

    public void updateGenerationAndAlive() {
        generationLabel.setText("Generation: " + uiSimulationController.getSimulation().getGenCounter());
        numberOfCreaturesAliveLabel.setText("Creatures Alive: " + uiSimulationController.getSimulation().getNumberOfCreaturesAlive());
    }

    private void updateSpeedLabel() {
        double speed = uiSimulationController.getSimulation().getSpeed();
        speedLabel.setText(String.format("Speed: %.1fx", speed));
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

        List<Entity> entitiesCopy = new ArrayList<>(entities);
        for (Entity entity : entitiesCopy) {
            int x = entity.getX();
            int y = entity.getY();

            if (entity instanceof Creature) {
                gc.setFill(Color.BLUE);
            } else if (entity instanceof Food) {
                gc.setFill(Color.GREEN);
            } else if (entity instanceof Poison) {
                gc.setFill(Color.RED);
            } else if (entity instanceof Rock) {
                gc.setFill(Color.GRAY);
            } else {
                continue;
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
                updateGenerationAndAlive();
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

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Change Parameters");

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField widthField = new TextField(String.valueOf(uiSimulationController.getSimulation().getWorldMap().getWidth()));
        TextField heightField = new TextField(String.valueOf(uiSimulationController.getSimulation().getWorldMap().getHeight()));
        TextField foodField = new TextField(String.valueOf(uiSimulationController.getSimulation().getNumberOfFood()));
        TextField poisonField = new TextField(String.valueOf(uiSimulationController.getSimulation().getNumberOfPoison()));
        TextField creaturesField = new TextField(String.valueOf(uiSimulationController.getSimulation().getNumberOfCreatures()));

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

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                int width = Integer.parseInt(widthField.getText());
                int height = Integer.parseInt(heightField.getText());
                int food = Integer.parseInt(foodField.getText());
                int poison = Integer.parseInt(poisonField.getText());
                int creatures = Integer.parseInt(creaturesField.getText());

                uiSimulationController.getSimulation().resetSimulation();
                uiSimulationController.changeMapSize(width, height);
                uiSimulationController.updateParameters(food, poison, creatures);
            } catch (NumberFormatException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Invalid Input");
                alert.setHeaderText("Invalid Parameter Values");
                alert.setContentText("Please make sure all fields are filled with valid numbers.");
                alert.showAndWait();
            }
        }
    }

    public Parent getRoot() {
        return rootPane;
    }
}
