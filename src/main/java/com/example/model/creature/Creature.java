package com.example.model.creature;

import com.example.model.Entity;

public class Creature extends Entity {

    protected int health;
    private int[][] genome;
    private int i, j = 0;
    private int lifetime = 0;

    public Creature(int x, int y, int[][] genome) {
        super(x, y);
        this.health = 10;
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

    public void changeLifetime(int delta) {
        lifetime += delta;
    }

    public int[][] getGenome() {
        return genome;
    }

    public int getI() {
        return i;
    }

    public int getJ() {
        return j;
    }

    public void setI(int i) {
        this.i = i;
    }

    public void setJ(int j) {
        this.j = j;
    }

    public int getLifetime() {
        return lifetime;
    }

    public void setLifetime(int lifetime) {
        this.lifetime = lifetime;
    }
}
