package service;

import controller.WorldMap;
import model.Creature;
import model.Entity;

import java.util.HashMap;

public class MoveCreaturesAction implements Action {

    @Override
    public void execute(WorldMap worldMap) {
        HashMap<String, Entity> currentEntities = new HashMap<>(worldMap.getEntities());

        for (Entity entity : currentEntities.values()) {
            if (entity instanceof Creature) {
                Creature creature = (Creature) entity;

                worldMap.removeEntity(creature.getX(), creature.getY());

                creature.makeMove(worldMap);

                worldMap.addEntity(creature);
            }
        }
    }
}
