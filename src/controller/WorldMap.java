package controller;

import model.Entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WorldMap {

    private final int width;
    private final int height;
    private final List<Entity> entities;

    public WorldMap(int width, int height) {
        this.width = width;
        this.height = height;
        entities = new ArrayList<>();
    }

    public void addEntity(Entity entity) {
        entities.add(entity);
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public void removeEntity(Entity entity) {
        entities.remove(entity);
    }

    public Entity getEntityAt(int x, int y) {
        return entities.stream()
                .filter(e -> e.getX() == x && e.getY() == y)
                .findFirst()
                .orElse(null);
    }

    public void render() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Entity entity = getEntityAt(x, y);
                System.out.print(entity != null ? entity.getSymbol() : '.');
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
}
