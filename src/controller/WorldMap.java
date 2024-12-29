package controller;

import model.Entity;

import java.util.HashMap;

public class WorldMap {

    private final int width;
    private final int height;
    private final HashMap<String, Entity> grid;

    public WorldMap(int width, int height) {
        this.width = width;
        this.height = height;
        grid = new HashMap<>();
    }

    public boolean addEntity(Entity e) {
        String key = generateKey(e.getX(), e.getY());
        if (isCellFree(e.getX(), e.getY())) {
            grid.put(key, e);
            return true;
        }
        return false;
    }

    public void removeEntity(int x, int y) {
        String key = generateKey(x, y);
        grid.remove(key);
    }

    public boolean isCellFree(int x, int y) {
        return !grid.containsKey(generateKey(x, y));
    }

    private String generateKey(int x, int y) {
        return x + "," + y;
    }

    public Entity getEntity(int x, int y) {
        return grid.get(generateKey(x, y));
    }

    public void render() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Entity e = getEntity(x, y);
                if (e != null) {
                    System.out.print(e.setSymbol());
                } else {
                    System.out.print(".");
                }
            }
            System.out.println();
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public HashMap<String, Entity> getEntities() {
        return grid;
    }
}
