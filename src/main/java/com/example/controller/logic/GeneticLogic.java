package com.example.controller.logic;

import com.example.controller.enums.EntityType;
import com.example.controller.simulation.Simulation;
import com.example.controller.world.WorldMap;
import com.example.controller.enums.Direction;
import com.example.controller.enums.GeneType;
import com.example.model.Entity;
import com.example.model.creature.Creature;
import com.example.model.staticEntity.Food;
import com.example.model.staticEntity.Poison;
import com.example.model.staticEntity.Rock;

public class GeneticLogic {

    private final WorldMap worldMap;
    private final Simulation simulation;

    public GeneticLogic(Simulation simulation, WorldMap worldMap) {
        this.simulation = simulation;
        this.worldMap = worldMap;
    }

    public void decideEverything(Creature creature) {
        int[][] genome = creature.getGenome();

        for (int step = 0; step < 10; step++) {
            int counter = creature.getActionCounter();
            int i = counter / 8;
            int j = counter % 8;
            int gene = genome[i][j];

            GeneType geneType = GeneType.fromGene(gene);

            //if turn or look no need for direction

            if (geneType.equals(GeneType.UNKNOWN)) {
                creature.changeActionCounter(gene);
                continue;
            }

            int counterForDirection = (counter + 1) % 64;
            int iForDirection = counterForDirection / 8;
            int jForDirection = counterForDirection % 8;
            int geneForDirection = genome[iForDirection][jForDirection];

            Direction direction = Direction.fromMoveGene(geneForDirection % 8);
            //Direction direction = Direction.fromGene(gene);

            Entity entity = worldMap.getEntityInDirection(creature, direction);
            EntityType entityType = EntityType.fromEntity(entity);
            int offset = entityType.getOffset();

            int newCounter = (counter + offset) % 64;
            int geneFromNewCounter = genome[newCounter / 8][newCounter % 8];
            int newActionCounter = (counter + geneFromNewCounter) % 64;

            //int direction = (creature.getActionCounter() + 1) % 8;

            switch (geneType) {
                case MOVE -> {
                    //TODO mb add direction from next ac

                    moveCreature(creature, direction);
                    creature.setActionCounter(newActionCounter);
                    return;
                }

                case INTERACT -> {

                    interact(creature, direction);
                    creature.setActionCounter(newActionCounter);
                    return;
                }

                case LOOK -> {

                    creature.setActionCounter(newActionCounter);
                }

                /*case TURN -> {

                    turn(creature, Direction.fromTurnGene(gene));
                    creature.changeActionCounter(1);
                }*/
            }
        }
    }

    private void neutralisePoison(Creature creature, Direction direction) {
    }

    private void eatFood(Creature creature, Direction direction) {
    }

    private void turn(Creature creature, Direction direction) {
    }

    private void look(Creature creature, Direction direction) {
    }

    /*private void advanceGenePointer(Creature creature, int i, int j) {
        int newI = (j == 7) ? i + 1 : i;
        int newJ = (j == 7) ? 0 : j + 1;

        if (j == 7) {
            creature.setI(i + 1);
            creature.setJ(0);
        } else {
            creature.setI(i);
            creature.setJ(j + 1);
        }

        if (newI >= creature.getGenome().length) {
            newI = 0;
            newJ = 0;
        }

        creature.setI(newI);
        creature.setJ(newJ);
    }*/

    public void interact(Creature creature, Direction direction) {
        int newX = creature.getX() + direction.dx();
        int newY = creature.getY() + direction.dy();

        Entity entity = worldMap.getEntityAt(newX, newY);

        if (entity instanceof Food) {
            creature.changeHealth(10);
            simulation.entitiesToRemove.add(entity);
        } else if (entity instanceof Poison) {
            simulation.entitiesToRemove.add(entity);
            worldMap.addEntity(new Food(newX, newY));
        }
    }

    private void moveCreature(Creature creature, Direction direction) {
        int newX = creature.getX() + direction.dx();
        int newY = creature.getY() + direction.dy();

        if (newX >= 0 && newX < worldMap.getWidth() && newY >= 0 && newY < worldMap.getHeight()) {
            encounter(creature, worldMap.getEntityAt(newX, newY), newX, newY);
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
}
