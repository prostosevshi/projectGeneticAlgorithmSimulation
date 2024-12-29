package controller;

import model.Creature;
import model.Entity;
import service.Action;

import java.util.ArrayList;
import java.util.List;

public class Simulation {

    private final WorldMap worldMap;
    private final List<Action> turnActions;
    private boolean isRunning;
    private int turnCounter;

    public Simulation(int mapWidth, int mapHeight) {
        this.worldMap = new WorldMap(mapWidth, mapHeight);
        this.turnActions = new ArrayList<>();
        this.isRunning = false;
        this.turnCounter = 0;
    }

    public void addTurnAction(Action action) {
        turnActions.add(action);
    }

    public void startSimulation(){
        isRunning = true;
        while (isRunning) {
            nextTurn();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("controller.Simulation interrupted");
            }
        }
    }

    private void nextTurn() {
        System.out.println("Turn " + (++turnCounter));

        for (Entity entity : worldMap.getEntities().values()) {
            if (entity instanceof Creature) {
                Creature creature = (Creature) entity;
                creature.makeMove(worldMap);  // Вызываем метод makeMove для каждого существа
            }
        }

        for (Action action : turnActions) {
            action.execute(worldMap);
        }

        worldMap.render();
    }

    public WorldMap getWorldMap() {
        return worldMap;
    }

    public void pauseSimulation(){
        isRunning = false;
    }

    public void initActions(){}

    public void turnActions(){}
}
