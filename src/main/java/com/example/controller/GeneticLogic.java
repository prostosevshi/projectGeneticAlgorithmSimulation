package com.example.controller;

import com.example.model.Entity;
import com.example.movingEntity.Creature;
import com.example.staticEntity.Food;
import com.example.staticEntity.Poison;

public class GeneticLogic {

    private final WorldMap worldMap;
    private final Simulation simulation;

    public GeneticLogic(Simulation simulation, WorldMap worldMap) {
        this.simulation = simulation;
        this.worldMap = worldMap;
    }

    public void decideEverything(Creature creature) {
        int[][] genome = creature.getGenome();

        for (int i = creature.getI(); i < genome.length; i++) {
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
                        genome[i][j] = (gene + gene) % 64;
                    }
                }
            }

            //TODO: genome for checking ifPoison
            //TODO: gen when movement obstructed


        }
    }

    private void advanceGenePointer(Creature creature, int i, int j) {
        int newI = (j == 7) ? i + 1 : i;
        int newJ = (j == 7) ? 0 : j + 1;

        /*if (j == 7) {
            creature.setI(i + 1);
            creature.setJ(0);
        } else {
            creature.setI(i);
            creature.setJ(j + 1);
        }*/

        if (newI >= creature.getGenome().length) {
            newI = 0;
            newJ = 0;
        }

        creature.setI(newI);
        creature.setJ(newJ);
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
}
