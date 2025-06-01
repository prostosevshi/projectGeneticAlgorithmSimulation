package com.example.controller.world;

import com.example.controller.enums.Direction;
import com.example.model.Entity;
import com.example.model.creature.Creature;
import com.example.model.staticEntity.Food;
import com.example.model.staticEntity.Poison;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class WorldMap {

    private static final Logger logger = Logger.getLogger(WorldMap.class.getName());

    private int width;
    private int height;

    private final List<Entity> entities;

    public WorldMap(int width, int height) {
        this.width = width;
        this.height = height;
        entities = new ArrayList<>();
    }

    public void resize(int newWidth, int newHeight) {
        this.width = newWidth;
        this.height = newHeight;
    }

    public void addEntity(Entity entity) {
        entities.add(entity);
    }

    /*public void addEntityAt(Entity entity, int x, int y) {

        if (x >= 0 && x < width && y >= 0 && y < height) {
            entity.setPosition(x, y);
            entities.add(entity);
            logger.fine("Entity added at position: (" + x + ", " + y + ")");
        } else {
            logger.warning("Invalid coordinates (" + x + ", " + y + "). Entity not added.");
        }
    }*/

    public List<Entity> getEntities() {
        return entities;
    }

    public Entity getEntityInDirection(Creature creature, Direction direction) {
        int newX = creature.getX() + direction.dx();
        int newY = creature.getY() + direction.dy();

        if (newX < 0 || newX >= getWidth() || newY < 0 || newY >= getHeight()) {
            return null;
        }

        return getEntityAt(newX, newY);
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
        final String RESET = "\u001B[0m";
        final String GREEN = "\u001B[32m"; // Для существ
        final String YELLOW = "\u001B[33m"; // Для еды
        final String RED = "\u001B[31m"; // Для яда

        // Верхняя граница карты
        System.out.println(" " + "+---".repeat(width) + "+");

        for (int y = 0; y < height; y++) {
            // Линия с данными
            System.out.print("|");
            for (int x = 0; x < width; x++) {
                Entity entity = getEntityAt(x, y);
                if (entity != null) {
                    if (entity instanceof Creature creature) {
                        System.out.printf(GREEN + "%3d" + RESET + "|", creature.getHealth()); // Существо с зеленым цветом
                    } else if (entity instanceof Food) {
                        System.out.print(YELLOW + "  F" + RESET + "|"); // Еда с желтым цветом
                    } else if (entity instanceof Poison) {
                        System.out.print(RED + "  P" + RESET + "|"); // Яд с красным цветом
                    }
                } else {
                    System.out.print("   |"); // Пустая клетка
                }
            }
            System.out.println(); // Переход на новую строку

            // Нижняя граница для текущей строки
            System.out.println(" " + "+---".repeat(width) + "+");
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
