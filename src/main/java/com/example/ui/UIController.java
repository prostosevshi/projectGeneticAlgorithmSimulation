package com.example.ui;

import com.example.movingEntity.Creature;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

import java.util.List;

public class UIController {

    private static final int WIDTH = 600;
    private static final int HEIGHT = 400;
    private static final int CELL_SIZE = 20;
    private Canvas canvas;

    public UIController() {
        this.canvas = new Canvas(WIDTH, HEIGHT);

        GraphicsContext gc = canvas.getGraphicsContext2D();
        drawGrid(gc);
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public void drawGrid(GraphicsContext gc) {
        gc.setStroke(Color.LIGHTGRAY);
        for (int i = 0; i < WIDTH; i += CELL_SIZE) {
            gc.strokeLine(i, 0, i, HEIGHT);
        }
        for (int i = 0; i < HEIGHT; i += CELL_SIZE) {
            gc.strokeLine(0, i, WIDTH, i);
        }
    }

    private void drawCreatures(GraphicsContext gc, List<Creature> creatures) {
        for (Creature creature : creatures) {
            gc.setFill(Color.GREEN);
            gc.fillRect(creature.getX() * CELL_SIZE, creature.getY() * CELL_SIZE, CELL_SIZE, CELL_SIZE);
        }
    }

    public HBox createControlButtons() {
        HBox buttons = new HBox(10);
        Button startButton = new Button("Start");
        Button pauseButton = new Button("Pause");
        Button stopButton = new Button("Stop");
        Button speedUpButton = new Button("Speed Up");
        Button slowDownButton = new Button("Slow Down");
        Button statsButton = new Button("Statistics");

        startButton.setOnAction(e -> startSimulation());
        pauseButton.setOnAction(e -> pauseSimulation());
        stopButton.setOnAction(e -> stopSimulation());
        speedUpButton.setOnAction(e -> speedUp());
        slowDownButton.setOnAction(e -> slowDown());
        statsButton.setOnAction(e -> showStatistics());

        buttons.getChildren().addAll(startButton, pauseButton, stopButton, speedUpButton, slowDownButton, statsButton);
        return buttons;
    }

    private void startSimulation() {
        System.out.println("Simulation started");
    }

    private void pauseSimulation() {
        System.out.println("Simulation paused");
    }

    private void stopSimulation() {
        System.out.println("Simulation stopped");
    }

    private void speedUp() {
        System.out.println("Speed increased");
    }

    private void slowDown() {
        System.out.println("Speed decreased");
    }

    private void showStatistics() {
        System.out.println("Showing statistics");
    }
}
