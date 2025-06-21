package com.example.model.creature;

import com.example.controller.enums.Direction;
import com.example.controller.enums.EntityType;
import com.example.model.Entity;

public class Creature extends Entity {

    protected int health;
    private final int[][] genome;
    private int lifetime = 0;
    private int foodEaten = 0;
    private int actionCounter = 0;
    private Direction direction = Direction.UP;
    //private EntityType lastSeen = EntityType.NOTHING;

    public Creature(int x, int y, int[][] genome) {
        super(x, y);
        this.health = 35; //changed from 10
        this.genome = genome;
    }

    @Override
    public String getSymbol() {
        return String.valueOf(getHealth());
    }

    //getters&setters

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void changeHealth(int delta) {
        if (getHealth() <= 90) {
            this.health += delta;
        } else {
            setHealth(90);
        }
    }

    public int getFoodEaten() {
        return foodEaten;
    }

    public void setFoodEaten(int foodEaten) {
        this.foodEaten = foodEaten;
    }

    public void changeFoodEaten(int delta) {
        this.foodEaten += delta;
    }

    public void changeLifetime(int delta) {
        lifetime += delta;
    }

    public void refreshCreature() {
        setHealth(35);
        setLifetime(0);
        setFoodEaten(0);
        setDirection(Direction.UP);
        setActionCounter(0);
    }

    public int[][] getGenome() {
        return genome;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    /*public EntityType getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(EntityType lastSeen) {
        this.lastSeen = lastSeen;
    }*/

    public int getLifetime() {
        return lifetime;
    }

    public void setLifetime(int lifetime) {
        this.lifetime = lifetime;
    }

    public int getActionCounter() {
        return actionCounter;
    }

    public void setActionCounter(int actionCounter) {
        this.actionCounter = actionCounter % 64; //changing to 32 for science was 64
    }

    public void changeActionCounter(int delta) {
        this.actionCounter = (this.actionCounter + delta) % 64; //changing to 32 for science was 64
    }
}
