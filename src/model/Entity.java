package model;

public abstract class Entity {

    protected String displaySymbol;
    protected int x, y; // Position on the grid

    public Entity(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Entity() {}

    public abstract String setSymbol();

    public abstract void update();

    //getters&setters
    public String getDisplaySymbol() {
        return displaySymbol;
    }

    public void setDisplaySymbol(String displaySymbol) {
        this.displaySymbol = displaySymbol;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
