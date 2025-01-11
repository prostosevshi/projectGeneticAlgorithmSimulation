package controller;

import model.Entity;
import movingEntity.Creature;
import staticEntity.Food;
import staticEntity.Poison;

import java.util.List;
import java.util.Random;

public class GeneticLogic {
    private WorldMap worldMap;
    private Simulation simulation;

    public GeneticLogic(Simulation simulation, WorldMap worldMap) {
        this.simulation = simulation;
        this.worldMap = worldMap;
    }

    public void decideEverything(Creature creature) {
        int[][] genome = creature.getGenome();

        for (int i = creature.getI(); i < genome.length; i++) {
            for (int j = creature.getJ(); j < genome[i].length; j++) {
                creature.setI(i);
                creature.setJ(j);

                //genome for moving
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

                //genome for interacting
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

                /*//genome for checking ifPoison
                if (genome[i][j] >= 8 && genome[i][j] <= 15) {
                    interact(creature, genome[i][j]);
                    //logger.info("Creature tried to interact.");
                    //System.out.println("creature " + creature.toString() + " tried to interact");
                    if (j == 7) {
                        creature.setI(i + 1);
                    } else
                        creature.setJ(j + 1);
                    return;
                }*/

                //gen when movement obstructed

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
            creature.setHealth(0);
            simulation.entitiesToRemove.add(entity);
            creature.setPosition(newX, newY);
            return;
        }

        if (entity instanceof Food) {
            creature.changeHealth(10);
            simulation.entitiesToRemove.add(entity);
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
            simulation.entitiesToRemove.add(worldMap.getEntityAt(newX, newY));
            //logger.info("Creature ate food. Health increased.");
            //System.out.println("creature " + creature.toString() + " ate food");
        } else if (worldMap.getEntityAt(newX, newY) instanceof Poison) {
            simulation.entitiesToRemove.add(worldMap.getEntityAt(newX, newY));
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
}
