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
        int counter = creature.getActionCounter();

        int i = counter / 8;
        int j = counter % 8;
        int gene = genome[i][j];

        GeneType geneType = GeneType.fromGene(gene);

        //if turn or look no need for direction

        if (geneType.equals(GeneType.UNKNOWN)) {
            creature.changeActionCounter(gene);
            return;
        }

        Direction direction = Direction.fromGene(gene); // fromMoveGene is worng indexOutOfBounds
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

                /*if (entityType == EntityType.NOTHING || entityType == EntityType.FOOD) {*/
                    moveCreature(creature, direction);
                /*}*/
                creature.setActionCounter(newActionCounter);
            }

            case INTERACT -> {

                /*if (entityType == EntityType.FOOD) {*/
                    /*eatFood(creature, direction);*/
                    interact(creature, direction);
                /*} else if (entityType == EntityType.POISON) {*/
                    /*neutralisePoison(creature, direction);*/
                    /*interact(creature, direction);*/
                /*}*/
                creature.setActionCounter(newActionCounter);
            }

            case LOOK -> {
                look(creature, Direction.fromLookGene(gene));
                creature.setActionCounter(newActionCounter);
                //TODO executable up to 10 times
            }

            case TURN -> {
                turn(creature, Direction.fromTurnGene(gene));
                creature.setActionCounter(newActionCounter);
                //TODO executable up to 10 times
            }

            /*case UNKNOWN -> {
                creature.changeActionCounter(gene);
                //TODO executable up to 10 times
                return;
            }*/

            //creature.changeActionCounter(activeAC);
        }

        /*switch (EntityType.fromEntity(entity)) {
            case "nothing" -> {
               moveCreature(creature, direction);
               activeAC += activeAC;
            }
            case "food" -> {
                eatFood(creature, direction);
                activeAC += activeAC;
                //interact(creature, Direction.fromInteractGene(gene));
            }

            case "poison" -> {
                neutralisePoison(creature, direction);
                activeAC += activeAC;
            }

            case "rock" -> {
                turn(creature, Direction.fromTurnGene(gene));
                //TODO executable up to 10 times

                creature.setActionCounter((counter + 1) % 64);
            }

            case "anotherBot" -> {
                creature.changeActionCounter(gene);
                //TODO executable up to 10 times
            }

            creature.changeActionCounter(activeAC);
        }*/

        /*for (int i = creature.getI(); i < genome.length; i++) {
            for (int j = creature.getJ(); j < genome[i].length; j++) {
                int gene = genome[i][j];
                creature.setI(i);
                creature.setJ(j);

                switch (GeneType.fromGene(gene)) {
                    case MOVE -> {
                        moveCreature(creature, Direction.fromMoveGene(gene));
                        advanceGenePointer(creature, i, j);
                        return;
                    }
                    case INTERACT -> {
                        interact(creature, Direction.fromInteractGene(gene));
                        advanceGenePointer(creature, i, j);
                        return;
                    }
                    case UNKNOWN -> {
                        //genome[i][j] = (gene + gene) % 64;
                        creature.changeActionCounter(gene);
                    }
                }
            }

            //TODO: genome for checking ifPoison
            //TODO: gen when movement obstructed

        }*/
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
