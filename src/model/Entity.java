package model;

public abstract class Entity {

    private int x, y;

    public Entity(int x, int y) {
        this.x = x;
        this.y = y;
    }

    //getters&setters

    public abstract String getSymbol();

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

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
