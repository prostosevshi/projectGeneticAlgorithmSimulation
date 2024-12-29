package service;

import controller.WorldMap;
import model.Entity;
import movingEntity.Herbivore;
import movingEntity.Predator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PredatorAttackAction implements Action {

    @Override
    public void execute(WorldMap worldMap) {

        List<Herbivore> herbivoreToRemove = new ArrayList<>();

        for (Entity entity : worldMap.getEntities().values()) {

            if (entity instanceof Predator) {
                Predator predator = (Predator) entity;

                for (int dx = -1; dx <= 1; dx++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        if (dx == 0 && dy == 0) {
                            continue;
                        }

                        int targetX = predator.getX() + dx;
                        int targetY = predator.getY() + dy;

                        Entity target = worldMap.getEntity(targetX, targetY);
                        if (target instanceof Herbivore) {
                            Herbivore herbivore = (Herbivore) target;
                            predator.attack(herbivore);

                            if (!herbivore.isAlive()) {
                                herbivoreToRemove.add(herbivore);
                            }
                        }
                    }
                }
            }
        }
        for (Herbivore herbivore : herbivoreToRemove) {
            worldMap.removeEntity(herbivore.getX(), herbivore.getY());
        }
    }
}
