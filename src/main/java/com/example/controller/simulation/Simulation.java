package com.example.controller.simulation;

import com.example.controller.world.WorldMap;
import com.example.controller.logic.GeneticLogic;
import com.example.model.creature.Creature;
import com.example.model.Entity;
import com.example.model.staticEntity.Food;
import com.example.model.staticEntity.Poison;
import com.example.model.staticEntity.Rock;
import javafx.application.Platform;

import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Simulation {

    private static final Logger logger = Logger.getLogger(Simulation.class.getName());
    private final Random random = new Random();
    public final LinkedList<Integer> last10Lifetimes = new LinkedList<>();

    private WorldMap worldMap;
    private GeneticLogic geneticLogic;

    private int numberOfFood, numberOfPoison, numberOfCreatures;
    private int simulationSpeed = 1000;

    public List<Entity> creaturesOfLastGen = new ArrayList<>();
    private List<Creature> top8Creatures = new ArrayList<>();
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
        } else if (simulationSpeed == 100) {
            simulationSpeed = 1;
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
        } else if (simulationSpeed == 1) {
            simulationSpeed = 100;
        } else simulationSpeed += 250;
        System.out.println("Speed decreased, new delay: " + simulationSpeed + "ms");
        /*TODO pause when too slow
           if (simulationSpeed >= 2000) {
            isPaused = true;
        }*/
    }

    private void nextTurn() {
        turnCounter++;

        //logger.info("Turn " + turnCounter);
        System.out.println("Turn: " + turnCounter + " Generation: " + genCounter + " Number of creatures alive: " + worldMap.getEntities().stream().filter(entity -> entity instanceof Creature).count());

        //worldMap.render();

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

        if (/*allCreaturesDead()*/shouldEvolve()) {

            evolveOnExtinction();

            turnCounter = 0;

            logger.info("All creatures are dead. Generation " + (genCounter += 1) + " begins.");
        }
    }

    public boolean allCreaturesDead() {
        return worldMap.getEntities().stream().noneMatch(e -> e instanceof Creature);
    }

    public boolean shouldEvolve() {
        long aliveCreatures = worldMap.getEntities().stream()
                .filter(e -> e instanceof Creature)
                .count();

        return aliveCreatures <= 8;
    }

    private void removeDeadCreatures() {

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

        List<Creature> topCreatures = selectTopCreaturesByLifetime(8);

        //setTop8Creatures(topCreatures);

        appendGenomeToFile(topCreatures.getFirst().getGenome(), this.getGeneration(), "top_genomes.txt");
        appendLifetimeToFile(topCreatures.getFirst().getLifetime(), this.getGeneration(), "lifetimes_for_graphics.txt");

        worldMap.getEntities().clear();
        creaturesOfLastGen.clear();

        int timesMutate = 1;

        for (Creature creature : topCreatures) {

            creature.refreshCreature();

            int[][] originalGenome = cloneGenome(creature.getGenome());
            creature.increaseEvolvesWithoutMutation();
            placeCreatureOnMap(originalGenome, 7);

            int[][] mutatedGenome = creature.getGenome();

            for (int i = 0; i < timesMutate; i++) {
                int randI = random.nextInt(8);
                int randJ = random.nextInt(8);
                int newRandGene = random.nextInt(64); //changing to 32 for science was 64

                mutatedGenome[randI][randJ] = newRandGene;
            }
            timesMutate++;

            creature.loseEvolvesWithoutMutation();
            placeCreatureOnMap(mutatedGenome, 1);
        }

        initializeFoodAndPoison(numberOfFood, numberOfPoison);
    }

    public static int[][] cloneGenome(int[][] genome) {
        int[][] copy = new int[8][8];
        for (int i = 0; i < 8; i++) {
            System.arraycopy(genome[i], 0, copy[i], 0, 8);
        }
        return copy;
    }

    private void appendGenomeToFile(int[][] genome, int generation, String filename) {
        StringBuilder sb = new StringBuilder();

        sb.append("Generation ").append(generation).append(":\n");

        for (int[] row : genome) {
            for (int gene : row) {
                sb.append(String.format("%2d ", gene));
            }
            sb.append("\n");
        }

        sb.append("\n");

        try (java.io.FileWriter writer = new java.io.FileWriter(filename, true)) { // true = append
            writer.write(sb.toString());
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    private void appendLifetimeToFile(int lifetime, int generation, String filename) {
        String line = String.format("Generation = %d Lifetime = %d\n", generation, lifetime);

        try (java.io.FileWriter writer = new java.io.FileWriter(filename, true)) {
            writer.write(line);
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    public void recordLifetime(List<Entity> entities) {

        final int lifetime = selectTopCreaturesByLifetime(1).getFirst().getLifetime();

        last10Lifetimes.addLast(Math.max(0, lifetime));
        if (last10Lifetimes.size() > 10) {
            last10Lifetimes.removeFirst();
        }

        if (lifetimeUpdateListener != null) {
            Platform.runLater(() -> lifetimeUpdateListener.accept(new ArrayList<>(last10Lifetimes)));
        }
    }

    public List<Creature> selectTopCreatures(int topN) {
        List<Creature> topCreatures = creaturesOfLastGen.stream()
                .filter(e -> e instanceof Creature)
                .map(e -> (Creature) e)
                .collect(Collectors.toList());

        topCreatures.sort((a, b) -> {
            int scoreA = a.getLifetime() + a.getFoodEaten() * 20;
            int scoreB = b.getLifetime() + b.getFoodEaten() * 20;
            return Integer.compare(scoreB, scoreA); // по убыванию
        });

        return topCreatures.subList(0, Math.min(topN, topCreatures.size()));
    }

    public List<Creature> selectTopCreaturesByLifetime(int topN) {
        return creaturesOfLastGen.stream()
                .filter(e -> e instanceof Creature)
                .map(e -> (Creature) e)
                .sorted((c1, c2) -> Integer.compare(c2.getLifetime(), c1.getLifetime()))
                .limit(topN)
                .toList();
    }

    public List<Creature> selectTopCreaturesByFoodEaten(int topN) {
        return creaturesOfLastGen.stream()
                .filter(e -> e instanceof Creature)
                .map(e -> (Creature) e)
                .sorted((c1, c2) -> Integer.compare(c2.getFoodEaten(), c1.getFoodEaten()))
                .limit(topN)
                .toList();
    }

    public void initializeFoodAndPoison(int numberOfFood, int numberOfPoison) {
        int width = worldMap.getWidth();
        int height = worldMap.getHeight();

        //Rocks
        for (int x = 0; x < width; x++) {
            worldMap.addEntity(new Rock(x, 0));
            worldMap.addEntity(new Rock(x, height - 1));
        }
        for (int y = 0; y < height; y++) {
            worldMap.addEntity(new Rock(0, y));
            worldMap.addEntity(new Rock(width - 1, y));
        }

        int firstStripeX = worldMap.getWidth() / 4;
        //int secondStripeX = (3 * worldMap.getWidth()) / 4;
        int centerStripeX = worldMap.getWidth() / 2;
        int halfHeight = worldMap.getHeight() / 2;

        for (int y = 0; y < halfHeight - 4; y++) {
            worldMap.addEntity(new Rock(firstStripeX, y));                             // сверху вниз
            //worldMap.addEntity(new Rock(secondStripeX, worldMap.getHeight() - 1 - y)); // снизу вверх
        }
        for (int y = 8; y < 16; y++) {
            worldMap.addEntity(new Rock(centerStripeX, y));
        }
        //Rocks

        for (int i = 0; i < numberOfFood; i++) {
            int x, y;
            do {
                x = random.nextInt(width - 2) + 1;
                y = random.nextInt(height - 2) + 1;
            } while (worldMap.getEntityAt(x, y) != null);
            worldMap.addEntity(new Food(x, y));
        }

        for (int i = 0; i < numberOfPoison; i++) {
            int x, y;
            do {
                x = random.nextInt(width - 2) + 1;
                y = random.nextInt(height - 2) + 1;
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
                    genome[k][j] = random.nextInt(64);
                }
            }

            /*int[][] genome = {
                    {38, 21, 40, 18, 39, 59, 48, 63},
                    {37, 24, 32, 47,  7,  8,  2, 61},
                    {43, 41,  8, 22, 14, 28, 57, 61},
                    {6, 56, 34, 45, 49, 16, 12, 30},
                    {26, 61, 49, 27, 30, 35, 0, 58},
                    {58, 15, 62, 6, 52, 53, 50, 27},
                    {13, 42, 54, 51, 60, 9, 5, 58},
                    {34, 25, 61, 41, 59, 14, 43, 22}
            };*/

            worldMap.addEntity(new Creature(x, y, genome));
        }
    }

    public void addFoodAndPoisonWhenLow() {
        long poisonCount = worldMap.getEntities().stream().filter(entity -> entity instanceof Poison).count();
        long foodCount = worldMap.getEntities().stream().filter(entity -> entity instanceof Food).count();
        long creatureCount = worldMap.getEntities().stream().filter(entity -> entity instanceof Creature).count();

        if (poisonCount < numberOfPoison) {
            int poisonToAdd = (int) (numberOfPoison - poisonCount);
            initializeFoodAndPoison(0, poisonToAdd);
        }

        if (foodCount < numberOfFood + numberOfCreatures - creatureCount) {
            int foodToAdd = (int) (numberOfFood - foodCount + numberOfCreatures - creatureCount);
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

    public int getNumberOfCreaturesAlive() {
        List<Entity> snapshot = new ArrayList<>(worldMap.getEntities());
        long count = snapshot.stream()
                .filter(e -> e instanceof Creature)
                .count();
        return (int) count;
        //return (int) worldMap.getEntities().stream().filter(entity -> entity instanceof Creature).count();
    }

    public int getGeneration() {
        return genCounter;
    }

    /*public void setTop8Creatures(List<Creature> top8Creatures) {
        this.top8Creatures = top8Creatures;
    }

    public List<Creature> getTop8Creatures() {
        return top8Creatures;
    }*/
}
