package controller;

import movingEntity.Creature;
import model.Entity;
import staticEntity.Food;
import staticEntity.Poison;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Simulation {

    private static final Logger logger = Logger.getLogger(Simulation.class.getName());
    private final WorldMap worldMap;
    private int turnCounter = 0;
    private int genCounter = 1;
    private int numberOfFood, numberOfPoison, numberOfCreatures;
    private List<Entity> entitiesToRemove = new ArrayList<>();

    public Simulation(int mapWidth, int mapHeight) {
        this.worldMap = new WorldMap(mapWidth, mapHeight);
        //initializeMap();
    }

    private void initializeMap() {
        Random random = new Random();

        initializeFoodAndPoison(random, numberOfFood, numberOfPoison);
        initializeCreatures(random, numberOfCreatures);
    }

    private void nextTurn() {
        turnCounter++;
        //logger.info("Turn " + turnCounter);
        System.out.println("Turn " + turnCounter);

        worldMap.render();

        List<Entity> currentEntities = new ArrayList<>(worldMap.getEntities());

        for (Entity entity : currentEntities) {
            if (entity instanceof Creature creature) {
                decideEverything(creature);

                creature.changeHealth(-1);
            }
        }

        for (Entity entity : entitiesToRemove) {
            worldMap.removeEntity(entity);
        }

        removeDeadCreatures();

        checkAndAddFood();

        for (Entity entity : currentEntities) {
            if (entity instanceof Creature creature) {
                creature.setLifetime(creature.getLifetime() + 1);
            }
        }

        //worldMap.render();

        if (allCreaturesDead()) {
            evolveOnExtinction();
            turnCounter = 0;

            logger.info("All creatures are dead. Generation " + (genCounter += 1) + " begins.");
        }
    }

    public void checkAndAddFood() {
        boolean foodExists = worldMap.getEntities().stream().anyMatch(entity -> entity instanceof Food);

        if (!foodExists) {
            initializeFoodAndPoison(new Random(), 1, 0);
        }
    }


    public void interact(Creature creature, int direction) {

        int newX = creature.getX();
        int newY = creature.getY();

        switch (direction) {
            case 8:
                newX -= 1;
                newY -= 1;
                break; // Влево вверх
            case 9:
                newY -= 1;
                break; // Вверх
            case 10:
                newY -= 1;
                newX += 1;
                break; // Вверх вправо
            case 11:
                newX += 1;
                break; // Вправо
            case 12:
                newY += 1;
                newX += 1;
                break; // Вниз вправо
            case 13:
                newY += 1;
                break; // Вниз
            case 14:
                newY += 1;
                newX -= 1;
                break; // Вниз влево
            case 15:
                newX -= 1;
                break; // Влево
        }

        if (worldMap.getEntityAt(newX, newY) instanceof Food) {
            creature.changeHealth(10);
            entitiesToRemove.add(worldMap.getEntityAt(newX, newY));
            //logger.info("Creature ate food. Health increased.");
            //System.out.println("creature " + creature.toString() + " ate food");
        } else if (worldMap.getEntityAt(newX, newY) instanceof Poison) {
            entitiesToRemove.add(worldMap.getEntityAt(newX, newY));
            worldMap.addEntity(new Food(newX, newY));
            //.out.println("creature " + creature.toString() + " converted poison to food");
            //logger.info("Creature encountered poison. Health decreased.");
        }
    }

    private void moveCreature(Creature creature, int direction) {

        int newX = creature.getX();
        int newY = creature.getY();

        switch (direction) {
            case 0:
                newX -= 1;
                newY -= 1;
                break; // Влево вверх
            case 1:
                newY -= 1;
                break; // Вверх
            case 2:
                newY -= 1;
                newX += 1;
                break; // Вверх вправо
            case 3:
                newX += 1;
                break; // Вправо
            case 4:
                newY += 1;
                newX += 1;
                break; // Вниз вправо
            case 5:
                newY += 1;
                break; // Вниз
            case 6:
                newY += 1;
                newX -= 1;
                break; // Вниз влево
            case 7:
                newX -= 1;
                break; // Влево
        }

        if (newX >= 0 && newX < worldMap.getWidth() && newY >= 0 && newY < worldMap.getHeight()) {
            encounter(creature, worldMap.getEntityAt(newX, newY), newX, newY);
        }
    }

    public void decideEverything(Creature creature) {
        int[][] genome = creature.getGenome();

        for (int i = creature.getI(); i < genome.length; i++) {
            for (int j = creature.getJ(); j < genome[i].length; j++) {
                creature.setI(i);
                creature.setJ(j);
                if (genome[i][j] >= 0 && genome[i][j] <= 7) {
                    moveCreature(creature, genome[i][j]);
                    //logger.info("Creature moved.");
                    //System.out.println("creature " + creature.toString() + " moved");
                    if (j == 7) {
                        creature.setI(i + 1);
                        creature.setJ(0);
                    } else
                        creature.setJ(j + 1);
                    return;
                }

                if (genome[i][j] >= 8 && genome[i][j] <= 15) {
                    interact(creature, genome[i][j]);
                    //logger.info("Creature tried to interact.");
                    //System.out.println("creature " + creature.toString() + " tried to interact");
                    if (j == 7) {
                        creature.setI(i + 1);
                    } else
                        creature.setJ(j + 1);
                    return;
                }
                if (genome[i][j] > 15) {
                    genome[i][j] += genome[i][j];
                    if (genome[i][j] >= 63) {
                        genome[i][j] -= 63;
                    }
                }

                if (j == 7)
                    creature.setJ(0);

                if (i == 7)
                    creature.setI(0);
            }
        }
    }

    private void encounter(Creature creature, Entity entity, int newX, int newY) {

        if (entity instanceof Poison) {
            creature.changeHealth(-10);
            entitiesToRemove.add(entity);
            creature.setPosition(newX, newY);
            return;
        }

        if (entity instanceof Food) {
            creature.changeHealth(10);
            entitiesToRemove.add(entity);
            creature.setPosition(newX, newY);
            return;
        }

        if (entity instanceof Creature) {
            if (creature.toString().equals(entity.toString())) {
                creature.setPosition(newX, newY);
            }
        } else {
            creature.setPosition(newX, newY);
        }
    }

    private boolean allCreaturesDead() {
        return worldMap.getEntities().stream().noneMatch(e -> e instanceof Creature);
    }

    private void removeDeadCreatures() {
        List<Entity> toRemove = new ArrayList<>();
        for (Entity entity : worldMap.getEntities()) {
            if (entity instanceof Creature creature && creature.getHealth() <= 0) {
                toRemove.add(entity);
                //logger.info("Creature died.");
            }
        }
        worldMap.getEntities().removeAll(toRemove);
    }

    private void evolveOnExtinction() {

        Random random = new Random();
        int newPopulationSize = numberOfCreatures;

        List<Creature> topCreatures = selectTopCreatures(numberOfCreatures/5);

        // Clear the map and spawn new creatures
        worldMap.getEntities().clear();

        for (Creature creature : topCreatures) {
            creature.changeHealth(10);
            placeCreatureOnMap(creature, random);
            worldMap.addEntity(creature);
        }

        initializeCreatures(random, (newPopulationSize - topCreatures.size()));
        initializeFoodAndPoison(random, numberOfFood, numberOfPoison);
    }

    private void placeCreatureOnMap(Creature creature, Random random) {
        int x, y;
        do {
            x = random.nextInt(worldMap.getWidth());
            y = random.nextInt(worldMap.getHeight());
        } while (worldMap.getEntityAt(x, y) != null);

        creature.setPosition(x, y);
    }

    private List<Creature> selectTopCreatures(int topN) {
        return worldMap.getEntities().stream()
                .filter(e -> e instanceof Creature)
                .map(e -> (Creature) e)
                .sorted((c1, c2) -> Integer.compare(c2.getLifetime(), c1.getLifetime()))
                .limit(topN)
                .toList();
    }

    private void initializeFoodAndPoison(Random random, int numberOfFood, int numberOfPoison) {
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

    private void initializeCreatures(Random random, int numberOfCreatures) {
        for (int i = 0; i < numberOfCreatures; i++) {
            int x, y;
            do {
                x = random.nextInt(worldMap.getWidth());
                y = random.nextInt(worldMap.getHeight());
            } while (worldMap.getEntityAt(x, y) != null);
            int[][] genome = new int[8][8];
            for (int k = 0; k < 8; k++) {
                for (int j = 0; j < 8; j++) {
                    genome[k][j] = random.nextInt(64);  // Генерируем случайное число от 0 до 63
                }
            }
            worldMap.addEntity(new Creature(x, y, genome));
        }
    }

    public void start() {
        logger.info("Starting simulation...");
        //worldMap.render();
        while (true) {
            nextTurn();

            try {
                Thread.sleep(500); // 1000 миллисекунд = 1 секунда
            } catch (InterruptedException e) {
                logger.log(Level.SEVERE, "Simulation interrupted: " + e.getMessage(), e);
                Thread.currentThread().interrupt();
            }

            /*if (allCreaturesDead()) {
                //evolveOnExtinction();
            }*/
        }
    }

    public void setParameters(int numberOfFood, int numberOfPoison, int numberOfCreatures) {
        this.numberOfCreatures = numberOfCreatures;
        this.numberOfFood = numberOfFood;
        this.numberOfPoison = numberOfPoison;
        initializeMap();
    }
}
