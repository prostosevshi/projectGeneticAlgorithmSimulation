package com.example.ui;

import com.example.controller.Simulation;

public class UISimulationController {

    private Simulation simulation;
    private Thread simulationThread;

    public UISimulationController() {
        this.simulation = new Simulation();
    }

    public void initializeSimulation(int mapWidth, int mapHeight, int numberOfFood, int numberOfPoison, int numberOfCreatures) {
        simulation.initializeSimulation(mapWidth, mapHeight);
        simulation.setParameters(numberOfFood, numberOfPoison, numberOfCreatures);
    }

    public void startSimulation() {
        if (simulationThread == null || !simulationThread.isAlive()) {
            simulationThread = new Thread(() -> simulation.start());
            simulationThread.setDaemon(true);
            simulationThread.start();
        }
    }

    public void pauseOrResumeSimulation() {
        if(simulation.isPaused()){
            if (simulationThread == null || !simulationThread.isAlive()) {
                simulationThread = new Thread(() -> simulation.resumeSimulation());
                simulationThread.setDaemon(true);
                simulationThread.start();
            }
        } else simulation.pause();
    }

    public void resetSimulation() {
        if(simulationThread == null || !simulationThread.isAlive()) {
            simulation.resetSimulation();
        }

        initializeSimulation(40, 20, 80, 80, 64);
    }

    public void increaseSpeed() {
        simulation.increaseSpeed();
    }

    public void decreaseSpeed() {
        simulation.decreaseSpeed();
    }

    public void changeMapSize(int newWidth, int newHeight) {
        simulation.updateMapSize(newWidth, newHeight);
    }

    public void updateParameters(int numberOfFood, int numberOfPoison, int numberOfCreatures) {
        simulation.setParameters(numberOfFood, numberOfPoison, numberOfCreatures);
    }

    //TODO: overall geninfo button

    //getters&setters

    public Simulation getSimulation() {
        return simulation;
    }
}
