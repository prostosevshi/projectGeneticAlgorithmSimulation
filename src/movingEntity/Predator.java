package movingEntity;

import controller.WorldMap;
import model.Creature;
import model.Entity;

public class Predator extends Creature {

    int attackPower;

    public Predator(int x, int y, int hp, int speed, int attackPower) {
        super(x, y, hp, speed);
        this.attackPower = attackPower;
    }

    @Override
    public String setSymbol() {
        return "P";
    }

    @Override
    public void makeMove(WorldMap worldMap) {
        Herbivore closestHerbivore = null;
        int minDistance = Integer.MAX_VALUE;

        for(Entity entity : worldMap.getEntities().values()) {
            if(entity instanceof Herbivore) {
                int distance = Math.abs(this.getX()-entity.getX())+Math.abs(this.getY()-entity.getY());

                if(distance < minDistance) {
                    minDistance = distance;
                    closestHerbivore = (Herbivore) entity;
                }
            }
        }

        if(closestHerbivore != null) {
            moveTowards(closestHerbivore.getX(), closestHerbivore.getY());
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
    public void update() {

    }

    public void attack(Herbivore herbivore){
        if(herbivore.isAlive()){
            herbivore.setHp(herbivore.getHp()-attackPower);
        }
    }

    /*@Override
    public void interact(model.Entity e) {
        if (e instanceof movingEntity.Herbivore) {
            attack();
        } else {
            makeMove(x, y);
        }
    }

    void attack() {

    }*/
}
