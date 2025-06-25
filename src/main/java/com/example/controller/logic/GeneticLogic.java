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

            int ac = creature.getActionCounter();
            int gene = genome[ac / 8][ac % 8];
            GeneType type = GeneType.fromGene(gene);

            if (type.equals(GeneType.UNKNOWN)) {
                creature.changeActionCounter(gene);
                continue;
            }

            int acForDir = (ac + 1) % 64;
            int genForDir = genome[acForDir / 8][acForDir % 8];
            Direction direction = creature.getDirection().rotate(genForDir % 8);
            Entity entity = worldMap.getEntityInDirection(creature, direction);
            EntityType entityType = EntityType.fromEntity(entity);
            int offset = entityType.getOffset();

            int acWithOffset = (ac + offset) % 64;
            /*int jumpValue = genome[acWithOffset / 8][acWithOffset % 8];
            int nextIp = (ip + jumpValue) % 64;*/

            switch (type) {
                case MOVE -> {

                    moveCreature(creature, direction);
                    creature.setActionCounter(acWithOffset);
                    return;

                    /*boolean moved = moveCreature(creature, direction);
                    if (moved) {
                        creature.setActionCounter(*//*nextIp*//*acWithOffset);
                        return;
                    } else {
                        creature.changeActionCounter(1); // пробуем следующую команду
                    }*/
                }

                case INTERACT -> {

                    interact(creature, direction);
                    creature.setActionCounter(acWithOffset);
                    return;

                    /*boolean interacted = interact(creature, direction);
                    if (interacted) {
                        creature.setActionCounter(*//*nextIp*//*acWithOffset);
                        return;
                    } else {
                        creature.changeActionCounter(1); // пробуем следующую команду
                    }*/
                }

                case LOOK -> {

                    //creature.setLastSeen(entityType);
                    creature.setActionCounter(/*nextIp*/acWithOffset);
                }

                case TURN -> {

                    creature.setDirection(creature.getDirection().rotate(genForDir % 8));
                    creature.changeActionCounter(1);
                }

                /*case JUMP -> {
                    creature.setActionCounter(nextIp);
                    return;
                }*/

                /*case REPRODUCE -> {
                    if (creature.getHealth() > 20) {
                        reproduce(creature); // создаёт копию поблизости
                        creature.changeHealth(-10);
                    }
                    creature.setActionCounter(nextIp);
                    return;
                }*/
            }
        }
    }

    public void interact(Creature creature, Direction direction) {
        int newX = creature.getX() + direction.dx();
        int newY = creature.getY() + direction.dy();

        Entity entity = worldMap.getEntityAt(newX, newY);

        if (entity instanceof Food) {
            creature.changeHealth(10);
            creature.changeFoodEaten(1);
            simulation.entitiesToRemove.add(entity);
        } else if (entity instanceof Poison) {
            simulation.entitiesToRemove.add(entity);
            worldMap.addEntity(new Food(newX, newY));
        }
    }

    /*public boolean interact(Creature creature, Direction direction) {
        int newX = creature.getX() + direction.dx();
        int newY = creature.getY() + direction.dy();

        Entity entity = worldMap.getEntityAt(newX, newY);

        if (entity instanceof Food) {
            creature.changeHealth(10);
            creature.changeFoodEaten(1);
            simulation.entitiesToRemove.add(entity);
            return true;
        } else if (entity instanceof Poison) {
            simulation.entitiesToRemove.add(entity);
            worldMap.addEntity(new Food(newX, newY));
            return true;
        }

        return false;
    }*/

    private void moveCreature(Creature creature, Direction direction) {
        int newX = creature.getX() + direction.dx();
        int newY = creature.getY() + direction.dy();

        if (newX >= 0 && newX < worldMap.getWidth() && newY >= 0 && newY < worldMap.getHeight()) {
            encounter(creature, worldMap.getEntityAt(newX, newY), newX, newY);
        }
    }

    /*private boolean moveCreature(Creature creature, Direction direction) {
        int newX = creature.getX() + direction.dx();
        int newY = creature.getY() + direction.dy();

        if (newX < 0 || newX >= worldMap.getWidth() || newY < 0 || newY >= worldMap.getHeight()) {
            return false;
        }

        Entity entity = worldMap.getEntityAt(newX, newY);

        if (entity instanceof Rock || entity instanceof Creature) {
            return false;
        }

        encounter(creature, entity, newX, newY);
        return true;
    }*/

    private void encounter(Creature creature, Entity entity, int newX, int newY) {

        if (entity instanceof Poison) {
            creature.setHealth(0);
            simulation.entitiesToRemove.add(entity);
            creature.setPosition(newX, newY);
            return;
        }

        if (entity instanceof Food) {
            creature.changeHealth(10);
            creature.changeFoodEaten(1);
            simulation.entitiesToRemove.add(entity);
            creature.setPosition(newX, newY);
            return;
        }

        if (entity instanceof Rock) {
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
