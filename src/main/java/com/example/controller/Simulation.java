package com.example.controller;

import com.example.movingEntity.Creature;
import com.example.model.Entity;
import com.example.staticEntity.Food;
import com.example.staticEntity.Poison;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Simulation {

    private static final Logger logger = Logger.getLogger(Simulation.class.getName());

    private final WorldMap worldMap;
    private final GeneticLogic geneticLogic;

    private final Random random = new Random();

    private int numberOfFood, numberOfPoison, numberOfCreatures;

    public List<Entity> creaturesOfLastGen = new ArrayList<>();
    public List<Entity> entitiesToRemove = new ArrayList<>();
    public Map<Integer, Integer> statMap = new LinkedHashMap<>();

    private int turnCounter = 0;
    private int genCounter = 1;

    public Simulation(int mapWidth, int mapHeight) {
        this.worldMap = new WorldMap(mapWidth, mapHeight);
        this.geneticLogic = new GeneticLogic(this, worldMap);
        //initializeMap();
    }

    private void initializeMap() {
        initializeFoodAndPoison(numberOfFood, numberOfPoison);
        initializeCreatures(numberOfCreatures);
    }

    public void start() {
        logger.info("Starting simulation...");
        //worldMap.render();
        while (true) {
            nextTurn();
            /*if (genCounter == 20000) {
                System.out.println(statMap);
                break;
            }*/
            try {
                Thread.sleep(1500); // 1000 миллисекунд = 1 секунда
            } catch (InterruptedException e) {
                logger.log(Level.SEVERE, "Simulation interrupted: " + e.getMessage(), e);
                Thread.currentThread().interrupt();
            }

            /*if (allCreaturesDead()) {
                //evolveOnExtinction();
            }*/
        }
    }

    private void nextTurn() {
        turnCounter++;

        logger.info("Turn " + turnCounter);
        System.out.println("Turn: " + turnCounter + " Generation: " + genCounter + " Number of creatures alive: " + worldMap.getEntities().stream().filter(entity -> entity instanceof Creature).count());
        //System.out.println(map);

        worldMap.render();

        List<Entity> currentEntities = new ArrayList<>(worldMap.getEntities());

        for (Entity entity : currentEntities) {
            if (entity instanceof Creature creature) {

                geneticLogic.decideEverything(creature);

                creature.changeHealth(-1);
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

        //worldMap.render();

        if (allCreaturesDead()) {

            statMap.put(genCounter, /*calculateAverageLifetime())*/turnCounter);

            evolveOnExtinction();

            turnCounter = 0;

            //System.out.println(map);
            logger.info("All creatures are dead. Generation " + (genCounter += 1) + " begins.");
        }

        //System.out.println("Top creature's lifetime: " + worldMap.getEntities().stream().toList().getFirst().getLifetime());
    }

    private boolean allCreaturesDead() {
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
        /*int x, y;
        do {
            x = random.nextInt(worldMap.getWidth());
            y = random.nextInt(worldMap.getHeight());
        } while (worldMap.getEntityAt(x, y) != null);

        creature.setPosition(x, y);*/

        for (int i = 0; i < number; i++) {
            int x, y;
            do {
                x = random.nextInt(worldMap.getWidth());
                y = random.nextInt(worldMap.getHeight());
            } while (worldMap.getEntityAt(x, y) != null);

            worldMap.addEntity(new Creature(x, y, genome));
        }
    }

    /*private double calculateAverageLifetime() {
        return creaturesOfLastGen.stream()
                .filter(e -> e instanceof Creature)  // Оставляем только существа
                .map(e -> (Creature) e)              // Преобразуем в Creature
                .sorted((c1, c2) -> Integer.compare(c2.getLifetime(), c1.getLifetime())) // Сортируем по убыванию жизни
                .mapToInt(Creature::getLifetime)     // Извлекаем продолжительность жизни
                .average()                           // Вычисляем среднее значение
                .orElse(0.0);                        // Возвращаем 0.0, если нет данных
    }*/

    private void evolveOnExtinction() {

        //int newPopulationSize = numberOfCreatures;

        List<Creature> topCreatures = selectTopCreatures(8);

        // Clear the map and spawn new creatures
        worldMap.getEntities().clear();
        creaturesOfLastGen.clear();

        for (Creature creature : topCreatures) {

            creature.setHealth(10);
            creature.setLifetime(0);

            placeCreatureOnMap(creature.getGenome(), 7);

            initializeCreatures( 1);

        }

        initializeFoodAndPoison(numberOfFood, numberOfPoison);
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

    public void setParameters(int numberOfFood, int numberOfPoison, int numberOfCreatures) {
        this.numberOfCreatures = numberOfCreatures;
        this.numberOfFood = numberOfFood;
        this.numberOfPoison = numberOfPoison;
        initializeMap();
    }
}
