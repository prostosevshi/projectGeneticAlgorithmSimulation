package com.example.controller.simulation;

import com.example.controller.world.WorldMap;
import com.example.controller.logic.GeneticLogic;
import com.example.model.creature.Creature;
import com.example.model.Entity;
import com.example.model.staticEntity.Food;
import com.example.model.staticEntity.Poison;
import javafx.application.Platform;

import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Simulation {

    private static final Logger logger = Logger.getLogger(Simulation.class.getName());
    private final Random random = new Random();
    public final LinkedList<Integer> last10Lifetimes = new LinkedList<>();

    private WorldMap worldMap;
    private GeneticLogic geneticLogic;

    private int numberOfFood, numberOfPoison, numberOfCreatures;
    private int simulationSpeed = 1000;

    public List<Entity> creaturesOfLastGen = new ArrayList<>();
    public List<Entity> entitiesToRemove = new ArrayList<>();
    private Consumer<List<Integer>> lifetimeUpdateListener;

    private int turnCounter = 0;
    private int genCounter = 1;

    private boolean isPaused;

    public Simulation() {
        this.isPaused = true;
        //start();
    }

    public void initializeSimulation(int mapWidth, int mapHeight) {
        this.worldMap = new WorldMap(mapWidth, mapHeight);
        this.geneticLogic = new GeneticLogic(this, worldMap);
    }

    private void initializeMap() {
        initializeFoodAndPoison(numberOfFood, numberOfPoison);
        initializeCreatures(numberOfCreatures);
    }

    public void updateMapSize(int newWidth, int newHeight) {
        worldMap.resize(newWidth, newHeight);
    }

    public void start() {
        logger.info("Starting simulation...");

        while (!isPaused) {

            nextTurn();
            try {
                Thread.sleep(simulationSpeed); // 1000 миллисекунд = 1 секунда
            } catch (InterruptedException e) {
                logger.log(Level.SEVERE, "Simulation interrupted: " + e.getMessage(), e);
                Thread.currentThread().interrupt();
            }
        }
    }

    public void pause() {
        isPaused = true;
        System.out.println("Simulation paused");
    }

    public void resumeSimulation() {
        if (isPaused) {
            isPaused = false;
            logger.info("Simulation resumed");

            while (!isPaused) {
                nextTurn();
                try {
                    Thread.sleep(simulationSpeed);
                } catch (InterruptedException e) {
                    logger.log(Level.SEVERE, "Simulation interrupted", e);
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }

    public void resetSimulation() {
        isPaused = true;
        logger.info("Simulation stopped");

        worldMap.getEntities().clear();
        creaturesOfLastGen.clear();
        entitiesToRemove.clear();

        turnCounter = 0;
        genCounter = 1;

        logger.info("Simulation reset complete");
    }

    public void increaseSpeed() {
        if (simulationSpeed == 250) {
            simulationSpeed = 100;
        } else if (simulationSpeed > 250) {
            simulationSpeed -= 250;
        }
        System.out.println("Speed increased, new delay: " + simulationSpeed + "ms");
        /*TODO pause when too slow
           if (simulationSpeed < 2000 && isPaused) {
            resumeSimulation();
        }*/
    }

    public void decreaseSpeed() {
        if (simulationSpeed == 100) {
            simulationSpeed = 250;
        } else simulationSpeed += 250;
        System.out.println("Speed decreased, new delay: " + simulationSpeed + "ms");
        /*TODO pause when too slow
           if (simulationSpeed >= 2000) {
            isPaused = true;
        }*/
    }

    private void nextTurn() {
        turnCounter++;

        logger.info("Turn " + turnCounter);
        System.out.println("Turn: " + turnCounter + " Generation: " + genCounter + " Number of creatures alive: " + worldMap.getEntities().stream().filter(entity -> entity instanceof Creature).count());

        worldMap.render();

        List<Entity> currentEntities = new ArrayList<>(worldMap.getEntities());

        for (Entity entity : currentEntities) {
            if (entity instanceof Creature creature) {

                geneticLogic.decideEverything(creature);

                creature.changeHealth(-1);
                creature.changeLifetime(1);
            }
        }

        if (!entitiesToRemove.isEmpty()) {
            for (Entity entity : entitiesToRemove) {
                worldMap.removeEntity(entity);
            }
            entitiesToRemove.clear();
        }

        removeDeadCreatures();

        addFoodAndPoisonWhenLow();

        for (Entity entity : currentEntities) {
            if (entity instanceof Creature creature) {
                creature.setLifetime(creature.getLifetime() + 1);
            }
        }

        if (allCreaturesDead()) {

            evolveOnExtinction();

            turnCounter = 0;

            logger.info("All creatures are dead. Generation " + (genCounter += 1) + " begins.");
        }
    }

    public boolean allCreaturesDead() {
        return worldMap.getEntities().stream().noneMatch(e -> e instanceof Creature);
    }

    private void removeDeadCreatures() {
        //List<Entity> toRemove = new ArrayList<>();
        for (Entity entity : worldMap.getEntities()) {
            if (entity instanceof Creature creature && creature.getHealth() <= 0) {
                entitiesToRemove.add(entity);
                creaturesOfLastGen.add(entity);
                //logger.info("Creature died.");
            }
        }
        worldMap.getEntities().removeAll(entitiesToRemove);
    }

    public void placeCreatureOnMap(int[][] genome, int number) {

        for (int i = 0; i < number; i++) {
            int x, y;
            do {
                x = random.nextInt(worldMap.getWidth());
                y = random.nextInt(worldMap.getHeight());
            } while (worldMap.getEntityAt(x, y) != null);

            worldMap.addEntity(new Creature(x, y, genome));
        }
    }

    /*TODO average lifetime
       private double calculateAverageLifetime() {
        return creaturesOfLastGen.stream()
                .filter(e -> e instanceof Creature)  // Оставляем только существа
                .map(e -> (Creature) e)              // Преобразуем в Creature
                .sorted((c1, c2) -> Integer.compare(c2.getLifetime(), c1.getLifetime())) // Сортируем по убыванию жизни
                .mapToInt(Creature::getLifetime)     // Извлекаем продолжительность жизни
                .average()                           // Вычисляем среднее значение
                .orElse(0.0);                        // Возвращаем 0.0, если нет данных
    }*/

    private void evolveOnExtinction() {

        recordLifetime(creaturesOfLastGen);

        List<Creature> topCreatures = selectTopCreatures(5);

        worldMap.getEntities().clear();
        creaturesOfLastGen.clear();

        for (Creature creature : topCreatures) {

            creature.setHealth(10);
            creature.setLifetime(0);

            placeCreatureOnMap(creature.getGenome(), 11);


            int[][] mutatedGenome = creature.getGenome();

            int timesMutate = random.nextInt(8);
            for (int i = 0; i < timesMutate; i++) {
                int randI = random.nextInt(8);
                int randJ = random.nextInt(8);
                int newRandGene = random.nextInt(64);

                mutatedGenome[randI][randJ] = newRandGene;
            }

            placeCreatureOnMap(mutatedGenome, 1);

            //initializeCreatures(1);
        }

        initializeFoodAndPoison(numberOfFood, numberOfPoison);
    }

    public void recordLifetime(List<Entity> entities) {
        final int lifetime = entities.stream()
                .filter(entity -> entity instanceof Creature)
                .map(entity -> (Creature) entity)
                .mapToInt(Creature::getLifetime)
                .max()
                .orElse(0);

        last10Lifetimes.addLast(Math.max(0, lifetime));
        if (last10Lifetimes.size() > 10) {
            last10Lifetimes.removeFirst();
        }

        if (lifetimeUpdateListener != null) {
            Platform.runLater(() -> lifetimeUpdateListener.accept(new ArrayList<>(last10Lifetimes)));
        }
    }


    public List<Creature> selectTopCreatures(int topN) {
        return creaturesOfLastGen.stream()
                .filter(e -> e instanceof Creature)
                .map(e -> (Creature) e)
                .sorted((c1, c2) -> Integer.compare(c2.getLifetime(), c1.getLifetime()))
                .limit(topN)
                .toList();
    }

    public void initializeFoodAndPoison(int numberOfFood, int numberOfPoison) {
        for (int i = 0; i < numberOfFood; i++) {
            int x, y;
            do {
                x = random.nextInt(worldMap.getWidth());
                y = random.nextInt(worldMap.getHeight());
            } while (worldMap.getEntityAt(x, y) != null);
            worldMap.addEntity(new Food(x, y));
        }

        for (int i = 0; i < numberOfPoison; i++) {
            int x, y;
            do {
                x = random.nextInt(worldMap.getWidth());
                y = random.nextInt(worldMap.getHeight());
            } while (worldMap.getEntityAt(x, y) != null);
            worldMap.addEntity(new Poison(x, y));
        }
    }

    public void initializeCreatures(int numberOfCreatures) {
        for (int i = 0; i < numberOfCreatures; i++) {
            int x, y;
            do {
                x = random.nextInt(worldMap.getWidth());
                y = random.nextInt(worldMap.getHeight());
            } while (worldMap.getEntityAt(x, y) != null);
            int[][] genome = new int[8][8];
            for (int k = 0; k < 8; k++) {
                for (int j = 0; j < 8; j++) {
                    genome[k][j] = random.nextInt(64);  // от 0 до 63
                }
            }
            worldMap.addEntity(new Creature(x, y, genome));
        }
    }

    public void addFoodAndPoisonWhenLow() {
        long poisonCount = worldMap.getEntities().stream().filter(entity -> entity instanceof Poison).count();
        long foodCount = worldMap.getEntities().stream().filter(entity -> entity instanceof Food).count();
        //long creatureCount = worldMap.getEntities().stream().filter(entity -> entity instanceof Creature).count();

        if (poisonCount < numberOfPoison / 2) {
            int poisonToAdd = (int) (numberOfPoison - poisonCount) / 2;
            initializeFoodAndPoison(0, poisonToAdd);
        }

        if (foodCount < numberOfFood) {
            int foodToAdd = (int) (numberOfFood - foodCount);
            initializeFoodAndPoison(foodToAdd, 0);
        }
    }

    public boolean isPaused() {
        return isPaused;
    }

    //setters&getters

    public void setLifetimeUpdateListener(Consumer<List<Integer>> listener) {
        this.lifetimeUpdateListener = listener;
    }

    public void setParameters(int numberOfFood, int numberOfPoison, int numberOfCreatures) {
        this.numberOfCreatures = numberOfCreatures;
        this.numberOfFood = numberOfFood;
        this.numberOfPoison = numberOfPoison;
        initializeMap();
    }

    public int getNumberOfFood() {
        return numberOfFood;
    }

    public int getNumberOfPoison() {
        return numberOfPoison;
    }

    public int getNumberOfCreatures() {
        return numberOfCreatures;
    }

    public WorldMap getWorldMap() {
        return worldMap;
    }

    public double getSpeed() {
        return (2.0 - (double) simulationSpeed / 1000);
    }

    public int getGenCounter() {
        return genCounter;
    }

    public int getNumberOfCreaturesAlive() {
        return (int) worldMap.getEntities().stream().filter(entity -> entity instanceof Creature).count();
    }
}
