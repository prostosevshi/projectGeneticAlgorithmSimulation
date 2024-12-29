package movingEntity;

import controller.WorldMap;
import model.Creature;
import model.Entity;
import staticEntity.Grass;

public class Herbivore extends Creature {

    public Herbivore(int x, int y, int hp, int speed) {
        super(x, y, hp, speed);
    }

    @Override
    public void makeMove(WorldMap worldMap) {
        Grass closestGrass = null;
        int minDistance = Integer.MAX_VALUE;

        for (Entity entity : worldMap.getEntities().values()) {
            if (entity instanceof Grass) {
                int distance = Math.abs(this.getX() - entity.getX()) + Math.abs(this.getY() - entity.getY());
                if (distance < minDistance) {
                    minDistance = distance;
                    closestGrass = (Grass) entity;
                }
            }
        }

        if (closestGrass != null) {
            moveTowards(closestGrass.getX(), closestGrass.getY());
        }
    }

    private void moveTowards(int targetX, int targetY) {
        if (this.getX() < targetX) {
            this.setX(this.getX() + 1);
        } else if (this.getX() > targetX) {
            this.setX(this.getX() - 1);
        }

        if (this.getY() < targetY) {
            this.setY(this.getY() + 1);
        } else if (this.getY() > targetY) {
            this.setY(this.getY() - 1);
        }
    }

    @Override
    public String setSymbol() {
        return "H";
    }

    @Override
    public void update() {

    }

    /*@Override
    public void interact(model.Entity e) {
        if (e instanceof staticEntity.Grass) {
            eat();
        } else {
            makeMove(x, y);
        }
    }

    void eat() {

    }*/
}
