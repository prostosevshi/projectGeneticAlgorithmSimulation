package model;

import controller.WorldMap;

public abstract class Creature extends Entity {

    protected int speed;
    protected int hp;

    public Creature(int x, int y, int hp, int speed) {
        super(x,y);
        this.hp = hp;
        this.speed = speed;
    }

    public abstract void makeMove(WorldMap worldMap);

    /*public abstract void interact(model.Entity e);*/

    public boolean isAlive() {
        return hp > 0;
    }

    @Override
    public String setSymbol() {
        return "C";
    }

    //getters&setters
    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }
}
